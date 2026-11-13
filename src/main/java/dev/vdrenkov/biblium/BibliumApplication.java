package dev.vdrenkov.biblium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BibliumApplication {
    private BibliumApplication() {
        /* static bootstrap class */
    }

    static void main(final String[] args) {
        SpringApplication.run(BibliumApplication.class, args);
    }
}
