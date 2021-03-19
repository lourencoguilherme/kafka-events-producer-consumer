package com.guimtlo.learnkafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guimtlo.learnkafka.domains.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;

@Component
@Slf4j
public class LibraryEventProducer {

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private ObjectMapper mapper;

    private static String topic = "library-events";

    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getLibraryEvent();
        String value = mapper.writeValueAsString(libraryEvent);
        ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.sendDefault(key, value);
        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                hangleFailure(key, value, ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                hangleSuccess(key, value, result);
            }


        });


    }

    public void sendLibraryEventApproach(LibraryEvent libraryEvent) throws JsonProcessingException {
            Integer key = libraryEvent.getLibraryEvent();
            String value = mapper.writeValueAsString(libraryEvent);

            ProducerRecord<Integer, String> producerRecord = buildProducerRecord(key, value, topic);

            ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.send(producerRecord);
            listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
                @Override
                public void onFailure(Throwable ex) {
                    hangleFailure(key, value, ex);
                }

                @Override
                public void onSuccess(SendResult<Integer, String> result) {
                    hangleSuccess(key, value, result);
                }


            });


        }


    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {
        List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));

        return new ProducerRecord<>(topic, null, key, value, recordHeaders);
    }

    private void hangleFailure(Integer key, String value, Throwable ex) {
        log.error("Erro no envio key: {} e valor {}, resposta {}", key, value, ex.getMessage());
    }

    private void hangleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("Enviado com sucesso key: {} e valor {}, partição {}", key, value, result.getRecordMetadata().partition());
    }
}
