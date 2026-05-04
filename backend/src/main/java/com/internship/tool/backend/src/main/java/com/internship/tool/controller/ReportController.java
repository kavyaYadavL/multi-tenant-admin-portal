package com.internship.tool.controller;

import com.internship.tool.dto.GenerateReportResponse;
import com.internship.tool.service.AiIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ReportController {

    @Autowired
    private AiIntegrationService aiIntegrationService;

    @PostMapping("/generate-report")
    public GenerateReportResponse generateReport(@RequestBody String input) {
        return aiIntegrationService.generateReport(input);
    }
}
