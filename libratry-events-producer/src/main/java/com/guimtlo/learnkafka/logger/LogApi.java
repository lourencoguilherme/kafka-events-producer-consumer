package com.guimtlo.learnkafka.logger;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpMethod;

import java.io.Serializable;
import java.util.Map;

@NoArgsConstructor
@Data
public class LogApi implements Serializable {
    public static Long serialVersionUID = 1L;
    private HttpMethod httpMethod;
    private String uri;
    private String pathUri;
    private String serverName;
    private String requestIp;
    private Map<String, String[]> parametros;

    private LogLevel logLevel;
}
