package com.newwine.church.dto.response;

import java.util.Map;

public class StatsResponse {
    private Long totalRegistrations;
    private Long totalContacts;
    private Map<String, Long> eventStats;
    private Long recentCount;

    public StatsResponse() {}

    public StatsResponse(Long totalRegistrations, Long totalContacts, Map<String, Long> eventStats, Long recentCount) {
        this.totalRegistrations = totalRegistrations;
        this.totalContacts = totalContacts;
        this.eventStats = eventStats;
        this.recentCount = recentCount;
    }

    // Getters and Setters
    public Long getTotalRegistrations() { return totalRegistrations; }
    public void setTotalRegistrations(Long totalRegistrations) { this.totalRegistrations = totalRegistrations; }
    public Long getTotalContacts() { return totalContacts; }
    public void setTotalContacts(Long totalContacts) { this.totalContacts = totalContacts; }
    public Map<String, Long> getEventStats() { return eventStats; }
    public void setEventStats(Map<String, Long> eventStats) { this.eventStats = eventStats; }
    public Long getRecentCount() { return recentCount; }
    public void setRecentCount(Long recentCount) { this.recentCount = recentCount; }
}