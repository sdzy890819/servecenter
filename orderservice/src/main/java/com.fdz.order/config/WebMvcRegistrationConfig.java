package com.fdz.order.config;

import com.fdz.common.web.version.VersionRequestMappingHandlerMapping;
import org.springframework.boot.autoconfigure.web.WebMvcRegistrationsAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * 支持apiversion
 */
@Configuration
public class WebMvcRegistrationConfig extends WebMvcRegistrationsAdapter {

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        VersionRequestMappingHandlerMapping handlerMapping = new VersionRequestMappingHandlerMapping("v");
        handlerMapping.setOrder(0);
        return handlerMapping;
    }
}
