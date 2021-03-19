package com.guimtlo.learnkafka.intg;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guimtlo.learnkafka.domains.LibraryEvent;
import com.guimtlo.learnkafka.templates.LibraryEventFixture;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@ActiveProfiles("SANDBOX")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.boostrap-servers=${spring.embedded.kafka.brokers}"
})
public class LibriryEventsControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<Integer, String> consumer;


//    @BeforeEach
//    public void setUp() {
//        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
//        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
//        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
//    }

    @AfterEach
    public void tearDown() {
        consumer.close();
    }

    @Before
    public void before() {

        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
        FixtureFactoryLoader.loadTemplates("com.guimtlo.learnkafka.templates");
    }


    @Test
    public void postLibraryEvent() throws JsonProcessingException {
        LibraryEvent libraryEvent = Fixture.from(LibraryEvent.class).gimme(LibraryEventFixture.LIBRARY_EVENT);
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

        ResponseEntity<LibraryEvent> libraryEventResponseEntity = restTemplate.exchange("/v1/libraryevents", HttpMethod.POST, request, LibraryEvent.class);

        assertEquals(HttpStatus.CREATED, libraryEventResponseEntity.getStatusCode());

        ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events");

        String value = consumerRecord.value();

        LibraryEvent libraryEventRecord = (LibraryEvent) jsonToObject(value, LibraryEvent.class);
        String libraryEventProducerAsString = objectToString(libraryEventResponseEntity.getBody());


        assertNotNull(libraryEventRecord);



        assertEquals(value, libraryEventProducerAsString);

    }


    public static Object jsonToObject(String value, Class classe) throws JsonProcessingException {
        return new ObjectMapper().readValue(value, classe);
    }

    public static String objectToString(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

}
