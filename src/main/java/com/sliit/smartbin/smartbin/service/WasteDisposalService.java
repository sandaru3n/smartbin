package com.sliit.smartbin.smartbin.service;

import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.model.WasteDisposal;

import java.util.List;

/**
 * Service interface for managing waste disposal operations
 * Following Single Responsibility Principle - handles only waste disposal logic
 */
public interface WasteDisposalService {
    
    /**
     * Submit a waste disposal form
     * @param user The user disposing waste
     * @param binQrCode QR code of the bin
     * @param fillLevel Reported fill level (0-100)
     * @param notes Optional notes
     * @return Created waste disposal record
     */
    WasteDisposal submitDisposal(User user, String binQrCode, Integer fillLevel, String notes);
    
    /**
     * Get user's disposal history
     * @param user The user
     * @return List of waste disposals
     */
    List<WasteDisposal> getUserDisposals(User user);
    
    /**
     * Validate bin QR code
     * @param qrCode QR code to validate
     * @return true if valid, false otherwise
     */
    boolean validateBinQrCode(String qrCode);
    
    /**
     * Check if bin is full after disposal
     * @param binId Bin ID
     * @return true if bin needs collection
     */
    boolean checkIfBinNeedsCollection(Long binId);
}

