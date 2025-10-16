package com.sliit.smartbin.smartbin.dto;

import com.sliit.smartbin.smartbin.model.BulkCategory;
import com.sliit.smartbin.smartbin.model.BulkRequestStatus;
import com.sliit.smartbin.smartbin.model.PaymentStatus;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
// import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

public class BulkRequestDTO {
    
    private Long id;
    private String requestId;
    private Long userId;
    private String userName;
    private String userEmail;
    
    private BulkCategory category;
    private String description;
    private String streetAddress;
    private String city;
    private String zipCode;
    
    private Double latitude;
    private Double longitude;
    private Double estimatedWeight;
    private String estimatedDimensions;
    
    private Double basePrice;
    private Double processingFee;
    private Double taxAmount;
    private Double totalAmount;
    
    private BulkRequestStatus status;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private String paymentReference;
    private Long collectorAssigned;
    private String collectorName;
    private LocalDateTime scheduledDate;
    private LocalDateTime completedDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> photoUrls;
    
    // Constructors
    public BulkRequestDTO() {}
    
    public BulkRequestDTO(String requestId, BulkCategory category, String description,
                         String streetAddress, String city, String zipCode) {
        this.requestId = requestId;
        this.category = category;
        this.description = description;
        this.streetAddress = streetAddress;
        this.city = city;
        this.zipCode = zipCode;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public BulkCategory getCategory() {
        return category;
    }
    
    public void setCategory(BulkCategory category) {
        this.category = category;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getStreetAddress() {
        return streetAddress;
    }
    
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public Double getEstimatedWeight() {
        return estimatedWeight;
    }
    
    public void setEstimatedWeight(Double estimatedWeight) {
        this.estimatedWeight = estimatedWeight;
    }
    
    public String getEstimatedDimensions() {
        return estimatedDimensions;
    }
    
    public void setEstimatedDimensions(String estimatedDimensions) {
        this.estimatedDimensions = estimatedDimensions;
    }
    
    public Double getBasePrice() {
        return basePrice;
    }
    
    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }
    
    public Double getProcessingFee() {
        return processingFee;
    }
    
    public void setProcessingFee(Double processingFee) {
        this.processingFee = processingFee;
    }
    
    public Double getTaxAmount() {
        return taxAmount;
    }
    
    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public Double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public BulkRequestStatus getStatus() {
        return status;
    }
    
    public void setStatus(BulkRequestStatus status) {
        this.status = status;
    }
    
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentReference() {
        return paymentReference;
    }
    
    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }
    
    public Long getCollectorAssigned() {
        return collectorAssigned;
    }
    
    public void setCollectorAssigned(Long collectorAssigned) {
        this.collectorAssigned = collectorAssigned;
    }
    
    public String getCollectorName() {
        return collectorName;
    }
    
    public void setCollectorName(String collectorName) {
        this.collectorName = collectorName;
    }
    
    public LocalDateTime getScheduledDate() {
        return scheduledDate;
    }
    
    public void setScheduledDate(LocalDateTime scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
    
    public LocalDateTime getCompletedDate() {
        return completedDate;
    }
    
    public void setCompletedDate(LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<String> getPhotoUrls() {
        return photoUrls;
    }
    
    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }
    
    @Override
    public String toString() {
        return "BulkRequestDTO{" +
                "id=" + id +
                ", requestId='" + requestId + '\'' +
                ", category=" + category +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", createdAt=" + createdAt +
                '}';
    }
}
