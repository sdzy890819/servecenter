package com.fdz.content.convert;

import com.fdz.common.dto.PageDto;
import com.fdz.common.utils.Page;
import com.fdz.content.domain.*;
import com.fdz.content.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DtoConvert {

    Partner convert(PartnerDto partnerDto);

    Page convert(PageDto pageDto);

    @Mapping(ignore = true, source = "productImages", target = "productImages")
    Product convert(ProductDto dto);

    Product convert(SearchProductDto dto);

    PartnerProduct convert(SearchPartnerProductDto dto);

    PartnerProduct convert(PartnerProductDto dto);

    ProductResult convert(Product product);

    List<ProductResult> convert(List<Product> products);

    ProductType convert(ProductTypeDto dto);

    PartnerUser convert(PartnerUserDto dto);
}
