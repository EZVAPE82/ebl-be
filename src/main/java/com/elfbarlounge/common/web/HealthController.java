package com.elfbarlounge.common.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/public")
public class HealthController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of(
                "status", "ok",
                "service", "elfbarlounge-backend",
                "timestamp", Instant.now().toString()
        );
    }
}
