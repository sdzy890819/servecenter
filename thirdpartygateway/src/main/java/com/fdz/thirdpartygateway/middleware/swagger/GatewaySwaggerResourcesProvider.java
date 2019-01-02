package com.fdz.thirdpartygateway.middleware.swagger;


import com.fdz.thirdpartygateway.config.ApplicationProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Primary
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Profile("swagger")
public class GatewaySwaggerResourcesProvider implements SwaggerResourcesProvider {

    @NonNull
    private final DiscoveryClient discoveryClient;

    @NonNull
    private final ApplicationProperties applicationProperties;

    @NonNull
    private final LoadBalancerClient loadBalancerClient;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        Map<String, String> serviceMappingNames = applicationProperties.getSwagger2().getServiceMappingNames();
        List<String> servicesNeedIgnoring = applicationProperties.getSwagger2().getIgnoredServices();
        discoveryClient.getServices().stream().filter(serviceId ->
                !servicesNeedIgnoring.contains(serviceId)
        ).forEach(serviceId -> {
            log.info("service : {}", serviceId);
            ServiceInstance selectedInstance = loadBalancerClient.choose(serviceId);
            if (selectedInstance != null) {
                String serviceName = serviceMappingNames.get(serviceId);
                if (StringUtils.isEmpty(serviceName)) {
                    serviceName = serviceId;
                }
                SwaggerResource resource = swaggerResource(serviceName, "/api-docs/" + serviceId);
                resources.add(resource);
            }
        });
        return resources;


    }

    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }
}
