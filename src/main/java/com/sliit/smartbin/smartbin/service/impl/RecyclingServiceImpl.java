package com.sliit.smartbin.smartbin.service.impl;

import com.sliit.smartbin.smartbin.model.RecyclingTransaction;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.repository.RecyclingTransactionRepository;
import com.sliit.smartbin.smartbin.repository.UserRepository;
import com.sliit.smartbin.smartbin.service.RecyclingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of RecyclingService
 * Following Dependency Inversion Principle - depends on repository abstractions
 */
@Service
public class RecyclingServiceImpl implements RecyclingService {
    
    private final RecyclingTransactionRepository recyclingTransactionRepository;
    private final UserRepository userRepository;
    
    // Mock data for recycling rates (points per kg)
    private static final Map<String, Double> RECYCLING_RATES = new HashMap<>();
    private static final Map<String, Double> PRICE_RATES = new HashMap<>();
    
    static {
        // Points per kg
        RECYCLING_RATES.put("plastic", 10.0);
        RECYCLING_RATES.put("paper", 5.0);
        RECYCLING_RATES.put("metal", 20.0);
        RECYCLING_RATES.put("glass", 15.0);
        RECYCLING_RATES.put("cardboard", 7.0);
        RECYCLING_RATES.put("electronics", 50.0);
        
        // Price per kg in currency units
        PRICE_RATES.put("plastic", 50.0);
        PRICE_RATES.put("paper", 30.0);
        PRICE_RATES.put("metal", 100.0);
        PRICE_RATES.put("glass", 40.0);
        PRICE_RATES.put("cardboard", 35.0);
        PRICE_RATES.put("electronics", 200.0);
    }
    
    public RecyclingServiceImpl(RecyclingTransactionRepository recyclingTransactionRepository,
                                UserRepository userRepository) {
        this.recyclingTransactionRepository = recyclingTransactionRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    @Transactional
    public RecyclingTransaction processRecyclingTransaction(User user, String recyclingUnitQrCode,
                                                            String itemType, Double weight, Integer quantity) {
        try {
            // Calculate points and price
            Double points = calculatePoints(itemType, weight, quantity);
            Double price = calculatePrice(itemType, weight);
            
            // Create transaction
            RecyclingTransaction transaction = new RecyclingTransaction();
            transaction.setUser(user);
            transaction.setRecyclingUnitQrCode(recyclingUnitQrCode);
            transaction.setItemType(itemType);
            transaction.setWeight(weight);
            transaction.setQuantity(quantity);
            transaction.setPointsEarned(points);
            transaction.setPriceValue(price);
            transaction.setStatus(RecyclingTransaction.TransactionStatus.PENDING);
            
            // Save transaction
            RecyclingTransaction savedTransaction = recyclingTransactionRepository.save(transaction);
            
            // Auto-confirm and credit points
            return confirmTransaction(savedTransaction.getId());
            
        } catch (Exception e) {
            // Error handling - create failed transaction
            RecyclingTransaction failedTransaction = new RecyclingTransaction();
            failedTransaction.setUser(user);
            failedTransaction.setRecyclingUnitQrCode(recyclingUnitQrCode);
            failedTransaction.setItemType(itemType);
            failedTransaction.setWeight(weight);
            failedTransaction.setQuantity(quantity);
            failedTransaction.setPointsEarned(0.0);
            failedTransaction.setPriceValue(0.0);
            failedTransaction.setStatus(RecyclingTransaction.TransactionStatus.FAILED);
            
            return recyclingTransactionRepository.save(failedTransaction);
        }
    }
    
    @Override
    public Double calculatePoints(String itemType, Double weight, Integer quantity) {
        Double pointsPerKg = RECYCLING_RATES.getOrDefault(itemType.toLowerCase(), 5.0);
        Double basePoints = weight * pointsPerKg;
        
        // Bonus points for quantity
        if (quantity != null && quantity > 10) {
            basePoints *= 1.1; // 10% bonus for bulk recycling
        }
        
        return Math.round(basePoints * 100.0) / 100.0;
    }
    
    @Override
    public Double calculatePrice(String itemType, Double weight) {
        Double pricePerKg = PRICE_RATES.getOrDefault(itemType.toLowerCase(), 25.0);
        Double totalPrice = weight * pricePerKg;
        
        return Math.round(totalPrice * 100.0) / 100.0;
    }
    
    @Override
    public List<RecyclingTransaction> getUserTransactions(User user) {
        return recyclingTransactionRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    @Override
    @Transactional
    public RecyclingTransaction confirmTransaction(Long transactionId) {
        RecyclingTransaction transaction = recyclingTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (transaction.getStatus() == RecyclingTransaction.TransactionStatus.PENDING) {
            // Update transaction status
            transaction.setStatus(RecyclingTransaction.TransactionStatus.CONFIRMED);
            
            // Credit points to user
            User user = transaction.getUser();
            Double currentPoints = user.getRecyclingPoints() != null ? user.getRecyclingPoints() : 0.0;
            user.setRecyclingPoints(currentPoints + transaction.getPointsEarned());
            userRepository.save(user);
            
            return recyclingTransactionRepository.save(transaction);
        }
        
        return transaction;
    }
    
    @Override
    public List<RecyclingUnitLocation> getNearbyRecyclingUnits(Double latitude, Double longitude, Double radius) {
        // Mock data for recycling units
        return Arrays.asList(
            new RecyclingUnitLocation(
                "RU001",
                "Colombo Central Recycling Hub",
                "123 Main Street, Colombo 01",
                6.9271,
                79.8612,
                0.5,
                Arrays.asList("plastic", "paper", "glass", "metal", "cardboard")
            ),
            new RecyclingUnitLocation(
                "RU002",
                "Green Point Recycling Center",
                "456 Green Road, Colombo 03",
                6.9085,
                79.8553,
                1.2,
                Arrays.asList("plastic", "paper", "electronics")
            ),
            new RecyclingUnitLocation(
                "RU003",
                "Eco Recycling Station",
                "789 Park Avenue, Colombo 05",
                6.8887,
                79.8570,
                2.3,
                Arrays.asList("glass", "metal", "electronics", "cardboard")
            ),
            new RecyclingUnitLocation(
                "RU004",
                "Smart Recycle Point",
                "321 Lake Road, Colombo 02",
                6.9349,
                79.8538,
                1.8,
                Arrays.asList("plastic", "paper", "glass", "metal")
            ),
            new RecyclingUnitLocation(
                "RU005",
                "Community Recycling Hub",
                "555 Beach Road, Colombo 06",
                6.8812,
                79.8608,
                3.1,
                Arrays.asList("paper", "cardboard", "plastic")
            )
        );
    }
}

