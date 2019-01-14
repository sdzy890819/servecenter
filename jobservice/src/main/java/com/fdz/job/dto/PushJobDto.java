package com.fdz.job.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fdz.job.enums.PushFrequencyEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class PushJobDto {

    @ApiModelProperty(value = "任务id")
    @NotNull(message = "任务id不能为空")
    private Long id;

    @ApiModelProperty(value = "任务名称")
    @NotNull(message = "任务名称不能为空")
    private String taskName;

    @ApiModelProperty(value = "push任务开始日期-年月日")
    @NotNull(message = "推送日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pushStartDate;

    @ApiModelProperty(value = "推送频率")
    @NotNull(message = "推送频率不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private PushFrequencyEnum pushFrequency = PushFrequencyEnum.NON_REPEAT;

    @ApiModelProperty(value = "循环日")
    private Integer cyclicDay;

    @ApiModelProperty(value = "push任务推送时间-时分秒")
    @NotNull(message = "推送时间不能为空")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime pushStartTime;
}
