package com.guimtlo.learnkafka.controllers;

import com.guimtlo.learnkafka.domains.LibraryEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/libraryevents")
public class LibraryEventsController {

    @PostMapping
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody LibraryEvent libraryEvent) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }
}
