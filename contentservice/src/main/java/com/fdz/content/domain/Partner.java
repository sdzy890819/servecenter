package com.fdz.content.domain;

import com.fdz.common.domain.BaseEntity;
import com.fdz.common.enums.NatureEnums;
import lombok.Data;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

@Data
public class Partner extends BaseEntity {

    private String name;

    private String shortName;

    private Byte nature;

    private String code;

    private String contacts;

    private String contactMobile;

    private BigDecimal serviceRate;

    private String publicKey;

    private String uniqueKey;

    private String myKey;

    private String myPublicKey;

//    private boolean encode;

    public Partner() {
        super();
    }

    public Partner(Long id) {
        super(id);
    }

    private String natureStr;

    public String getNatureStr() {
        if (nature != null) {
            return NatureEnums.get(nature).getText();
        }
        return "";
    }

    private String createTimeStr;

    public String getCreateTimeStr() {
        if (this.getCreateTime() != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.format(getCreateTime());
        }
        return "";
    }
}