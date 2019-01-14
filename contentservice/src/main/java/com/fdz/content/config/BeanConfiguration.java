package com.fdz.content.config;

import com.fdz.common.enums.BussinessIds;
import com.fdz.common.utils.IDGenerator;
import com.fdz.common.utils.MacUtil;
import com.fdz.content.oss.OssClientManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Configuration
public class BeanConfiguration {

    @Resource
    private ApplicationProperties applicationProperties;

    @Bean
    public IDGenerator productTypeIDGenerator() {
        String ip = MacUtil.getIpAddress();
        long machineId = 0L;
        if (StringUtils.isNotBlank(ip)) {
            String[] numbers = ip.split("\\.");
            machineId = Long.parseLong(numbers[numbers.length - 1]);
        }
        IDGenerator idGenerator = new IDGenerator(machineId, BussinessIds.PRODUCTTYPE.getId());
        return idGenerator;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public OssClientManager ossClientManager() {
        return new OssClientManager(applicationProperties.getOss().getAccessKeyId(),
                applicationProperties.getOss().getAccessKeySecret(),
                applicationProperties.getOss().getEndpoint(),
                applicationProperties.getOss().getBucketName(),
                applicationProperties.getOss().getPublicEndpoint());
    }

}
