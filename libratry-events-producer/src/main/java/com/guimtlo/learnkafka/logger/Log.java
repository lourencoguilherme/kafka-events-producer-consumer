package com.guimtlo.learnkafka.logger;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.coyote.Request;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
public class Log {
    private LogApi logApi;

    private String apiId;
    private String apiName;
    private String apiVersion;
    private LocalDateTime callStart;
    private LocalDateTime callEnb;
    private LocalDateTime endTime;
    private RequestEntity<JsonNode> request;
    private ResponseEntity<JsonNode> response;
    private List<Object> calls = new ArrayList<>();

    private LogLevel logLevel;
}
