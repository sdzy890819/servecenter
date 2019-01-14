package com.fdz.content.oss;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.fdz.common.utils.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class OssClientManager {

    private static ClientConfiguration clientConfiguration = new ClientConfiguration();

    static {
        clientConfiguration.setMaxConnections(200);
        clientConfiguration.setSocketTimeout(20000);//毫秒
        clientConfiguration.setConnectionRequestTimeout(5000);
        clientConfiguration.setMaxErrorRetry(4);
    }

    private String accessKeyId;
    private String accessKeySecret;
    private String endpoint;
    private String bucketName;
    private String publicEndpoint;

    public OssClientManager(String accessKeyId, String accessKeySecret, String endpoint, String bucketName, String publicEndpoint) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.endpoint = endpoint;
        this.bucketName = bucketName;
        this.publicEndpoint = publicEndpoint;
    }

    /**
     * 上传图片
     *
     * @param name
     * @param inputStream
     * @return
     */
    public String upload(String name, InputStream inputStream) {

        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret, clientConfiguration);
        ossClient.putObject(bucketName, name, inputStream);
        ossClient.shutdown();

        return installPath(bucketName, publicEndpoint, name);
    }

    public String upload(String name, byte[] bytes) {
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret, clientConfiguration);
        ossClient.putObject(bucketName, name, new ByteArrayInputStream(bytes));
        ossClient.shutdown();
        return installPath(bucketName, publicEndpoint, name);
    }


    public byte[] getObject(String path) {
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret, clientConfiguration);
        String key = uninstallKey(bucketName, endpoint, path);
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        OSSObject ossObject = ossClient.getObject(getObjectRequest);
        InputStream in = ossObject.getObjectContent();
        return StreamUtils.getBytes(in);
    }


    public String installPath(String bucketName, String endpoint, String key) {
        return "http://" + bucketName + "." + endpoint + "/" + key;
    }

    public String uninstallKey(String bucketName, String endpoint, String path) {
        return path.replace("http://" + bucketName + "." + endpoint + "/", "");
    }
}
