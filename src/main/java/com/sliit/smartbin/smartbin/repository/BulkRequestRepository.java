package com.sliit.smartbin.smartbin.repository;

import com.sliit.smartbin.smartbin.model.BulkRequest;
import com.sliit.smartbin.smartbin.model.BulkRequestStatus;
import com.sliit.smartbin.smartbin.model.PaymentStatus;
import com.sliit.smartbin.smartbin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BulkRequestRepository extends JpaRepository<BulkRequest, Long> {
    
    // Find by request ID
    Optional<BulkRequest> findByRequestId(String requestId);
    
    // Find by user
    List<BulkRequest> findByUserOrderByCreatedAtDesc(User user);
    
    // Find by status
    List<BulkRequest> findByStatusOrderByCreatedAtDesc(BulkRequestStatus status);
    
    // Find by payment status
    List<BulkRequest> findByPaymentStatusOrderByCreatedAtDesc(PaymentStatus paymentStatus);
    
    // Find by user and status
    List<BulkRequest> findByUserAndStatusOrderByCreatedAtDesc(User user, BulkRequestStatus status);
    
    // Find pending requests (for authority approval)
    @Query("SELECT br FROM BulkRequest br WHERE br.status = :status ORDER BY br.createdAt ASC")
    List<BulkRequest> findPendingRequests(@Param("status") BulkRequestStatus status);
    
    // Find requests requiring collector assignment
    @Query("SELECT br FROM BulkRequest br WHERE br.status = 'PAYMENT_COMPLETED' AND br.collectorAssigned IS NULL ORDER BY br.createdAt ASC")
    List<BulkRequest> findRequestsRequiringCollectorAssignment();
    
    // Find requests by collector
    List<BulkRequest> findByCollectorAssignedOrderByScheduledDateAsc(Long collectorId);
    
    // Find requests scheduled for today
    @Query("SELECT br FROM BulkRequest br WHERE DATE(br.scheduledDate) = DATE(:date) ORDER BY br.scheduledDate ASC")
    List<BulkRequest> findRequestsScheduledForDate(@Param("date") LocalDateTime date);
    
    // Find requests by date range
    @Query("SELECT br FROM BulkRequest br WHERE br.createdAt BETWEEN :startDate AND :endDate ORDER BY br.createdAt DESC")
    List<BulkRequest> findRequestsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Count requests by status
    long countByStatus(BulkRequestStatus status);
    
    // Count requests by user
    long countByUser(User user);
    
    // Count requests by payment status
    long countByPaymentStatus(PaymentStatus paymentStatus);
    
    // Find recent requests (last 30 days)
    @Query("SELECT br FROM BulkRequest br WHERE br.createdAt >= :date ORDER BY br.createdAt DESC")
    List<BulkRequest> findRecentRequests(@Param("date") LocalDateTime date);
    
    // Find requests with pending payment
    @Query("SELECT br FROM BulkRequest br WHERE br.paymentStatus = 'PENDING' AND br.createdAt < :expiryDate")
    List<BulkRequest> findExpiredPaymentRequests(@Param("expiryDate") LocalDateTime expiryDate);
    
    // Find requests by location (within radius)
    @Query("SELECT br FROM BulkRequest br WHERE " +
           "6371 * acos(cos(radians(:lat)) * cos(radians(br.latitude)) * " +
           "cos(radians(br.longitude) - radians(:lng)) + " +
           "sin(radians(:lat)) * sin(radians(br.latitude))) <= :radius " +
           "ORDER BY br.createdAt DESC")
    List<BulkRequest> findRequestsByLocation(@Param("lat") double latitude, 
                                           @Param("lng") double longitude, 
                                           @Param("radius") double radiusKm);
}
