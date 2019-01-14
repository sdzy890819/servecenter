package com.fdz.job.config;

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
