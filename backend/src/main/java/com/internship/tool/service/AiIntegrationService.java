package com.internship.tool.service;

import com.internship.tool.dto.GenerateReportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiIntegrationService {

    @Autowired
    private AiServiceClient aiServiceClient;

    public GenerateReportResponse generateReport(String input) {

        GenerateReportResponse response = new GenerateReportResponse();

        try {
            String aiResponse = aiServiceClient.callGenerateReport(input);

            if (aiResponse == null || aiResponse.isBlank()) {

                response.setTitle("Fallback Report");
                response.setSummary("AI failed, showing default response");
                response.setOverview("Service unavailable, fallback used");

                response.setKeyItems(List.of("Default item 1", "Default item 2"));
                response.setRecommendations(List.of("Try again later", "Check input"));

                response.setFallback(true);

                return response;
            }

           
            response.setTitle("AI Generated Report");
            response.setSummary(aiResponse);
            response.setOverview(aiResponse);
            response.setKeyItems(List.of("Item 1", "Item 2"));
            response.setRecommendations(List.of("Recommendation 1", "Recommendation 2"));

            response.setFallback(false);

        } catch (Exception e) {

         
            response.setTitle("Error Report");
            response.setSummary("Something went wrong");
            response.setOverview("Exception occurred while processing");

            response.setKeyItems(List.of("Error item"));
            response.setRecommendations(List.of("Retry later"));

            response.setFallback(true);
        }

        return response;
    }
}
