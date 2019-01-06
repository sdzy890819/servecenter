package com.fdz.content.convert;

import com.fdz.content.domain.Partner;
import com.fdz.content.dto.PartnerDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DtoConvert {

    Partner convert(PartnerDto partnerDto);

}
