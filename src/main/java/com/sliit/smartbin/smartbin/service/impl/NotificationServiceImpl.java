package com.sliit.smartbin.smartbin.service.impl;

import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.model.Route;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Implementation of NotificationService following SOLID principles
 * Single Responsibility: Handles only notification-related operations
 * Open/Closed: Can be extended without modification
 * Liskov Substitution: Implements NotificationService interface correctly
 * Interface Segregation: Implements focused interface
 * Dependency Inversion: Depends on abstractions
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public void sendRouteNotification(User collector, Route route) {
        try {
            String message = String.format(
                "New route assigned! Route ID: %d, Bins: %d, Estimated Duration: %d minutes",
                route.getId(),
                route.getRouteBins().size(),
                route.getEstimatedDurationMinutes()
            );
            
            logger.info("Route notification sent to collector {}: {}", collector.getName(), message);
            
            // In a real implementation, this would integrate with:
            // - Push notification services (FCM, APNS)
            // - SMS services
            // - Email services
            // - WebSocket connections for real-time updates
            
            // For now, we'll just log the notification
            logNotification("ROUTE_ASSIGNMENT", collector.getEmail(), message);
            
        } catch (Exception e) {
            logger.error("Failed to send route notification to collector {}: {}", 
                        collector.getName(), e.getMessage());
        }
    }

    @Override
    public void sendRegionAssignmentNotification(User collector, String region) {
        try {
            String message = String.format(
                "You have been assigned to %s region. Please check your dashboard for updates.",
                region
            );
            
            logger.info("Region assignment notification sent to collector {}: {}", 
                       collector.getName(), message);
            
            logNotification("REGION_ASSIGNMENT", collector.getEmail(), message);
            
        } catch (Exception e) {
            logger.error("Failed to send region assignment notification to collector {}: {}", 
                        collector.getName(), e.getMessage());
        }
    }

    @Override
    public void sendBinAlertNotification(Bin bin) {
        try {
            String message = String.format(
                "Bin Alert: %s at %s is %s%% full and requires immediate attention!",
                bin.getQrCode(),
                bin.getLocation(),
                bin.getFillLevel()
            );
            
            logger.warn("Bin alert notification: {}", message);
            
            // Send to relevant authorities and collectors
            logNotification("BIN_ALERT", "authority@smartbin.com", message);
            
        } catch (Exception e) {
            logger.error("Failed to send bin alert notification for bin {}: {}", 
                        bin.getQrCode(), e.getMessage());
        }
    }

    @Override
    public void sendCollectionCompletionNotification(User collector, Bin bin) {
        try {
            String message = String.format(
                "Collection completed: Bin %s at %s has been emptied successfully.",
                bin.getQrCode(),
                bin.getLocation()
            );
            
            logger.info("Collection completion notification: {}", message);
            
            logNotification("COLLECTION_COMPLETED", collector.getEmail(), message);
            
        } catch (Exception e) {
            logger.error("Failed to send collection completion notification: {}", e.getMessage());
        }
    }

    @Override
    public void sendOverdueBinNotification(Bin bin) {
        try {
            String message = String.format(
                "Overdue Bin Alert: %s at %s has not been emptied for over 48 hours!",
                bin.getQrCode(),
                bin.getLocation()
            );
            
            logger.warn("Overdue bin notification: {}", message);
            
            logNotification("OVERDUE_BIN", "authority@smartbin.com", message);
            
        } catch (Exception e) {
            logger.error("Failed to send overdue bin notification for bin {}: {}", 
                        bin.getQrCode(), e.getMessage());
        }
    }

    @Override
    public void sendSystemNotification(String message, List<User> recipients) {
        try {
            logger.info("System notification sent to {} recipients: {}", recipients.size(), message);
            
            for (User recipient : recipients) {
                logNotification("SYSTEM_NOTIFICATION", recipient.getEmail(), message);
            }
            
        } catch (Exception e) {
            logger.error("Failed to send system notification: {}", e.getMessage());
        }
    }

    /**
     * Log notification for audit purposes
     * In a real implementation, this would be stored in a database
     */
    private void logNotification(String type, String recipient, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("[{}] {} -> {}: {}", timestamp, type, recipient, message);
        
        // In a real implementation, you would:
        // 1. Store notification in database
        // 2. Send push notification via FCM/APNS
        // 3. Send SMS if configured
        // 4. Send email notification
        // 5. Update WebSocket connections for real-time updates
    }
}
