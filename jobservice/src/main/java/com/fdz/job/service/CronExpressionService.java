package com.fdz.job.service;


import com.fdz.job.dto.JobDetailDO;
import com.fdz.job.dto.PushJobDto;

public interface CronExpressionService {

    JobDetailDO genCronExpressionByPushJobDto(PushJobDto pushJobDto);

}
