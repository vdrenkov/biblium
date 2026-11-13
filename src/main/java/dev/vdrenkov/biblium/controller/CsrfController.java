package dev.vdrenkov.biblium.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CsrfController {
    @GetMapping("/csrf")
    public ResponseEntity<Map<String, String>> csrf(final CsrfToken csrfToken) {
        return ResponseEntity.ok(Map.of("token", csrfToken.getToken()));
    }
}
