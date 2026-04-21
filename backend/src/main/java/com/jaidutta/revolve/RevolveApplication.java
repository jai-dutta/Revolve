package com.jaidutta.revolve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RevolveApplication {

    public static void main(String[] args) {
        SpringApplication.run(RevolveApplication.class, args);
    }

}
