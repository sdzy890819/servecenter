package com.fdz.job.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Configuration
@EnableSwagger2
@Profile("swagger")
public class SwaggerConfiguration {

    @Resource
    private ApplicationProperties applicationProperties;

    @Bean
    public Docket api() {
        Set<String> mediaTypes = new LinkedHashSet();
        mediaTypes.add("application/json;charset=UTF-8");
        mediaTypes.add("text/xml");
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .consumes(mediaTypes)
                .produces(mediaTypes)
                .forCodeGeneration(true)
                .genericModelSubstitutes(new Class[]{ResponseEntity.class})
                .ignoredParameterTypes(new Class[]{Date.class})
                .directModelSubstitute(LocalDateTime.class, java.util.Date.class)
                .directModelSubstitute(LocalDate.class, java.util.Date.class)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.fdz.job"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        ApplicationProperties.Swagger2 swagger2 = applicationProperties.getSwagger2();
        return new ApiInfoBuilder()
                .title(swagger2.getTitle())
                .description(swagger2.getDescription())
                .contact(swagger2.getContact().get())
                .termsOfServiceUrl(swagger2.getTermsUrl())
                .version(swagger2.getVersion())
                .build();
    }

}
