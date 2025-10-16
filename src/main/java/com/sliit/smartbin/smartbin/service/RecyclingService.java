package com.sliit.smartbin.smartbin.service;

import com.sliit.smartbin.smartbin.model.RecyclingTransaction;
import com.sliit.smartbin.smartbin.model.User;

import java.util.List;

/**
 * Service interface for managing recycling operations
 * Following Interface Segregation Principle - focused interface for recycling operations
 */
public interface RecyclingService {
    
    /**
     * Process a recycling transaction
     * @param user The user performing the recycling
     * @param recyclingUnitQrCode QR code of the recycling unit
     * @param itemType Type of recyclable item
     * @param weight Weight of the item in kg
     * @param quantity Quantity of items
     * @return The created recycling transaction
     */
    RecyclingTransaction processRecyclingTransaction(User user, String recyclingUnitQrCode, 
                                                     String itemType, Double weight, Integer quantity);
    
    /**
     * Calculate points for a recycling transaction
     * @param itemType Type of recyclable item
     * @param weight Weight in kg
     * @param quantity Quantity of items
     * @return Points earned
     */
    Double calculatePoints(String itemType, Double weight, Integer quantity);
    
    /**
     * Calculate monetary value for recycling
     * @param itemType Type of recyclable item
     * @param weight Weight in kg
     * @return Price value
     */
    Double calculatePrice(String itemType, Double weight);
    
    /**
     * Get user's recycling transaction history
     * @param user The user
     * @return List of transactions
     */
    List<RecyclingTransaction> getUserTransactions(User user);
    
    /**
     * Confirm a pending transaction
     * @param transactionId Transaction ID
     * @return Updated transaction
     */
    RecyclingTransaction confirmTransaction(Long transactionId);
    
    /**
     * Get nearby recycling units (mock data)
     * @param latitude User's latitude
     * @param longitude User's longitude
     * @param radius Search radius in km
     * @return List of recycling unit locations
     */
    List<RecyclingUnitLocation> getNearbyRecyclingUnits(Double latitude, Double longitude, Double radius);
    
    /**
     * Inner class to represent recycling unit locations
     */
    class RecyclingUnitLocation {
        private String qrCode;
        private String name;
        private String address;
        private Double latitude;
        private Double longitude;
        private Double distance;
        private List<String> acceptedItems;
        
        public RecyclingUnitLocation(String qrCode, String name, String address, 
                                    Double latitude, Double longitude, Double distance,
                                    List<String> acceptedItems) {
            this.qrCode = qrCode;
            this.name = name;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
            this.distance = distance;
            this.acceptedItems = acceptedItems;
        }
        
        // Getters
        public String getQrCode() { return qrCode; }
        public String getName() { return name; }
        public String getAddress() { return address; }
        public Double getLatitude() { return latitude; }
        public Double getLongitude() { return longitude; }
        public Double getDistance() { return distance; }
        public List<String> getAcceptedItems() { return acceptedItems; }
    }
}

