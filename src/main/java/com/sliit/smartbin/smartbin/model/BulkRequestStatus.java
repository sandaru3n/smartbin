package com.sliit.smartbin.smartbin.model;

public enum BulkRequestStatus {
    PENDING("Pending", "Request is pending approval"),
    APPROVED("Approved", "Request has been approved"),
    PAYMENT_PENDING("Payment Pending", "Waiting for payment"),
    PAYMENT_COMPLETED("Payment Completed", "Payment has been processed"),
    COLLECTOR_ASSIGNED("Collector Assigned", "Collector has been assigned"),
    SCHEDULED("Scheduled", "Collection has been scheduled"),
    IN_PROGRESS("In Progress", "Collection is in progress"),
    COMPLETED("Completed", "Collection has been completed"),
    CANCELLED("Cancelled", "Request has been cancelled"),
    REJECTED("Rejected", "Request has been rejected");
    
    private final String displayName;
    private final String description;
    
    BulkRequestStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
