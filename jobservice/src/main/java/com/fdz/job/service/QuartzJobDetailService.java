package com.fdz.job.service;

import com.fdz.job.dto.JobDetailDO;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.List;

public interface QuartzJobDetailService {

    List<JobDetailDO> queryJobList();

    JobDetailDO queryByKey(JobKey jobKey);

    boolean add(JobDetailDO jobDetailDO);

    boolean remove(List<JobKey> jobKeyList);

    boolean disable(GroupMatcher<JobKey> matcher);

    boolean disableAll();

    boolean enable(GroupMatcher<JobKey> matcher);

    boolean enableAll();

    boolean triggerNow(JobKey jobKey, JobDataMap jobDataMap);

    JobDetail getJobDetailByKey(JobKey jobKey);

    List<Trigger> getTriggerByKey(JobKey jobKey);

}
