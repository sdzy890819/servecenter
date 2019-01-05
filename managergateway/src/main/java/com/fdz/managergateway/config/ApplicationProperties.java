package com.fdz.managergateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.web.cors.CorsConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置读取.
 */
@ConfigurationProperties(prefix = "application")
@Data
public class ApplicationProperties {

    @NestedConfigurationProperty
    private final Cors cors = new Cors();

    @NestedConfigurationProperty
    private final Swagger2 swagger2 = new Swagger2();

    @NestedConfigurationProperty
    private final SecurityConf securityConf = new SecurityConf();

    @Data
    public static class SecurityConf {
        @NestedConfigurationProperty
        private final Authentication authentication = new Authentication();
        private boolean syncFetchJwtCfgFromRemoteServer = false;
    }

    @Data
    public static class Authentication {
        @NestedConfigurationProperty
        private final Jwt jwt = new Jwt();
    }

    @Data
    public static class Jwt {
        private String secret = "";
        private String secret2 = "";
        private Long tokenValidityInSeconds = 60L; //6 sec
        private Long tokenValidityInSecondsForRememberMe = 6 * 60 * 60L; //6 hours
    }

    @Data
    public static class Cors {
        private boolean enabled = false;
        @NestedConfigurationProperty
        private CorsConfiguration config = new CorsConfiguration();
    }


    @Data
    public static class Swagger2 {
        private String title;
        private String description;
        private String version;
        private Map<String, String> serviceMappingNames = new HashMap<>();
        private List<String> ignoredServices = new ArrayList<>();
        @NestedConfigurationProperty
        private Contact contact = new Contact();
        private String termsUrl;
    }

    @Data
    public static class Contact {
        private String name;
        private String url;
        private String email;

        public springfox.documentation.service.Contact get() {
            return new springfox.documentation.service.Contact(name, url, email);
        }
    }

}
