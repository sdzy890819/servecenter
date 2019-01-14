package com.fdz.job.service.impl;

import com.fdz.job.dto.JobDetailDO;
import com.fdz.job.service.QuartzJobDetailService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 定时任务操作
 */
@Service
@Transactional
@Slf4j
public class QuartzJobDetailServiceImpl implements QuartzJobDetailService {

    // SchedulerFactoryBean 创建
    @Autowired
    private Scheduler scheduler;

    // 任务列表
    @Transactional(readOnly = true)
    public List<JobDetailDO> queryJobList() {
        List<JobDetailDO> jobDetailDOs = Lists.newArrayList();

        // 数据处理
        Function<Set<JobKey>, List<JobDetailDO>> copyPropFun = jbst -> {
            List<JobDetailDO> jddList = Lists.newArrayList();
            jddList = jbst.stream().map(jk -> {
                JobDetail jd = null;
                List<Trigger> trList = this.getTriggerByKey(jk);
                jd = this.getJobDetailByKey(jk);

                // jobDetail
                JobDetailDO jobDetailDO = new JobDetailDO();
                jobDetailDO.fillWithQuartzJobDetail(jd);
                jobDetailDO.fillWithQuartzTriggers(trList);
                return jobDetailDO;
            }).collect(Collectors.toList());
            return jddList;
        };

        try {
            Set<JobKey> jobSet = scheduler.getJobKeys(GroupMatcher.anyJobGroup());
            jobDetailDOs = copyPropFun.apply(jobSet);
        } catch (SchedulerException e) {
            log.error("getJobKeys SchedulerException ", e);
        }
        return jobDetailDOs;
    }

    /**
     * 查询指定jobkey jobDetail
     *
     * @param jobKey
     * @return
     */
    @Transactional(readOnly = true)
    public JobDetailDO queryByKey(JobKey jobKey) {
        JobDetailDO jobDetailDO = new JobDetailDO();
        JobDetail jobDetail = this.getJobDetailByKey(jobKey);
        if (Objects.nonNull(jobDetail)) {
            List<Trigger> triggerList = this.getTriggerByKey(jobKey);
            jobDetailDO.fillWithQuartzJobDetail(jobDetail);
            jobDetailDO.fillWithQuartzTriggers(triggerList);
        }
        return jobDetailDO;
    }

    /**
     * 添加任务
     *
     * @param jobDetailDO
     */
    public boolean add(JobDetailDO jobDetailDO) {
        JobDetail jobDetail = jobDetailDO.getJobDO().convert2QuartzJobDetail();
        Set<CronTrigger> triggerSet = jobDetailDO.getTriggerDOs().stream().map(jtd ->
                jtd.convert2QuartzTrigger(jobDetail)
        ).collect(Collectors.toSet());

        // 如果已经存在 则替换
        try {
            scheduler.scheduleJob(jobDetail, triggerSet, true);
            return true;
        } catch (SchedulerException e) {
            log.error("add SchedulerException ", e);
        }
        return false;
    }

    /**
     * 删除任务
     *
     * @param jobKeyList
     */
    public boolean remove(List<JobKey> jobKeyList) {
        try {
            return scheduler.deleteJobs(jobKeyList);
        } catch (SchedulerException e) {
            log.error("remove SchedulerException ", e);
        }
        return false;
    }

    // 停用任务
    public boolean disable(GroupMatcher<JobKey> matcher) {
        try {
            scheduler.pauseJobs(matcher);
            return true;
        } catch (SchedulerException e) {
            log.error("disable SchedulerException ", e);
        }
        return false;
    }

    // 停用所有任务
    public boolean disableAll() {
        try {
            scheduler.pauseAll();
            return true;
        } catch (SchedulerException e) {
            log.error("disableAll SchedulerException ", e);
        }
        return false;
    }

    // 启用任务
    public boolean enable(GroupMatcher<JobKey> matcher) {
        try {
            scheduler.resumeJobs(matcher);
            return true;
        } catch (SchedulerException e) {
            log.error("enable SchedulerException ", e);
        }
        return false;
    }

    // 启用所有任务
    public boolean enableAll() {
        try {
            scheduler.resumeAll();
            return true;
        } catch (SchedulerException e) {
            log.error("enableAll SchedulerException ", e);
        }
        return false;
    }

    // 立即触发任务
    public boolean triggerNow(JobKey jobKey, JobDataMap jobDataMap) {
        try {
            scheduler.triggerJob(jobKey, jobDataMap);
            return true;
        } catch (SchedulerException e) {
            log.error("triggerNow SchedulerException ", e);
        }
        return false;
    }

    /**
     * 根据key 获取jobDetail
     *
     * @param jobKey
     * @return
     */
    @Transactional(readOnly = true)
    public JobDetail getJobDetailByKey(JobKey jobKey) {
        JobDetail jd = null;
        try {
            jd = scheduler.getJobDetail(jobKey);
        } catch (SchedulerException e) {
            log.error("getJobDetailByKey SchedulerException ", e);
        }
        return jd;
    }

    /**
     * 根据key 获取job trigger
     *
     * @param jobKey
     * @return
     */
    public List<Trigger> getTriggerByKey(JobKey jobKey) {
        List<Trigger> triggerList = Lists.newArrayList();
        try {
            triggerList = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
        } catch (SchedulerException e) {
            log.error("getTriggerByKey SchedulerException ", e);
        }
        return triggerList;
    }
}
