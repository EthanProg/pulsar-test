package com.test.pulsar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.pulsar.annotation.EnablePulsar;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnablePulsar
public class PulsarTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(PulsarTestApplication.class, args);
    }

}
