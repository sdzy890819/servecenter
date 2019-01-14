package com.fdz.job.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.MessageFormat;

@Component
@Slf4j
public class ServiceJob implements Job {


    @Resource
    private LoadBalancerClient loadBalancerClient;

    @Resource
    private RestTemplate restTemplate;


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            JobKey jobKey = context.getJobDetail().getKey();
            String uniqueKey = MessageFormat.format("{0}[{1}]", jobKey.getGroup(), jobKey.getName());
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            String serviceId = String.valueOf(jobDataMap.get("serviceId"));
            String method = String.valueOf(jobDataMap.get("method"));
            String path = String.valueOf(jobDataMap.get("path"));
            log.info("serviceId :{}, method :{}, path :{}, 执行开始", serviceId, method, path);
            ServiceInstance instance = loadBalancerClient.choose(serviceId);
            if (instance != null) {
                String url = "http://" + instance.getHost() + ":" + instance.getPort() + path;
                if (HttpMethod.valueOf(method) == HttpMethod.GET) {
                    ResponseEntity<String> stringResponseEntity = restTemplate.getForEntity(url, String.class);
                    log.info("serviceId :{}, method :{}, path :{}, 返回code : {}, 返回数据 : {}", serviceId, method, path, stringResponseEntity.getStatusCodeValue(), stringResponseEntity.getBody());
                    if (stringResponseEntity.getStatusCode() == HttpStatus.OK) {
                        log.info("执行成功, 返回数据: {}", stringResponseEntity.getBody());
                    } else {
                        log.info("执行失败, code : {}, 返回数据 : {}", stringResponseEntity.getStatusCodeValue(), stringResponseEntity.getBody());
                    }
                }
            } else {
                log.info("serviceId :{}, method :{}, path :{}, 执行失败，未找到服务", serviceId, method, path);
            }
        } catch (Exception e) {
            log.error("Job 执行失败, message : ", e);
        }
    }
}
