package com.guimtlo.learnkafka.logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Data
@ToString
public class LoggerApi {
    private static Map<String, Log> mapLog;


    @Autowired
    private Environment tempEnv;

    private static Environment env;



    @PostConstruct
    private void init() {
        env = tempEnv;
    }


    private static final Logger logger = LoggerFactory.getLogger(LoggerApi.class);

    public static Boolean existLogApiId(String apiId) {
        if(mapLog != null) {
            return mapLog.containsKey(apiId);
        }
        return false;
    }

    public static void create(String apiId) {
        if(mapLog == null) {
            mapLog = new LinkedHashMap<>();
        }

        Log log = new Log();
        log.setCallStart(LocalDateTime.now());
        log.setApiId(apiId);

        if(env != null) {
            log.setApiName(getApiName());
            log.setApiVersion(getApiVersion());
        }

        mapLog.put(apiId, log);


    }

    private static String getApiName() {
        String apiId = env.getProperty("api.id");
        if(apiId == null) {
            apiId = "teste";
        }
        return apiId;
    }
    private static String getApiVersion() {
        String apiId = env.getProperty("api.version");
        if(apiId == null) {
            apiId = "0.0.1";
        }
        return apiId;
    }

    public static void setLogApi(String apiId, ContentCachingRequestWrapper requestWrapper) {
        Log log = (Log)mapLog.get(apiId);
        if(log != null) {
            LogApi logApi = new LogApi();
            logApi.setParametros(requestWrapper.getParameterMap());
            logApi.setRequestIp(getRemoteAddr(requestWrapper));
            logApi.setHttpMethod(HttpMethod.valueOf(requestWrapper.getMethod()));
            logApi.setUri(requestWrapper.getRequestURI());
            log.setLogApi(logApi);
        }
    }

    private static String getRemoteAddr(ContentCachingRequestWrapper requestWrapper) {
        String ipFromHeader = requestWrapper.getHeader("X-FORWARDED-FOR");
        return ipFromHeader != null && ipFromHeader.length() > 0 ? ipFromHeader : requestWrapper.getRemoteAddr();

    }

    public static void setLogLevel(String apiId, LogLevel logLevel) {
        Log log = mapLog.get(apiId);
        if(log != null) {
            log.setLogLevel(logLevel);
        }
    }

    public static void newStep(String apiId, Object step) {
        if(mapLog == null) {
            create(apiId);
        }

        Log log = mapLog.get(apiId);
        if(log != null) {
            log.getCalls().add(step);
        }
    }

    public static void setException(String apiId, Exception exception) {
//        Log log = mapLog.get(apiId);
//        if(log != null) {
//            String message = exception.getMessage();
//
//            String stack = ExceptionUtils
//        }
    }


    public static void save(String apiId, ContentCachingResponseWrapper responseWrapper, RequestEntity<JsonNode> requestEntity, ResponseEntity<JsonNode> responseEntity) {
        try {
            Log log  = mapLog.get(apiId);
            if(log == null) {
                return;
            }
            log.setRequest(requestEntity);
            log.setResponse(responseEntity);
            log.setEndTime(LocalDateTime.now());

            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(log);
            logger.info(jsonString);
            mapLog.remove(apiId);

        }catch (IOException exception) {

        }
    }

}
