package com.sliit.smartbin.smartbin.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bulk_requests")
public class BulkRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "request_id", unique = true, nullable = false)
    private String requestId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private BulkCategory category;
    
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @Column(name = "street_address", nullable = false)
    private String streetAddress;
    
    @Column(name = "city", nullable = false)
    private String city;
    
    @Column(name = "zip_code", nullable = false)
    private String zipCode;
    
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    @Column(name = "estimated_weight")
    private Double estimatedWeight;
    
    @Column(name = "estimated_dimensions")
    private String estimatedDimensions;
    
    @Column(name = "base_price", nullable = false)
    private Double basePrice;
    
    @Column(name = "processing_fee", nullable = false)
    private Double processingFee;
    
    @Column(name = "tax_amount", nullable = false)
    private Double taxAmount;
    
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BulkRequestStatus status = BulkRequestStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "payment_reference")
    private String paymentReference;
    
    @Column(name = "collector_assigned")
    private Long collectorAssigned;
    
    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;
    
    @Column(name = "completed_date")
    private LocalDateTime completedDate;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ElementCollection
    @CollectionTable(name = "bulk_request_photos", joinColumns = @JoinColumn(name = "bulk_request_id"))
    @Column(name = "photo_url")
    private List<String> photoUrls;
    
    // Constructors
    public BulkRequest() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public BulkRequest(User user, BulkCategory category, String description, 
                      String streetAddress, String city, String zipCode) {
        this();
        this.user = user;
        this.category = category;
        this.description = description;
        this.streetAddress = streetAddress;
        this.city = city;
        this.zipCode = zipCode;
        this.requestId = generateRequestId();
    }
    
    // Utility method to generate unique request ID
    private String generateRequestId() {
        return "BULK-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }
    
    // Calculate total amount
    public void calculateTotalAmount() {
        this.taxAmount = (this.basePrice + this.processingFee) * 0.05; // 5% GST
        this.totalAmount = this.basePrice + this.processingFee + this.taxAmount;
    }
    
    // Update timestamp before persisting
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
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
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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
        return "BulkRequest{" +
                "id=" + id +
                ", requestId='" + requestId + '\'' +
                ", category=" + category +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", createdAt=" + createdAt +
                '}';
    }
}
