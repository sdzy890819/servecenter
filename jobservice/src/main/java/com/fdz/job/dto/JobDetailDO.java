package com.fdz.job.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.quartz.*;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 任务抽象业务类
 */
@ApiModel(value = "JobDetailDO", description = "Quartz JodDetail 等价类")
public class JobDetailDO {

    @ApiModelProperty
    // job info
    private JobDO jobDO;

    // trigger info
    @ApiModelProperty
    private Set<TriggerDO> triggerDOs;

    // 处理job
    public void fillWithQuartzJobDetail(JobDetail jd) {
        jobDO = new JobDO();
        // job
        JobKey jk = jd.getKey();
        // name group desc
        BeanUtils.copyProperties(jk, jobDO);
        jobDO.setDescription(jd.getDescription());
        jobDO.setTargetClass(jd.getJobClass().getCanonicalName());

        // ext
        JobDataMap jdm = jd.getJobDataMap();
        if (Objects.nonNull(jdm)) {
            jobDO.setExtInfo(jdm.getWrappedMap());
        }

        this.setJobDO(jobDO);
    }

    ;

    // 处理triggers
    public void fillWithQuartzTriggers(List<Trigger> trList) {

        // triggers
        Set<TriggerDO> tdSet = trList.stream().map(tr -> {
            TriggerDO td = new TriggerDO();
            if (tr instanceof CronTrigger) {
                CronTrigger ctr = (CronTrigger) tr;
                td.setCronExpression(ctr.getCronExpression());
            }
            TriggerKey trk = tr.getKey();
            td.setName(trk.getName());
            td.setGroup(trk.getGroup());
            td.setDescription(tr.getDescription());
            return td;
        }).collect(Collectors.toSet());
        this.setTriggerDOs(tdSet);
    }

    ;

    public JobDO getJobDO() {
        return jobDO;
    }

    public void setJobDO(JobDO jobDO) {
        this.jobDO = jobDO;
    }

    public Set<TriggerDO> getTriggerDOs() {
        return triggerDOs;
    }

    public void setTriggerDOs(Set<TriggerDO> triggerDOs) {
        this.triggerDOs = triggerDOs;
    }

}
