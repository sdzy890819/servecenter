package com.fdz.job.dto;

import com.fdz.job.job.ServiceJob;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.ApiModelProperty;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.core.jmx.JobDataMapSupport;
import org.slf4j.Logger;
import org.springframework.util.ClassUtils;

import java.util.Map;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 作业DO
 */
public class JobDO {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(JobDO.class);
    private static final Map<String, Class<? extends Job>> SUPPORTED_JOB_TYPES = ImmutableMap.of("service_job", ServiceJob.class);


    // job info
    @ApiModelProperty(value = "job名称")
    private String name;

    @ApiModelProperty(value = "job所属组")
    private String group;

    @ApiModelProperty(value = "job实现类的完全包名,quartz就是根据这个路径到classpath找到该job类，")
    private String targetClass;

    @ApiModelProperty(value = "job 描述")
    private String description;

    // ext info
    // supportExtFields
    @ApiModelProperty(value = "拓展字段", dataType = "Map[String,Object]")
    private Map<String, Object> extInfo;

    public JobDetail convert2QuartzJobDetail() {
        Class<? extends Job> clazz = null;

        // 如果未定义 则根据extInfo里type获取默认处理类
        if (Objects.isNull(this.targetClass)) {
            String type = String.valueOf(this.extInfo.get("type"));
            clazz = SUPPORTED_JOB_TYPES.get(type);
            checkNotNull(clazz, "未找到匹配type的Job");
            this.targetClass = clazz.getCanonicalName();
        }
        try {
            clazz = (Class<Job>) ClassUtils.resolveClassName(this.targetClass, this.getClass().getClassLoader());
        } catch (IllegalArgumentException e) {
            log.error("加载类错误", e);
        }

        return JobBuilder.newJob()
                .ofType(clazz)
                .withIdentity(this.name, this.getGroup())
                .withDescription(this.description)
                .setJobData(JobDataMapSupport.newJobDataMap(this.extInfo))
                .build();
    }

    public Map<String, Object> getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(Map<String, Object> extInfo) {
        this.extInfo = extInfo;
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

    public String getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JobDO{");
        sb.append("name='").append(name).append('\'');
        sb.append(", group='").append(group).append('\'');
        sb.append(", targetClass='").append(targetClass).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", extInfo=").append(extInfo);
        sb.append('}');
        return sb.toString();
    }
}
