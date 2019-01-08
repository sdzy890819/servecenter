package com.fdz.common.dto;

import lombok.Data;

@Data
public class PageDto {

    private Integer page = 1;

    private Integer pageSize = 100;
}
