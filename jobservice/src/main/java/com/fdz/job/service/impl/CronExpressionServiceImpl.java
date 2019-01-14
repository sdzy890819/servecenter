package com.fdz.job.service.impl;

import com.fdz.job.dto.JobDO;
import com.fdz.job.dto.JobDetailDO;
import com.fdz.job.dto.PushJobDto;
import com.fdz.job.dto.TriggerDO;
import com.fdz.job.service.CronExpressionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author yanglongfei
 */
@Service
@Slf4j
public class CronExpressionServiceImpl implements CronExpressionService {

    private static final String JOB_GROUP = "http_job_group";

    private static final String JOB_NAME_PREFIX = "job_";

    private static final String JOB_TARGET_CLASS = "com.ouding.wallet.job.ServiceJob";

    private static final String JOB_DESC = "定时任务";

    private static final String JOB_TYPE = "http_job";

    private static final String TRIGGER_GROUP = "http_trigger_group";

    private static final String TRIGGER_NAME_PREFIX = "trigger_";

    private static final String TRIGGER_DESC = "触发器";

    /**
     * pushDto转换为jobCron表达式
     *
     * @param pushJobDto
     * @return
     */
    public JobDetailDO genCronExpressionByPushJobDto(PushJobDto pushJobDto) {
        //生成jobDO
        JobDO jobDO = buildJobDOByPushDto(pushJobDto);

        //生成triggerSet
        Set<TriggerDO> triggerSet = buildTriggerSetByPushDto(pushJobDto);

        //生成JobDetailDO
        JobDetailDO jobDetailDO = new JobDetailDO();
        jobDetailDO.setJobDO(jobDO);
        jobDetailDO.setTriggerDOs(triggerSet);

        return jobDetailDO;
    }

    private Set<TriggerDO> buildTriggerSetByPushDto(PushJobDto pushJobDto) {
        TriggerDO triggerDO = new TriggerDO();
        triggerDO.setDescription(pushJobDto.getTaskName() + TRIGGER_DESC);
        triggerDO.setGroup(TRIGGER_GROUP);
        triggerDO.setName(TRIGGER_NAME_PREFIX + pushJobDto.getId());
        String cronExpression = buildCronExpressionByJobDto(pushJobDto);
        triggerDO.setCronExpression(cronExpression);

        Set<TriggerDO> triggerSet = new HashSet<>();
        triggerSet.add(triggerDO);

        return triggerSet;
    }

    private String buildCronExpressionByJobDto(PushJobDto pushJobDto) {
        StringBuilder cronBuilder = new StringBuilder("");
        LocalTime pushStartTime = pushJobDto.getPushStartTime();
        cronBuilder.append(pushStartTime.getSecond()).append(" ");
        cronBuilder.append(pushStartTime.getMinute()).append(" ");
        cronBuilder.append(pushStartTime.getHour()).append(" ");
        LocalDate pushStartDate = pushJobDto.getPushStartDate();
        String dayOfWeek = pushJobDto.getCyclicDay() == null ? "" : pushJobDto.getCyclicDay() == 7 ? "L" : String.valueOf(pushJobDto.getCyclicDay() + 1);
        switch (pushJobDto.getPushFrequency()) {
            case NON_REPEAT:
                cronBuilder.append(pushStartDate.getDayOfMonth()).append(" ").append(pushStartDate.getMonthValue()).append(" ").append("? ").append(pushStartDate.getYear()).append("-").append(pushStartDate.getYear());
                break;
            case PER_DAY:
                cronBuilder.append("* * ?");
                break;
            case PER_WEEK:
                cronBuilder.append("? * ").append(dayOfWeek);
                break;
            case PER_TWO_WEEK:
                cronBuilder.append("? * ").append(dayOfWeek).append("/2");
                break;
            case PER_MONTH:
                String day = LocalDate.now().getMonth().maxLength() - pushJobDto.getCyclicDay() < 0 ? "L" : String.valueOf(pushJobDto.getCyclicDay());
                cronBuilder.append(day).append(" * ?");
                break;
            default:
                throw new RuntimeException("不可达分支");
        }
        return cronBuilder.toString();
    }

    private JobDO buildJobDOByPushDto(PushJobDto pushJobDto) {
        JobDO jobDO = new JobDO();
        jobDO.setName(JOB_NAME_PREFIX + pushJobDto.getId());
        jobDO.setGroup(JOB_GROUP);
        jobDO.setTargetClass(JOB_TARGET_CLASS);
        jobDO.setDescription(pushJobDto.getTaskName() + JOB_DESC);

        Map<String, Object> extraInfo = new HashMap<>();
        extraInfo.put("method", "get");
        extraInfo.put("jsonParams", "");
        extraInfo.put("type", JOB_TYPE);
        String url = new StringBuilder("http://localhost:37114/jobs/push/").append("?id=").append(pushJobDto.getId()).toString();
        extraInfo.put("url", url);
        jobDO.setExtInfo(extraInfo);

        return jobDO;
    }
}
