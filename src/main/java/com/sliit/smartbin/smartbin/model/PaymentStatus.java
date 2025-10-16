package com.sliit.smartbin.smartbin.model;

public enum PaymentStatus {
    PENDING("Pending", "Payment is pending"),
    PROCESSING("Processing", "Payment is being processed"),
    COMPLETED("Completed", "Payment has been completed"),
    FAILED("Failed", "Payment has failed"),
    REFUNDED("Refunded", "Payment has been refunded"),
    CANCELLED("Cancelled", "Payment has been cancelled");
    
    private final String displayName;
    private final String description;
    
    PaymentStatus(String displayName, String description) {
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
