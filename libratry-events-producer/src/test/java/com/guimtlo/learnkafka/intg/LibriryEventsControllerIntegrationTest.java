package com.guimtlo.learnkafka.intg;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import com.guimtlo.learnkafka.domains.LibraryEvent;
import com.guimtlo.learnkafka.templates.LibraryEventFixture;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;
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

    @Before
    public void before() {
        FixtureFactoryLoader.loadTemplates("com.guimtlo.learnkafka.templates");
    }


    @Test
    public void postLibraryEvent(){
        LibraryEvent libraryEvent = Fixture.from(LibraryEvent.class).gimme(LibraryEventFixture.LIBRARY_EVENT);
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

        ResponseEntity<LibraryEvent> libraryEventResponseEntity = restTemplate.exchange("/v1/libraryevents", HttpMethod.POST, request, LibraryEvent.class);

        assertEquals(HttpStatus.CREATED, libraryEventResponseEntity.getStatusCode());

    }

}
