package com.sliit.smartbin.smartbin.service.impl;

import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.model.WasteDisposal;
import com.sliit.smartbin.smartbin.repository.BinRepository;
import com.sliit.smartbin.smartbin.repository.WasteDisposalRepository;
import com.sliit.smartbin.smartbin.service.BinService;
import com.sliit.smartbin.smartbin.service.NotificationService;
import com.sliit.smartbin.smartbin.service.WasteDisposalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SOLID PRINCIPLES APPLIED IN WASTE DISPOSAL SERVICE IMPLEMENTATION
 * 
 * S - Single Responsibility Principle (SRP):
 *     This class ONLY handles waste disposal submission and validation.
 *     Bin updates delegated to BinService, notifications to NotificationService.
 * 
 * O - Open/Closed Principle (OCP):
 *     Open for extension (can add new validation rules via methods)
 *     Closed for modification (core disposal logic remains stable).
 * 
 * D - Dependency Inversion Principle (DIP):
 *     Depends on Repository and Service interfaces, not concrete implementations.
 *     BinService and NotificationService can be swapped without changes here.
 */
@Service
public class WasteDisposalServiceImpl implements WasteDisposalService {
    
    // DIP: Depend on abstractions (interfaces), not concrete classes
    private final WasteDisposalRepository wasteDisposalRepository;
    private final BinRepository binRepository;
    private final BinService binService;
    private final NotificationService notificationService;
    
    // OCP: Configuration constants can be extended without modifying core logic
    private static final int FULL_THRESHOLD = 80;
    private static final int MAX_RETRIES = 3;
    
    // DIP: Constructor injection promotes loose coupling and testability
    public WasteDisposalServiceImpl(WasteDisposalRepository wasteDisposalRepository,
                                    BinRepository binRepository,
                                    BinService binService,
                                    NotificationService notificationService) {
        this.wasteDisposalRepository = wasteDisposalRepository;
        this.binRepository = binRepository;
        this.binService = binService;
        this.notificationService = notificationService;
    }
    
    @Override
    @Transactional
    public WasteDisposal submitDisposal(User user, String binQrCode, Integer fillLevel, String notes) {
        int retryCount = 0;
        Exception lastException = null;
        
        // Retry logic for backend failures
        while (retryCount < MAX_RETRIES) {
            try {
                // Validate QR code
                if (!validateBinQrCode(binQrCode)) {
                    throw new RuntimeException("Invalid bin QR code");
                }
                
                // Find bin
                Bin bin = binRepository.findByQrCode(binQrCode)
                        .orElseThrow(() -> new RuntimeException("Bin not found"));
                
                // Create disposal record
                WasteDisposal disposal = new WasteDisposal();
                disposal.setUser(user);
                disposal.setBin(bin);
                disposal.setReportedFillLevel(fillLevel);
                disposal.setNotes(notes);
                disposal.setStatus(WasteDisposal.DisposalStatus.SUBMITTED);
                
                // Save disposal
                WasteDisposal savedDisposal = wasteDisposalRepository.save(disposal);
                
                // SRP: Bin update logic is in BinService, not here
                binService.updateBinFillLevel(bin.getId(), fillLevel);
                
                // Confirm disposal
                savedDisposal.setStatus(WasteDisposal.DisposalStatus.CONFIRMED);
                wasteDisposalRepository.save(savedDisposal);
                
                // Check if bin needs collection
                if (checkIfBinNeedsCollection(bin.getId())) {
                    // SRP: Notification logic delegated to NotificationService
                    sendCollectionNotification(bin);
                }
                
                return savedDisposal;
                
            } catch (Exception e) {
                lastException = e;
                retryCount++;
                
                // Log error (in production, use proper logging framework)
                System.err.println("Error submitting disposal (attempt " + retryCount + "): " + e.getMessage());
                
                if (retryCount < MAX_RETRIES) {
                    try {
                        Thread.sleep(1000 * retryCount); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        // All retries failed - throw exception
        String errorMessage = "Unable to submit disposal";
        if (lastException != null) {
            if (lastException.getMessage().contains("Bin not found")) {
                errorMessage = "Bin not found. Please check the QR code and try again.";
            } else {
                errorMessage = lastException.getMessage();
            }
        }
        throw new RuntimeException(errorMessage);
    }
    
    @Override
    public List<WasteDisposal> getUserDisposals(User user) {
        return wasteDisposalRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    @Override
    public boolean validateBinQrCode(String qrCode) {
        // Validate format: QR followed by 3-4 digits
        if (qrCode == null || qrCode.isEmpty()) {
            return false;
        }
        return qrCode.matches("QR\\d{3,4}");
    }
    
    @Override
    public boolean checkIfBinNeedsCollection(Long binId) {
        return binRepository.findById(binId)
                .map(bin -> bin.getFillLevel() >= FULL_THRESHOLD)
                .orElse(false);
    }
    
    private void sendCollectionNotification(Bin bin) {
        try {
            notificationService.sendBinAlertNotification(bin);
        } catch (Exception e) {
            // Retry notification in background
            System.err.println("Failed to send notification for bin " + bin.getQrCode() + ": " + e.getMessage());
            // In production, implement retry queue
        }
    }
}

