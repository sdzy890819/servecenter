package com.fdz.common.domain;

import com.fdz.common.security.SecurityUtils;
import com.fdz.common.utils.StringUtils;
import lombok.Data;

import java.util.Date;

@Data
public class BaseEntity {

    private Long id;

    private Long createTime;

    private Long modifyTime;

    private String createBy;

    private String modifyBy;

    private String remark;

    private Boolean delete;

    public BaseEntity() {
        createTime = new Date().getTime();
        modifyTime = new Date().getTime();
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
        modifyTime = new Date().getTime();
        modifyBy = SecurityUtils.getCurrentLoginUserIdByPartner();
        if (StringUtils.isNotBlank(modifyBy)) {
            modifyBy = SecurityUtils.getCurrentLoginUserIdByManager();
        }
    }


}
