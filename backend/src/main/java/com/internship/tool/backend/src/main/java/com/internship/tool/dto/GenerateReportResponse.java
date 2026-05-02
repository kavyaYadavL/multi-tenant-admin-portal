package com.internship.tool.dto;

import java.util.List;

public class GenerateReportResponse {

    private String title;
    private String summary;
    private String overview;
    private List<String> keyItems;
    private List<String> recommendations;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public List<String> getKeyItems() {
        return keyItems;
    }

    public void setKeyItems(List<String> keyItems) {
        this.keyItems = keyItems;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
}
