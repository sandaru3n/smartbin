package com.sliit.smartbin.smartbin.service;

import com.sliit.smartbin.smartbin.dto.BulkRequestDTO;
import com.sliit.smartbin.smartbin.model.BulkRequest;
import com.sliit.smartbin.smartbin.model.BulkRequestStatus;
import com.sliit.smartbin.smartbin.model.PaymentStatus;
import com.sliit.smartbin.smartbin.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BulkRequestService {
    
    // Create new bulk request
    BulkRequestDTO createBulkRequest(BulkRequestDTO bulkRequestDTO, User user);
    
    // Get bulk request by ID
    Optional<BulkRequestDTO> getBulkRequestById(Long id);
    
    // Get bulk request by request ID
    Optional<BulkRequestDTO> getBulkRequestByRequestId(String requestId);
    
    // Get all bulk requests for a user
    List<BulkRequestDTO> getBulkRequestsByUser(User user);
    
    // Get all bulk requests by status
    List<BulkRequestDTO> getBulkRequestsByStatus(BulkRequestStatus status);
    
    // Get all bulk requests by payment status
    List<BulkRequestDTO> getBulkRequestsByPaymentStatus(PaymentStatus paymentStatus);
    
    // Get pending requests (for authority approval)
    List<BulkRequestDTO> getPendingRequests();
    
    // Get requests requiring collector assignment
    List<BulkRequestDTO> getRequestsRequiringCollectorAssignment();
    
    // Get requests by collector
    List<BulkRequestDTO> getBulkRequestsByCollector(Long collectorId);
    
    // Get requests scheduled for a specific date
    List<BulkRequestDTO> getRequestsScheduledForDate(LocalDateTime date);
    
    // Get requests by date range
    List<BulkRequestDTO> getRequestsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    // Update bulk request status
    BulkRequestDTO updateRequestStatus(Long requestId, BulkRequestStatus status);
    
    // Update payment status
    BulkRequestDTO updatePaymentStatus(Long requestId, PaymentStatus paymentStatus, String paymentReference);
    
    // Assign collector to request
    BulkRequestDTO assignCollector(Long requestId, Long collectorId);
    
    // Schedule collection
    BulkRequestDTO scheduleCollection(Long requestId, LocalDateTime scheduledDate);
    
    // Complete collection
    BulkRequestDTO completeCollection(Long requestId, String notes);
    
    // Cancel request
    BulkRequestDTO cancelRequest(Long requestId, String reason);
    
    // Calculate fee for category
    BulkRequestDTO calculateFee(BulkRequestDTO bulkRequestDTO);
    
    // Get statistics
    long getRequestCountByStatus(BulkRequestStatus status);
    long getRequestCountByUser(User user);
    long getRequestCountByPaymentStatus(PaymentStatus paymentStatus);
    
    // Get recent requests
    List<BulkRequestDTO> getRecentRequests(int days);
    
    // Process payment
    BulkRequestDTO processPayment(Long requestId, String paymentMethod, String paymentReference);
    
    // Validate request data
    void validateBulkRequest(BulkRequestDTO bulkRequestDTO);
    
    // Convert entity to DTO
    BulkRequestDTO convertToDTO(BulkRequest bulkRequest);
    
    // Convert DTO to entity
    BulkRequest convertToEntity(BulkRequestDTO bulkRequestDTO, User user);
}
