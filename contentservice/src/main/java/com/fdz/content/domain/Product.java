package com.fdz.content.domain;

import com.fdz.common.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
public class Product extends BaseEntity {

    private String productName;

    private String productTypeNo;

    private String productDescription;

    private String productCoverImage;

    private BigDecimal primeCosts;

    private BigDecimal salePrice;

    private Boolean status;

    private String productModel;

    public Product() {
        super();
    }

    public Product(Long id) {
        super(id);
    }

    /**
     * 伪字段
     */
    private String productTypeName;

    private String createTimeStr;

    private List<ProductImage> productImages;

    public String getCreateTimeStr() {
        if (this.getCreateTime() != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.format(new Date(this.getCreateTime()));
        }
        return "";
    }
}