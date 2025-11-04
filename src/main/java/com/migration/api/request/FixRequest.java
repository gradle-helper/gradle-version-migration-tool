package com.migration.api.request;

import java.util.List;

/**
 * Request object for applying fixes
 */
public class FixRequest {
    private List<String> issueIds;
    
    public FixRequest() {
    }
    
    public FixRequest(List<String> issueIds) {
        this.issueIds = issueIds;
    }
    
    public List<String> getIssueIds() {
        return issueIds;
    }
    
    public void setIssueIds(List<String> issueIds) {
        this.issueIds = issueIds;
    }
}
