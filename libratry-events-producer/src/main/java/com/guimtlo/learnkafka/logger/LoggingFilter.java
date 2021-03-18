package com.guimtlo.learnkafka.logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.UUID;

@Component
@NoArgsConstructor
public class LoggingFilter extends OncePerRequestFilter {
    @Autowired
    private ObjectMapper mapper;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpServletRequest);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpServletResponse);
        String apiId = this.defineCorrelationId(httpServletRequest);

        LoggerApi.create(apiId);

        httpServletRequest.setAttribute("api-id", apiId);
        filterChain.doFilter(requestWrapper, responseWrapper);
        String url = requestWrapper.getRequestURI();

        HttpHeaders requestHeaders = new HttpHeaders();

        Enumeration headerNames = requestWrapper.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            requestHeaders.add(headerName, requestWrapper.getHeader(headerName));
        }

        HttpMethod httpMethod = HttpMethod.valueOf(requestWrapper.getMethod());
        String requestBody = getData(requestWrapper);

        JsonNode requestJson = requestBody == null ? null : this.mapper.readTree(requestBody);
        RequestEntity<JsonNode> requestEntity = new RequestEntity<>(requestJson, requestHeaders, httpMethod, URI.create(url));
        HttpStatus responseStatus = HttpStatus.valueOf(responseWrapper.getStatus());
        HttpHeaders responseHeaders = new HttpHeaders();
        Iterator interators = responseWrapper.getHeaderNames().iterator();

        while (interators.hasNext()) {
            String headerName = (String)interators.next();
            responseHeaders.add(headerName, responseWrapper.getHeader(headerName));
        }

        String responseBody = getData(responseWrapper);
        JsonNode responseJson = responseBody == null ? null : this.mapper.readTree(requestBody);
        ResponseEntity<JsonNode> responseEntity = new ResponseEntity(responseJson, responseHeaders, responseStatus);
        responseWrapper.copyBodyToResponse();
        LoggerApi.setLogApi(apiId, requestWrapper);
        LoggerApi.setLogLevel(apiId, LogLevel.INFO);
        LoggerApi.save(apiId, responseWrapper, requestEntity, responseEntity);

    }

    private String getData(HttpServletRequest request) throws UnsupportedEncodingException {
        String payload = null;
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if(wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if(buf.length > 0) {
                payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
            }
        }
        return payload;
    }

    private String getData(HttpServletResponse request) throws UnsupportedEncodingException {
        String payload = null;
        ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(request, ContentCachingResponseWrapper.class);
        if(wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if(buf.length > 0) {
                payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
            }
        }
        return payload;
    }

    private String defineCorrelationId(HttpServletRequest request) {
        String apiId = request.getHeader("api-id");
        if(apiId == null || LoggerApi.existLogApiId(apiId)) {
            apiId = UUID.randomUUID().toString();
        }

        return apiId;
    }
}
