package com.internship.tool.service;

import com.internship.tool.dto.GenerateReportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AiIntegrationService {

    @Autowired
    private AiServiceClient aiServiceClient;

    // Day 5
    @Async
    public void processWithAI(String input) {
        try {
            String result = aiServiceClient.callDescribe(input);

            if (result == null || result.isBlank()) {
                result = "AI response not available";
            }

            System.out.println("AI Result: " + result);

        } catch (Exception e) {
            System.out.println("AI processing failed");
        }
    }

    // Day 6
    public GenerateReportResponse generateReport(String input) {

        String aiResponse = aiServiceClient.callGenerateReport(input);

        GenerateReportResponse response = new GenerateReportResponse();

        if (aiResponse == null || aiResponse.isBlank()) {
            response.setTitle("No Report");
            response.setSummary("AI service unavailable");
            response.setOverview("");
            return response;
        }

        response.setTitle("AI Generated Report");
        response.setSummary(aiResponse);
        response.setOverview(aiResponse);

        return response;
    }
}
