package com.fdz.content.convert;

import com.fdz.common.dto.PageDto;
import com.fdz.common.utils.Page;
import com.fdz.content.domain.Partner;
import com.fdz.content.domain.PartnerProduct;
import com.fdz.content.domain.Product;
import com.fdz.content.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DtoConvert {

    Partner convert(PartnerDto partnerDto);

    Page convert(PageDto pageDto);

    Product convert(ProductDto dto);

    Product convert(SearchProductDto dto);

    PartnerProduct convert(SearchPartnerProductDto dto);

    PartnerProduct convert(PartnerProductDto dto);

    ProductResult convert(Product product);

    List<ProductResult> convert(List<Product> products);
}
