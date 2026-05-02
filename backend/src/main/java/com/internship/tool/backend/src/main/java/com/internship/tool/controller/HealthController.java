package com.internship.tool.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {

        Map<String, Object> res = new HashMap<>();
        res.put("status", "UP");
        res.put("model", "AI Service");
        res.put("avgResponseTime", "100ms");
        res.put("uptime", "running");

        return res;
    }
}
