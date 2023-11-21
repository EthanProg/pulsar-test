package com.test.pulsar;

import com.test.pulsar.bo.User1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PulsarController {

    private final PulsarProducer producer;

    @GetMapping("/test")
    public ResponseEntity<String> test() throws PulsarClientException {
        producer.sendMessage(User1.builder().name("Tom").age(21).build());
        return ResponseEntity.ok().body("OK");
    }
}
