package com.sliit.smartbin.smartbin.service.impl;

import com.sliit.smartbin.smartbin.dto.BulkRequestDTO;
import com.sliit.smartbin.smartbin.model.*;
import com.sliit.smartbin.smartbin.repository.BulkRequestRepository;
import com.sliit.smartbin.smartbin.service.BulkRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BulkRequestServiceImpl implements BulkRequestService {
    
    @Autowired
    private BulkRequestRepository bulkRequestRepository;
    
    private static final double PROCESSING_FEE = 500.0; // LKR 500
    private static final double TAX_RATE = 0.05; // 5% GST
    
    @Override
    public BulkRequestDTO createBulkRequest(BulkRequestDTO bulkRequestDTO, User user) {
        validateBulkRequest(bulkRequestDTO);
        
        BulkRequest bulkRequest = convertToEntity(bulkRequestDTO, user);
        
        // Set base price from category
        bulkRequest.setBasePrice(bulkRequest.getCategory().getBasePrice());
        bulkRequest.setProcessingFee(PROCESSING_FEE);
        
        // Calculate total amount
        bulkRequest.calculateTotalAmount();
        
        // Set initial status
        bulkRequest.setStatus(BulkRequestStatus.PENDING);
        bulkRequest.setPaymentStatus(PaymentStatus.PENDING);
        
        BulkRequest savedRequest = bulkRequestRepository.save(bulkRequest);
        return convertToDTO(savedRequest);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<BulkRequestDTO> getBulkRequestById(Long id) {
        return bulkRequestRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<BulkRequestDTO> getBulkRequestByRequestId(String requestId) {
        return bulkRequestRepository.findByRequestId(requestId)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BulkRequestDTO> getBulkRequestsByUser(User user) {
        return bulkRequestRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BulkRequestDTO> getBulkRequestsByStatus(BulkRequestStatus status) {
        return bulkRequestRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BulkRequestDTO> getBulkRequestsByPaymentStatus(PaymentStatus paymentStatus) {
        return bulkRequestRepository.findByPaymentStatusOrderByCreatedAtDesc(paymentStatus)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BulkRequestDTO> getPendingRequests() {
        return bulkRequestRepository.findPendingRequests(BulkRequestStatus.PENDING)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BulkRequestDTO> getRequestsRequiringCollectorAssignment() {
        return bulkRequestRepository.findRequestsRequiringCollectorAssignment()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BulkRequestDTO> getBulkRequestsByCollector(Long collectorId) {
        return bulkRequestRepository.findByCollectorAssignedOrderByScheduledDateAsc(collectorId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BulkRequestDTO> getRequestsScheduledForDate(LocalDateTime date) {
        return bulkRequestRepository.findRequestsScheduledForDate(date)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BulkRequestDTO> getRequestsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return bulkRequestRepository.findRequestsByDateRange(startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public BulkRequestDTO updateRequestStatus(Long requestId, BulkRequestStatus status) {
        BulkRequest bulkRequest = bulkRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Bulk request not found with ID: " + requestId));
        
        bulkRequest.setStatus(status);
        BulkRequest updatedRequest = bulkRequestRepository.save(bulkRequest);
        return convertToDTO(updatedRequest);
    }
    
    @Override
    public BulkRequestDTO updatePaymentStatus(Long requestId, PaymentStatus paymentStatus, String paymentReference) {
        BulkRequest bulkRequest = bulkRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Bulk request not found with ID: " + requestId));
        
        bulkRequest.setPaymentStatus(paymentStatus);
        if (paymentReference != null) {
            bulkRequest.setPaymentReference(paymentReference);
        }
        
        // Update request status based on payment status
        if (paymentStatus == PaymentStatus.COMPLETED) {
            bulkRequest.setStatus(BulkRequestStatus.PAYMENT_COMPLETED);
        } else if (paymentStatus == PaymentStatus.FAILED) {
            bulkRequest.setStatus(BulkRequestStatus.PAYMENT_PENDING);
        }
        
        BulkRequest updatedRequest = bulkRequestRepository.save(bulkRequest);
        return convertToDTO(updatedRequest);
    }
    
    @Override
    public BulkRequestDTO assignCollector(Long requestId, Long collectorId) {
        BulkRequest bulkRequest = bulkRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Bulk request not found with ID: " + requestId));
        
        bulkRequest.setCollectorAssigned(collectorId);
        bulkRequest.setStatus(BulkRequestStatus.COLLECTOR_ASSIGNED);
        
        BulkRequest updatedRequest = bulkRequestRepository.save(bulkRequest);
        return convertToDTO(updatedRequest);
    }
    
    @Override
    public BulkRequestDTO scheduleCollection(Long requestId, LocalDateTime scheduledDate) {
        BulkRequest bulkRequest = bulkRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Bulk request not found with ID: " + requestId));
        
        bulkRequest.setScheduledDate(scheduledDate);
        bulkRequest.setStatus(BulkRequestStatus.SCHEDULED);
        
        BulkRequest updatedRequest = bulkRequestRepository.save(bulkRequest);
        return convertToDTO(updatedRequest);
    }
    
    @Override
    public BulkRequestDTO completeCollection(Long requestId, String notes) {
        BulkRequest bulkRequest = bulkRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Bulk request not found with ID: " + requestId));
        
        bulkRequest.setStatus(BulkRequestStatus.COMPLETED);
        bulkRequest.setCompletedDate(LocalDateTime.now());
        if (notes != null) {
            bulkRequest.setNotes(notes);
        }
        
        BulkRequest updatedRequest = bulkRequestRepository.save(bulkRequest);
        return convertToDTO(updatedRequest);
    }
    
    @Override
    public BulkRequestDTO cancelRequest(Long requestId, String reason) {
        BulkRequest bulkRequest = bulkRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Bulk request not found with ID: " + requestId));
        
        bulkRequest.setStatus(BulkRequestStatus.CANCELLED);
        if (reason != null) {
            bulkRequest.setNotes(reason);
        }
        
        BulkRequest updatedRequest = bulkRequestRepository.save(bulkRequest);
        return convertToDTO(updatedRequest);
    }
    
    @Override
    public BulkRequestDTO calculateFee(BulkRequestDTO bulkRequestDTO) {
        if (bulkRequestDTO.getCategory() == null) {
            throw new IllegalArgumentException("Category is required for fee calculation");
        }
        
        double basePrice = bulkRequestDTO.getCategory().getBasePrice();
        double processingFee = PROCESSING_FEE;
        double tax = (basePrice + processingFee) * TAX_RATE;
        double total = basePrice + processingFee + tax;
        
        bulkRequestDTO.setBasePrice(basePrice);
        bulkRequestDTO.setProcessingFee(processingFee);
        bulkRequestDTO.setTaxAmount(tax);
        bulkRequestDTO.setTotalAmount(total);
        
        return bulkRequestDTO;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getRequestCountByStatus(BulkRequestStatus status) {
        return bulkRequestRepository.countByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getRequestCountByUser(User user) {
        return bulkRequestRepository.countByUser(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getRequestCountByPaymentStatus(PaymentStatus paymentStatus) {
        return bulkRequestRepository.countByPaymentStatus(paymentStatus);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BulkRequestDTO> getRecentRequests(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return bulkRequestRepository.findRecentRequests(cutoffDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public BulkRequestDTO processPayment(Long requestId, String paymentMethod, String paymentReference) {
        // Simulate payment processing
        boolean paymentSuccess = simulatePaymentProcessing(paymentMethod);
        
        if (paymentSuccess) {
            return updatePaymentStatus(requestId, PaymentStatus.COMPLETED, paymentReference);
        } else {
            return updatePaymentStatus(requestId, PaymentStatus.FAILED, null);
        }
    }
    
    private boolean simulatePaymentProcessing(String paymentMethod) {
        // Simulate 90% success rate
        return Math.random() > 0.1;
    }
    
    @Override
    public void validateBulkRequest(BulkRequestDTO bulkRequestDTO) {
        if (bulkRequestDTO.getCategory() == null) {
            throw new IllegalArgumentException("Category is required");
        }
        if (bulkRequestDTO.getDescription() == null || bulkRequestDTO.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (bulkRequestDTO.getStreetAddress() == null || bulkRequestDTO.getStreetAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Street address is required");
        }
        if (bulkRequestDTO.getCity() == null || bulkRequestDTO.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("City is required");
        }
        if (bulkRequestDTO.getZipCode() == null || bulkRequestDTO.getZipCode().trim().isEmpty()) {
            throw new IllegalArgumentException("ZIP code is required");
        }
    }
    
    @Override
    public BulkRequestDTO convertToDTO(BulkRequest bulkRequest) {
        BulkRequestDTO dto = new BulkRequestDTO();
        dto.setId(bulkRequest.getId());
        dto.setRequestId(bulkRequest.getRequestId());
        dto.setUserId(bulkRequest.getUser().getId());
        dto.setUserName(bulkRequest.getUser().getName());
        dto.setUserEmail(bulkRequest.getUser().getEmail());
        dto.setCategory(bulkRequest.getCategory());
        dto.setDescription(bulkRequest.getDescription());
        dto.setStreetAddress(bulkRequest.getStreetAddress());
        dto.setCity(bulkRequest.getCity());
        dto.setZipCode(bulkRequest.getZipCode());
        dto.setLatitude(bulkRequest.getLatitude());
        dto.setLongitude(bulkRequest.getLongitude());
        dto.setEstimatedWeight(bulkRequest.getEstimatedWeight());
        dto.setEstimatedDimensions(bulkRequest.getEstimatedDimensions());
        dto.setBasePrice(bulkRequest.getBasePrice());
        dto.setProcessingFee(bulkRequest.getProcessingFee());
        dto.setTaxAmount(bulkRequest.getTaxAmount());
        dto.setTotalAmount(bulkRequest.getTotalAmount());
        dto.setStatus(bulkRequest.getStatus());
        dto.setPaymentStatus(bulkRequest.getPaymentStatus());
        dto.setPaymentMethod(bulkRequest.getPaymentMethod());
        dto.setPaymentReference(bulkRequest.getPaymentReference());
        dto.setCollectorAssigned(bulkRequest.getCollectorAssigned());
        dto.setScheduledDate(bulkRequest.getScheduledDate());
        dto.setCompletedDate(bulkRequest.getCompletedDate());
        dto.setNotes(bulkRequest.getNotes());
        dto.setCreatedAt(bulkRequest.getCreatedAt());
        dto.setUpdatedAt(bulkRequest.getUpdatedAt());
        dto.setPhotoUrls(bulkRequest.getPhotoUrls());
        return dto;
    }
    
    @Override
    public BulkRequest convertToEntity(BulkRequestDTO bulkRequestDTO, User user) {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.setUser(user);
        bulkRequest.setCategory(bulkRequestDTO.getCategory());
        bulkRequest.setDescription(bulkRequestDTO.getDescription());
        bulkRequest.setStreetAddress(bulkRequestDTO.getStreetAddress());
        bulkRequest.setCity(bulkRequestDTO.getCity());
        bulkRequest.setZipCode(bulkRequestDTO.getZipCode());
        bulkRequest.setLatitude(bulkRequestDTO.getLatitude());
        bulkRequest.setLongitude(bulkRequestDTO.getLongitude());
        bulkRequest.setEstimatedWeight(bulkRequestDTO.getEstimatedWeight());
        bulkRequest.setEstimatedDimensions(bulkRequestDTO.getEstimatedDimensions());
        bulkRequest.setPhotoUrls(bulkRequestDTO.getPhotoUrls());
        return bulkRequest;
    }
}
