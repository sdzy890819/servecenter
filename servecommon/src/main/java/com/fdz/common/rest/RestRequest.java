package com.fdz.common.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.constant.Constants;
import com.fdz.common.exception.BizException;
import com.fdz.common.exception.IORequestBizException;
import com.fdz.common.exception.RequestBizException;
import com.fdz.common.exception.TimeOutException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.Map;

@Data
@Slf4j
public abstract class RestRequest {

    private final static EnumSet<HttpStatus> RETRY_HTTP_STATUS = EnumSet.of(HttpStatus.BAD_GATEWAY, HttpStatus.GATEWAY_TIMEOUT);

    /**
     * Get 请求
     */
    public String doGet(String url) {
        return doGet(url, null);
    }

    /**
     * GET 简单请求.
     *
     * @param url
     * @param params
     * @return
     */
    public String doGet(String url, Map<String, String> params) {
        String requestUrl = buildUrl(url, params);
        info(requestUrl, HttpMethod.GET, "", "0", "");
        ResponseEntity<String> result = null;
        try {
            result = getRestTemplate().getForEntity(requestUrl, String.class);
            info(requestUrl, HttpMethod.GET, "", result.getStatusCodeValue(), result.getBody());
            return result.getBody();
        } catch (HttpClientErrorException e) {
            error(requestUrl, HttpMethod.GET, "", e.getStatusCode().value(), e.getResponseBodyAsString());
            throw new RequestBizException(e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException e) {
            error(requestUrl, HttpMethod.GET, "", e.getStatusCode().value(), e.getResponseBodyAsString());
            if (RETRY_HTTP_STATUS.contains(e.getStatusCode())) {
                throw new TimeOutException(e.getStatusCode(), e.getResponseBodyAsString(), e);
            } else {
                throw new RequestBizException(e.getStatusCode(), e.getResponseBodyAsString(), e);
            }
        } catch (ResourceAccessException e) {
            error(requestUrl, HttpMethod.GET, "", "499", e.getMessage());
            throw new IORequestBizException(e);
        } catch (Exception e) {
            error(requestUrl, HttpMethod.GET, "", "error", e.getMessage());
            throw new BizException(MessageFormat.format(Constants.BusinessCode.THIRDPARTY_ERROR_MESSAGE, getBusinessName()), e);
        }

    }

    /**
     * 传递url 和jsonMap
     *
     * @param url
     * @param jsonMap
     * @return
     */
    public String doPostJson(String url, Map<String, Object> jsonMap) {
        return doPostJson(url, null, jsonMap, null);
    }


    /**
     * Post Json
     *
     * @param url
     * @param params
     * @param jsonMap
     * @return
     */
    public String doPostJson(String url, Map<String, String> params, Map<String, Object> jsonMap) {
        return doPostJson(url, params, jsonMap, null);
    }

    /**
     * Post Json 实现
     *
     * @param url
     * @param params
     * @param jsonMap
     * @param headerMap
     * @return
     */
    public String doPostJson(String url, Map<String, String> params, Map<String, Object> jsonMap, Map<String, String> headerMap) {
        String requestUrl = buildUrl(url, params);
        try {
            info(requestUrl, HttpMethod.POST, getObjectMapper().writeValueAsString(jsonMap), "0", "");
        } catch (JsonProcessingException e) {
            log.error("do post json data error, json processing error, {}", e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (headerMap != null && headerMap.size() > 0) {
            headerMap.forEach((k, v) -> headers.add(k, v));
        }
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(jsonMap, headers);
        return execRest(requestUrl, request);
    }

    /**
     * 执行rest
     *
     * @param requestUrl
     * @param request
     * @return
     */
    private String execRest(String requestUrl, HttpEntity<?> request) {
        ResponseEntity<String> result = null;
        try {
            result = getRestTemplate().postForEntity(requestUrl, request, String.class);
            info(requestUrl, HttpMethod.POST, getObjectMapper().writeValueAsString(request.getBody()), result.getStatusCodeValue(), result.getBody());
            return result.getBody();
        } catch (HttpClientErrorException e) {
            try {
                error(requestUrl, HttpMethod.POST, getObjectMapper().writeValueAsString(request.getBody()), e.getStatusCode().value(), e.getResponseBodyAsString());
            } catch (JsonProcessingException e1) {
                log.error("do post json data error, json processing error, {}", e1);
            }
            throw new RequestBizException(e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException e) {
            try {
                error(requestUrl, HttpMethod.POST, getObjectMapper().writeValueAsString(request.getBody()), e.getStatusCode().value(), e.getResponseBodyAsString());
            } catch (JsonProcessingException e1) {
                log.error("do post json data error, json processing error, {}", e1);
            }
            if (RETRY_HTTP_STATUS.contains(e.getStatusCode())) {
                throw new TimeOutException(e.getStatusCode(), e.getResponseBodyAsString(), e);
            } else {
                throw new RequestBizException(e.getStatusCode(), e.getResponseBodyAsString(), e);
            }
        } catch (ResourceAccessException e) {
            try {
                error(requestUrl, HttpMethod.POST, getObjectMapper().writeValueAsString(request.getBody()), "499", e.getMessage());
            } catch (JsonProcessingException e1) {
                log.error("do post json data error, json processing error, {}", e1);
            }
            throw new IORequestBizException(e);
        } catch (Exception e) {
            try {
                error(requestUrl, HttpMethod.POST, getObjectMapper().writeValueAsString(request.getBody()), "error", e.getMessage());
            } catch (JsonProcessingException e1) {
                log.error("do post json data error, json processing error, {}", e1);
            }
            throw new BizException(MessageFormat.format(Constants.BusinessCode.THIRDPARTY_ERROR_MESSAGE, getBusinessName()), e);
        }
    }

    /**
     * 忽略header
     */
    public String doPostData(String url, Map<String, String> params, Map<String, Object> data) {
        return doPostData(url, params, data, null);
    }

    /**
     * 忽略header
     * InputStream io流
     */
    public InputStream doPostInputStreamData(String url, Map<String, Object> data) {
        return doPostInputStreamData(url, data, null);
    }

    /**
     * 忽略header 和params
     *
     * @param url
     * @param data
     * @return
     */
    public String doPostData(String url, Map<String, Object> data) {
        return doPostData(url, null, data, null);
    }

    /**
     * post data
     *
     * @param url
     * @param params
     * @param data
     * @param headerMap
     * @return
     */
    public String doPostData(String url, Map<String, String> params, Map<String, Object> data, Map<String, String> headerMap) {
        String requestUrl = buildUrl(url, params);
        try {
            info(requestUrl, HttpMethod.POST, getObjectMapper().writeValueAsString(data(data)), "0", "");
        } catch (JsonProcessingException e) {
            log.error("do post json data error, json processing error, {}", e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        if (headerMap != null && headerMap.size() > 0) {
            headerMap.forEach((k, v) -> headers.add(k, v));
        }
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(data(data), headers);
        return execRest(requestUrl, request);
    }


    /**
     * post data
     *
     * @param url
     * @param data
     * @param headerMap
     * @return
     */
    public InputStream doPostInputStreamData(String url, Map<String, Object> data, Map<String, String> headerMap) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        if (headerMap != null && headerMap.size() > 0) {
            headerMap.forEach((k, v) -> headers.add(k, v));
        }
        ResponseEntity<Resource> result = null;
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(data(data), headers);
        try {
            result = getRestTemplate().postForEntity(url, request, Resource.class);
            info(url, HttpMethod.POST, getObjectMapper().writeValueAsString(request.getBody()), result.getStatusCodeValue(), result.getBody());
            return result.getBody().getInputStream();
        } catch (HttpClientErrorException e) {
            try {
                error(url, HttpMethod.POST, getObjectMapper().writeValueAsString(request.getBody()), e.getStatusCode().value(), e.getResponseBodyAsString());
            } catch (JsonProcessingException e1) {
                log.error("do post json data error, json processing error, {}", e1);
            }
            throw new RequestBizException(e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException e) {
            try {
                error(url, HttpMethod.POST, getObjectMapper().writeValueAsString(request.getBody()), e.getStatusCode().value(), e.getResponseBodyAsString());
            } catch (JsonProcessingException e1) {
                log.error("do post json data error, json processing error, {}", e1);
            }
            if (RETRY_HTTP_STATUS.contains(e.getStatusCode())) {
                throw new TimeOutException(e.getStatusCode(), e.getResponseBodyAsString(), e);
            } else {
                throw new RequestBizException(e.getStatusCode(), e.getResponseBodyAsString(), e);
            }
        } catch (ResourceAccessException e) {
            try {
                error(url, HttpMethod.POST, getObjectMapper().writeValueAsString(request.getBody()), "499", e.getMessage());
            } catch (JsonProcessingException e1) {
                log.error("do post json data error, json processing error, {}", e1);
            }
            throw new IORequestBizException(e);
        } catch (Exception e) {
            try {
                error(url, HttpMethod.POST, getObjectMapper().writeValueAsString(request.getBody()), "error", e.getMessage());
            } catch (JsonProcessingException e1) {
                log.error("do post json data error, json processing error, {}", e1);
            }
            throw new BizException(MessageFormat.format(Constants.BusinessCode.THIRDPARTY_ERROR_MESSAGE, getBusinessName()), e);
        }
    }

    /**
     * Get 请求
     * io流
     */
    public InputStream doGetInputStream(String url) {
        return doGetInputStreamData(url);
    }

    public InputStream doGetInputStreamData(String url) {
        ResponseEntity<Resource> result = null;
        try {
            result = getRestTemplate().getForEntity(url, Resource.class);
            info(url, HttpMethod.GET, "", result.getStatusCodeValue(), result.getBody());
            return result.getBody().getInputStream();
        } catch (HttpClientErrorException e) {
            error(url, HttpMethod.GET, "", e.getStatusCode().value(), e.getResponseBodyAsString());
            throw new RequestBizException(e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException e) {
            error(url, HttpMethod.GET, "", e.getStatusCode().value(), e.getResponseBodyAsString());
            if (RETRY_HTTP_STATUS.contains(e.getStatusCode())) {
                throw new TimeOutException(e.getStatusCode(), e.getResponseBodyAsString(), e);
            } else {
                throw new RequestBizException(e.getStatusCode(), e.getResponseBodyAsString(), e);
            }
        } catch (ResourceAccessException e) {
            error(url, HttpMethod.GET, "", "499", e.getMessage());
            throw new IORequestBizException(e);
        } catch (Exception e) {
            error(url, HttpMethod.GET, "", "error", e.getMessage());
            throw new BizException(MessageFormat.format(Constants.BusinessCode.THIRDPARTY_ERROR_MESSAGE, getBusinessName()), e);
        }
    }

    protected MultiValueMap<String, Object> data(Map<String, Object> map) {
        MultiValueMap<String, Object> result = new LinkedMultiValueMap<>();
        if (map != null) {
            map.forEach((k, v) -> result.add(k, v));

        }
        return result;
    }


    /**
     * 拼接请求链接
     *
     * @param url
     * @param params
     * @return
     */
    private String buildUrl(String url, Map<String, String> params) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(url);
        if (params != null && params.size() > 0) {
            if (!url.endsWith(Constants.Symbol.QUESTIONS_MARK)) {
                stringBuffer.append(Constants.Symbol.QUESTIONS_MARK);
            }
            params.forEach((key, value) -> {
                stringBuffer.append(key);
                stringBuffer.append(Constants.Symbol.EQUAL_MARK);
                stringBuffer.append(value);
                stringBuffer.append(Constants.Symbol.AND);
            });
            if (stringBuffer.lastIndexOf(Constants.Symbol.AND) == stringBuffer.length() ||
                    stringBuffer.lastIndexOf(Constants.Symbol.QUESTIONS_MARK) == stringBuffer.length()) {
                return stringBuffer.substring(0, stringBuffer.length() - 1);
            }
        }

        return stringBuffer.toString();
    }

    public void info(Object url, HttpMethod httpMethod, Object request, Object code, Object response) {
        log.info("[Business:{}] [url:{}] [method:{}] [request:{}] [status code:{}] [response:{}]", getBusinessName(), url, httpMethod.toString(), request, code, response);
    }

    public void error(Object url, HttpMethod httpMethod, Object request, Object code, Object response) {
        log.error("[Business:{}] [url:{}] [method:{}] [request:{}] [status code:{}] [response:{}]", getBusinessName(), url, httpMethod.toString(), request, code, response);
    }


    public abstract RestTemplate getRestTemplate();

    protected abstract String getBusinessName();

    public abstract ObjectMapper getObjectMapper();

}
