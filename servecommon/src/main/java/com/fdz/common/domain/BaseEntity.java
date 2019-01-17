package com.fdz.common.domain;

import com.fdz.common.security.SecurityUtils;
import com.fdz.common.utils.StringUtils;
import lombok.Data;

import java.util.Date;

@Data
public class BaseEntity {

    private Long id;

    private Date createTime;

    private Date modifyTime;

    private String createBy;

    private String modifyBy;

    private String remark;

    private Boolean delete;

    public BaseEntity() {
        createTime = new Date();
        modifyTime = new Date();
        createBy = SecurityUtils.getCurrentLoginUserIdByPartner();
        if (StringUtils.isNotBlank(createBy)) {
            createBy = SecurityUtils.getCurrentLoginUserIdByManager();
        }
        modifyBy = SecurityUtils.getCurrentLoginUserIdByPartner();
        if (StringUtils.isNotBlank(modifyBy)) {
            modifyBy = SecurityUtils.getCurrentLoginUserIdByManager();
        }
        delete = false;
    }

    public BaseEntity(Long id) {
        this.id = id;
        modifyTime = new Date();
        modifyBy = SecurityUtils.getCurrentLoginUserIdByPartner();
        if (StringUtils.isNotBlank(modifyBy)) {
            modifyBy = SecurityUtils.getCurrentLoginUserIdByManager();
        }
    }


}
