package com.fdz.managergateway.middleware.swagger;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.HashMap;

@RestController
@RequestMapping("/api-docs")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Profile("swagger")
public class ProxiedSwaggerController {

    private final
    @NonNull
    ZuulProperties zuulProperties;

    private final
    @NonNull
    LoadBalancerClient loadBalancerClient;

    private RestTemplate restTemplate;

    @PostConstruct
    void init() {
        this.restTemplate = new RestTemplate();
    }

    @ApiIgnore
    @GetMapping(
            value = "{serviceId}",
            produces = {"application/json", "application/hal+json"}
    )
    public ResponseEntity<HashMap<String, Object>> processSwaggerRequest(@PathVariable("serviceId") String serviceId, HttpServletRequest request) {
        ServiceInstance selectedInstance = loadBalancerClient.choose(serviceId);
        if (selectedInstance != null) {
            URI serviceURI = selectedInstance.getUri();
            String contextPath = selectedInstance.getMetadata().getOrDefault("server.context-path", "");
            String documentationUri = serviceURI.toString() + contextPath + "/v2/api-docs";
            ResponseEntity<HashMap<String, Object>> swaggerResponse = restTemplate.exchange(documentationUri, HttpMethod.GET, null, new ParameterizedTypeReference<HashMap<String, Object>>() {
            });
            HashMap<String, Object> swagger = swaggerResponse.getBody();
            swagger.put("name", "MANAGER-GATEWAY");
            swagger.put("host", "");
            swagger.put("basePath", request.getContextPath() + zuulProperties.getPrefix());
            return new ResponseEntity<>(swagger, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
