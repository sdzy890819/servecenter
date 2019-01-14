package com.fdz.job.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("测试DTO")
@Data
public class TestDto {

    private Long id;

    private String name;
}
