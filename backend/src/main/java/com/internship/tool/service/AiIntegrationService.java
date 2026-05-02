package com.internship.tool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AiIntegrationService {

    @Autowired
    private AiServiceClient aiServiceClient;

    @Async
    public void processWithAI(String input) {
        try {
            // calling method from AiServiceClient
            String result = aiServiceClient.callDescribe(input);

            // null/empty handling
            if (result == null || result.isBlank()) {
                result = "AI response not available";
            }

            // for now just print (basic integration)
            System.out.println("AI Result: " + result);

        } catch (Exception e) {
            System.out.println("AI processing failed: " + e.getMessage());
        }
    }
}
