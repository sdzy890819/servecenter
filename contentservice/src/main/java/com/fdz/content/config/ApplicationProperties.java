package com.fdz.content.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.web.cors.CorsConfiguration;

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
    private final Rabbit rabbit = new Rabbit();

    @NestedConfigurationProperty
    private final RoutingKey routingKey = new RoutingKey();

    @NestedConfigurationProperty
    private final SecurityConf securityConf = new SecurityConf();


    @NestedConfigurationProperty
    private final MyRsaKey myRsaKey = new MyRsaKey();

    @NestedConfigurationProperty
    private final Oss oss = new Oss();

    @Data
    public static class Oss {
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;
        private String endpoint;
        private String imagesDir;
        private String publicEndpoint;
    }

    @Data
    public static class MyRsaKey {
        private String privateKey;
    }

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
    public static class RoutingKey {
        private String statusKey;
    }

    @Data
    public static class Rabbit {
        private String loanExchange;
        private String loanRoutingKey;
        private String host;
        private Integer port;
        private String username;
        private String password;
        private String virtualHost;
        private String loanChannel;
    }

    @Data
    public static class Zyt {
        private String userName;
        private String password;
        private String pushUrl;
        private String updateCaseUrl;
        /**
         * 还款结果确认
         */
        private String repayResultUrl;
        /**
         * 案件销案
         */
        private String closeCaseUrl;
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
