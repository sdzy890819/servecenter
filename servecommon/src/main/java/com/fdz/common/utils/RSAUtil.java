package com.fdz.common.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * 加解密工具类
 * <p>
 * 使用方法见junit测试
 */
@Slf4j
public class RSAUtil {
    private static final String CHARSET = "UTF-8";
    //密钥算法
    private static final String ALGORITHM_RSA = "RSA";
    private static final String ALGORITHM_RSA_SIGN = "SHA256WithRSA";
    public static final Integer ALGORITHM_RSA_PRIVATE_KEY_LENGTH = 2048;
    public static final String ALGORITHM_AES = "AES";
    public static final String ALGORITHM_AES_PKCS7 = "AES";
    public static final String ALGORITHM_DES = "DES";
    public static final String ALGORITHM_DE_SEDE = "DESede";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PUBLIC_KEY = "publicKey";

    /**
     * 初始化RSA算法密钥对
     *
     * @param keysize RSA1024已经不安全了,建议2048
     * @return 经过Base64编码后的公私钥Map, 键名分别为publicKey和privateKey
     */
    public static Map<String, String> initRSAKey(int keysize) {
        if (keysize != ALGORITHM_RSA_PRIVATE_KEY_LENGTH) {
            throw new IllegalArgumentException("RSA1024已经不安全了,请使用" + ALGORITHM_RSA_PRIVATE_KEY_LENGTH + "初始化RSA密钥对");
        }
        //为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance(ALGORITHM_RSA);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm-->[" + ALGORITHM_RSA + "]");
        }
        //初始化KeyPairGenerator对象,不要被initialize()源码表面上欺骗,其实这里声明的size是生效的
        kpg.initialize(ALGORITHM_RSA_PRIVATE_KEY_LENGTH);
        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        //得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
        //得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
        Map<String, String> keyPairMap = new HashMap<String, String>();
        keyPairMap.put(PUBLIC_KEY, publicKeyStr);
        keyPairMap.put(PRIVATE_KEY, privateKeyStr);
        return keyPairMap;
    }

    /**
     * RSA算法公钥加密数据
     *
     * @param data 待加密的明文字符串
     * @param key  RSA公钥字符串
     * @return RSA公钥加密后的经过Base64编码的密文字符串
     */
    public static String buildRSAEncryptByPublicKey(String data, String key) {
        try {
            //通过X509编码的Key指令获得公钥对象
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            Key publicKey = keyFactory.generatePublic(x509KeySpec);
            //encrypt
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            //return Base64.encodeBase64URLSafeString(cipher.doFinal(data.getBytes(CHARSET)));
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET)));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * RSA算法私钥解密数据
     *
     * @param data 待解密的经过Base64编码的密文字符串
     * @param key  RSA私钥字符串
     * @return RSA私钥解密后的明文字符串
     */
    public static String buildRSADecryptByPrivateKey(String data, String key) {
        try {
            //通过PKCS#8编码的Key指令获得私钥对象
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            //decrypt
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            //return new String(cipher.doFinal(Base64.decodeBase64(data)), CHARSET);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data)), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * RSA算法使用私钥对数据生成数字签名
     *
     * @param data 待签名的明文字符串
     * @param key  RSA私钥字符串
     * @return RSA私钥签名后的经过Base64编码的字符串
     */
    public static String buildRSASignByPrivateKey(String data, String key) {
        try {
            //通过PKCS#8编码的Key指令获得私钥对象
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            //sign
            Signature signature = Signature.getInstance(ALGORITHM_RSA_SIGN);
            signature.initSign(privateKey);
            signature.update(data.getBytes(CHARSET));
            return Base64.encodeBase64URLSafeString(signature.sign());
        } catch (Exception e) {
            throw new RuntimeException("签名字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * RSA算法使用公钥校验数字签名
     *
     * @param data 参与签名的明文字符串
     * @param key  RSA公钥字符串
     * @param sign RSA签名得到的经过Base64编码的字符串
     * @return true--验签通过,false--验签未通过
     */
    public static boolean buildRSAverifyByPublicKey(String data, String key, String sign) {
        try {
            //通过X509编码的Key指令获得公钥对象
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
            //verify
            Signature signature = Signature.getInstance(ALGORITHM_RSA_SIGN);
            signature.initVerify(publicKey);
            signature.update(data.getBytes(CHARSET));
            return signature.verify(Base64.decodeBase64(sign));
        } catch (Exception e) {
            throw new RuntimeException("验签字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * RSA算法分段加解密数据
     *
     * @param cipher 初始化了加解密工作模式后的javax.crypto.Cipher对象
     * @param opmode 加解密模式,值为javax.crypto.Cipher.ENCRYPT_MODE/DECRYPT_MODE
     * @return 加密或解密后得到的数据的字节数组
     */
    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas) {
        int maxBlock = 0;
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = ALGORITHM_RSA_PRIVATE_KEY_LENGTH / 8;
        } else {
            maxBlock = ALGORITHM_RSA_PRIVATE_KEY_LENGTH / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try {
            while (datas.length > offSet) {
                if (datas.length - offSet > maxBlock) {
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        } catch (Exception e) {
            throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        IOUtils.closeQuietly(out);
        return resultDatas;
    }


    /**
     * 验签
     *
     * @param data
     * @param signMsg
     * @param facebankPublicKey
     * @return
     */
    public static boolean inspectionSign(String data, String signMsg, String facebankPublicKey) {
        return buildRSAverifyByPublicKey(data, facebankPublicKey, signMsg);
    }

    /**
     * 解密内容
     *
     * @param data
     * @param jjjPrivateKey
     * @return
     */
    public static String resultAnalysis(String data, String jjjPrivateKey) {
        log.info("未解密结果:{}", data);
        String decData = buildRSADecryptByPrivateKey(data, jjjPrivateKey);
        log.info("解密结果:{}", decData);
        return decData;
    }

//    public static void main(String[] args) throws JsonProcessingException {
//        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA26Su0kli1_02b8q9qg-qnGaBqGrMQh2bco034h2tLBID9hUvo-i5JqutXka5ZErh2CjkxQ8x_7rBdeCJu8a3Fd3fmpBPg5zwgxzTr7SWYyeRu_KFbIP8ZksjSOcJ0uHrS5nKu81DeQntA9KiuuG4Yov8arIfMXyLlxgGQVdPCI4ruTCY3mdnfdPsWTle34Fq3jqx3bgxzrCcXtvPDsSZ9qG54Idj1z9DYWUzjRRPLt8EUjGggXxQERiDAjDGm06fDf_JnM48MBOiba8rDyngM5HXyvVEgJxocE9P1wkjrWV81leoQ5QQVPu98-iKwKx1Hd94xkcTtbeMfA8kHSNoZQIDAQAB";
//        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDbpK7SSWLX_TZvyr2qD6qcZoGoasxCHZtyjTfiHa0sEgP2FS-j6Lkmq61eRrlkSuHYKOTFDzH_usF14Im7xrcV3d-akE-DnPCDHNOvtJZjJ5G78oVsg_xmSyNI5wnS4etLmcq7zUN5Ce0D0qK64bhii_xqsh8xfIuXGAZBV08Ijiu5MJjeZ2d90-xZOV7fgWreOrHduDHOsJxe288OxJn2obngh2PXP0NhZTONFE8u3wRSMaCBfFARGIMCMMabTp8N_8mczjwwE6JtrysPKeAzkdfK9USAnGhwT0_XCSOtZXzWV6hDlBBU-73z6IrArHUd33jGRxO1t4x8DyQdI2hlAgMBAAECggEBALC90-9jHiavmx5aBkTSVneEBNGBHtwU9wE2ocFSIAdWd9mNMsiyfEqh2uACCSyrFRDb1zq6_4DIxuQTysFPJgRyhXvapCFEKEVY7P52Uo13Sc3zWWn4yyGFVN9VCpKnOK-dEYYNpj2_vvlUhZ3cBINIIdW5f2BS2AC9VEs_9ljqkCAIZCyhwUYOvJ89_4yS3CFXXQXZNxPebxXAhzkKEGNCL_irDy1CuZo1dK7n6NKXo5CPaRQzoyhiM_1w61RFhD5mD-zGuhNs8QjxNP3AywHTRpgGHfLB-0HI8l2-D7cb5Z6TuVduJBJeYPB0TpcJ7enuEBuyVQ9jr6WSA2iRqAECgYEA-c36oEMtS_386mtDXeZv0U3iB3tdw0cNO3e66Jbkwwj_EctbvUx9eGg-iCDztmQh2Kf2AuwmlnircExAm75ATuJOmctYu0DdIbOwPt9mGt-X4A7reix2uvlFWXvcpO0o-SXnl1R8reTIWYRfgapRyqZES-1KX5Xbqr_e1wqree8CgYEA4Rc1VE9kcptpoqNsiLBHxW1M3iA500gntrUwSyjgudCm6ak7xI9UjkAReJrmsfvS90aRZN3k_X-LsVB6ONGiY2nu8Ue3zOyJvkpLq6XfYPc_NVl9uPcZVnTYABwucv9nP61c_6MYBxiTw74Iur2xAsJYGOqvG5KI71wuPPFvJusCgYAd953XV_e5JBERNijwvNIxM7yn1R7cbEukFKrxeBidZRjkU5b_0ItExIyr2_ggq0Z8LToQA7BlYLiAUkV16Y7hZ7iyLnjIjfF5N4svH7GqC6S2llOdZnQf3gN5xqgjiOffa7KqrtH-MnHuezjR31LMksGisia37MxbYYUkGv2TNQKBgFyuerX5bsnF9kwScHkj5JmFNNZtfIHjvv57QqRf85BvOpsRpNt9jPp5pPf9CCqXHznUI7_dohFDOFjNY1YL84ptrwZIWoUVjG3_F4NY7E53393uuFa14Af6WopJEFMnmPLeRmG4XQdm9kjt8Zg6zSzuqumvkGSRBLNSSHOtke_7AoGBAMMP1zb6SitDGIJ95Cvd9uoy5L2sOTWVnak93S63x-Md8cY1pjLL3q9tWaGJMdtcLCZGTFvVtZgXv8aYyINJfEEkm6CVZzGial2S9knn0IJg_GqUREDC-NNA3fNWaUZTQlqNGvJBaaaarulzRUzE6z522IRa6GKX2_nCLbjfosxG";
//        String thirdPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAheduBGUeYnEp34Vwa8KJ7XLDPvnxrdcXLsbtvPd1v0TV5ocd8mI60mGp9XjI4uMkWAXfsZbJLBgdESyIHG3wZ0LDwCztQdn2V521_isg1T_pPwzWHjRow2opJsRwew1o2duTS8xwBAxcnswciG19s9UmWn_wqhdxxY-hjOBY2inDj4NKuR2f8est_1_W4HG5LTVE7dgkTTpia4Wzg6u1W7_GIvPCaCX2bB1BEPhfQsLzDPF3k4DwRLD7OYl2-lkuuyOhFdk2tK297cG7S2mnd731C_RGNnaEySrzJnyR3cPeaXQHJiss8lHv7pnvljmgn1Gst-FEkRc4TqsLfrOwYQIDAQAB";
//        String thirdPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCF524EZR5icSnfhXBrwontcsM--fGt1xcuxu2893W_RNXmhx3yYjrSYan1eMji4yRYBd-xlsksGB0RLIgcbfBnQsPALO1B2fZXnbX-KyDVP-k_DNYeNGjDaikmxHB7DWjZ25NLzHAEDFyezByIbX2z1SZaf_CqF3HFj6GM4FjaKcOPg0q5HZ_x6y3_X9bgcbktNUTt2CRNOmJrhbODq7Vbv8Yi88JoJfZsHUEQ-F9CwvMM8XeTgPBEsPs5iXb6WS67I6EV2Ta0rb3twbtLaad3vfUL9EY2doTJKvMmfJHdw95pdAcmKyzyUe_ume-WOaCfUay34USRFzhOqwt-s7BhAgMBAAECggEAYg3p43AEwwOpLRQzEcYgaG_Mh_ZRwwIp6MglWWZApDKNDEXPaSoDdwGriqTLPlIk0AxlFU-cuxxhVK0uctsS2xrp63U6vCkXGmvT1G7SmTY24EPPG8k3GjpILipVT9au_DGteCkZse03edVv43uCPEkOz-C2ZGfMmWYnX9j9V6uDQ7jwe4AK0_QeuVTTr44d6xkWbZgY-VEFm7vRrxLz6Wr8t0Oh_awmba4vEy4DyAz3_yzaUqWkyvST1C1RZ3r5Q7JfzTZpdrzSWjohdGRrWdLVLJmG3vXu9ix4wzwHhP9vqcUM4Vc-0blZonhK2g3B3vjz2-iWIQbBloOfcKDCzQKBgQDLKMWnpkHnU9-kdW59FQzngBqHTB041Qu2TkEOc8-KhsvSkCgvxJY-5JdOaKzY-13Bms5BZvetDM19BxkiQEQW3sJannBHzNrB6xOPlA6PTcVaTn6JwH8kxb02z-F93K_jfRsvDjiIc7cdgpiZW1WswpvID9US2xfSTY2KrbJM4wKBgQCou1ebKnRfPZnAl5MLhDap80RUaeoKT3RnM41-WI7qYsLq5KNVplx1cXfD460knhh1YLjEhfjHoRiBuBAk_bLJwzxhXqQ1xtcnx0ZOwpBep-cwGAGol9iAFpI-hNmoEbSVL4uQmGtTXsNjPUWCBegWS_WALKFPWGAYkdnAh0M06wKBgDPNfrDkBPP-gA1IYFcGshappJoRP3ZJZMYkqBQOVcJl4r0bQxQDFaiN00MMD974IY0Hc0ZcAhWfKRqTX8ZLCtGmFXrT4Z99Td6amY9H2nqSAxum3j8z1XfD8B503XOhfTwNn4vdHW81ymB893pLPsphiSV3XAaF1Uoruka9SbSvAoGACW7v5uzotKps_id7QRmTNM009MM3InxutZZcQsQQuWnsG9XMi-1RHf_KAlmoRHPSSj2uFs6mqgKHrDjuLiNA66bOqWLxK1CClua9N4oJKEGa20V8UkCrCf45DuW-hXb38Z9G3j6nQXH62oGCcVKDNzvn7dKQ8i6usLD2y5zEVSECgYEAhAxhE0PSuP8g0dfut-iW7q-Hc4MJRgAKfUzCvm5ABq3MeM2ko8223-XAUpzeokPPZeaQJDfwBRHO2pCnpepiNXDVsL10R-CA0dmV_a3ghD4ULH6VJxCwKp6px3jDjuUJKxWyn4bvTv6ImvOwQhlUFaitzTZqZr36FbbkUgEpmXc";
//        ObjectMapper objectMapper = new ObjectMapper();
//        Map<String, Object> map = new HashMap<>();
//        map.put("page", 1);
//        map.put("pageSize", 100);
//        String encodeData = RSAUtil.buildRSAEncryptByPublicKey(objectMapper.writeValueAsString(map), publicKey);
//        String sign = RSAUtil.buildRSASignByPrivateKey(encodeData, thirdPrivateKey);
//        System.out.println("加密串: " + encodeData);
//        System.out.println("签名: " + sign);
//        System.out.println("替换后" + ("content.abc.dff".replaceAll("\\.", "/")));
//    }
}

