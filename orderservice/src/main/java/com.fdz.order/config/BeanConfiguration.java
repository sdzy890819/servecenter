package com.fdz.order.config;

import com.fdz.common.enums.BussinessIds;
import com.fdz.common.utils.IDGenerator;
import com.fdz.common.utils.MacUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean("paySnIDGenerator")
    public IDGenerator paySnIDGenerator() {
        String ip = MacUtil.getIpAddress();
        long machineId = 0L;
        if (StringUtils.isNotBlank(ip)) {
            String[] numbers = ip.split("\\.");
            machineId = Long.parseLong(numbers[numbers.length - 1]);
        }
        IDGenerator idGenerator = new IDGenerator(machineId, BussinessIds.PAYSN.getId());
        return idGenerator;
    }

    @Bean("paymentIDGenerator")
    public IDGenerator paymentIDGenerator() {
        String ip = MacUtil.getIpAddress();
        long machineId = 0L;
        if (StringUtils.isNotBlank(ip)) {
            String[] numbers = ip.split("\\.");
            machineId = Long.parseLong(numbers[numbers.length - 1]);
        }
        IDGenerator idGenerator = new IDGenerator(machineId, BussinessIds.PAYMENT.getId());
        return idGenerator;
    }

    @Bean("orderIDGenerator")
    public IDGenerator orderIDGenerator() {
        String ip = MacUtil.getIpAddress();
        long machineId = 0L;
        if (StringUtils.isNotBlank(ip)) {
            String[] numbers = ip.split("\\.");
            machineId = Long.parseLong(numbers[numbers.length - 1]);
        }
        IDGenerator idGenerator = new IDGenerator(machineId, BussinessIds.ORDER.getId());
        return idGenerator;
    }

}
