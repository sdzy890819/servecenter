package com.fdz.job.dto;

import com.google.common.base.Strings;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.text.ParseException;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * 触发器
 */
@Slf4j
public class TriggerDO {

    // trigger info
    @ApiModelProperty(value = "触发器名称")
    private String name;

    @ApiModelProperty(value = "触发器所属组")
    private String group;

    @ApiModelProperty(value = "触发器cron表达式")
    private String cronExpression;

    @ApiModelProperty(value = "触发器描述")
    private String description;

    public CronTrigger convert2QuartzTrigger(JobDetail jobDetail) {
        CronExpression ce = null;
        try {
            checkArgument(!Strings.isNullOrEmpty(cronExpression), "cronExpression参数非法");
            ce = new CronExpression(this.cronExpression);
        } catch (ParseException e) {
            log.error("TriggerDO convert2QuartzTrigger ParseException error", e);
        }
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withSchedule(CronScheduleBuilder.cronSchedule(ce))
                .withIdentity(this.name, this.group)
                .withDescription(this.description)
                .build();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TriggerDO{");
        sb.append("name='").append(name).append('\'');
        sb.append(", group='").append(group).append('\'');
        sb.append(", cronExpression='").append(cronExpression).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
