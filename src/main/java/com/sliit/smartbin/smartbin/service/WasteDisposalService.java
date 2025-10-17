package com.sliit.smartbin.smartbin.service;

import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.model.WasteDisposal;

import java.util.List;

/**
 * SOLID PRINCIPLES APPLIED IN WASTE DISPOSAL SERVICE
 * 
 * S - Single Responsibility Principle (SRP):
 *     This service has ONE responsibility: Manage waste disposal reporting.
 *     Doesn't handle recycling, bin creation, or route management.
 * 
 * I - Interface Segregation Principle (ISP):
 *     This interface is focused only on waste disposal operations.
 *     Clients using disposal features don't need recycling or collection methods.
 * 
 * D - Dependency Inversion Principle (DIP):
 *     High-level modules (controllers) depend on this interface abstraction,
 *     not on the concrete implementation class.
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

