package com.fdz.thirdpartygateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.rest.rsarest.vo.ThirdpartyResponse;
import com.fdz.common.web.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ThirdpartyZuulFallback implements FallbackProvider {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse() {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return this.getStatusCode().value();
            }

            @Override
            public String getStatusText() throws IOException {
                return this.getStatusCode().getReasonPhrase();
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {
                RestResponse restResponse = RestResponse.error(ThirdpartyResponse.ERROR_CODE, "哎呀，服务好像出去旅游了. 稍等片刻，喝口茶");
                return new ByteArrayInputStream(objectMapper.writeValueAsBytes(restResponse));
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
                return httpHeaders;
            }
        };
    }

    @Override
    public ClientHttpResponse fallbackResponse(Throwable cause) {
        log.error("[gateway] [service zuul] [error] [throwable: {}]", cause.getMessage());
        return fallbackResponse();
    }
}
