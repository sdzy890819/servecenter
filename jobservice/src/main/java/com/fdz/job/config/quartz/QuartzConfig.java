package com.fdz.job.config.quartz;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * Quartz配置类
 */
@Configuration
public class QuartzConfig {
    private static final Logger log = LoggerFactory.getLogger(QuartzConfig.class);

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private AutowiringQuartzJobFactory autowiringQuartzJobFactory;

    @Value("${config.quartz-file-name}")
    private String path;

    @PostConstruct
    public void initDone() {
        log.info("Quartz init done...");
    }

    @Bean
    public DataSource dataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    public SchedulerFactoryBean init() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setTransactionManager(platformTransactionManager);
        schedulerFactoryBean.setConfigLocation(new ClassPathResource(path));
        schedulerFactoryBean.setDataSource(dataSource());
        schedulerFactoryBean.setAutoStartup(true);
        // 覆盖已存在定时任务
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(false);
        schedulerFactoryBean.setJobFactory(autowiringQuartzJobFactory);
        return schedulerFactoryBean;
    }


}
