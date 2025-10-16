package com.sliit.smartbin.smartbin.service.impl;

import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.model.BulkRequest;
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

    @Override
    public void notifyUserBulkRequest(User user, String statusMessage, BulkRequest bulkRequest) {
        try {
            String message = String.format(
                "Bulk Request Update - Request ID: %s\n" +
                "Status: %s\n" +
                "Message: %s\n" +
                "Category: %s\n" +
                "Location: %s, %s",
                bulkRequest.getRequestId(),
                bulkRequest.getStatus().getDisplayName(),
                statusMessage,
                bulkRequest.getCategory().getDisplayName(),
                bulkRequest.getStreetAddress(),
                bulkRequest.getCity()
            );
            
            logger.info("Bulk request notification sent to user {}: {}", user.getName(), statusMessage);
            logNotification("BULK_REQUEST_UPDATE", user.getEmail(), message);
            
        } catch (Exception e) {
            logger.error("Failed to send bulk request notification to user {}: {}", 
                        user.getName(), e.getMessage());
        }
    }

    @Override
    public void notifyAuthorityBulkPayment(BulkRequest bulkRequest) {
        try {
            String message = String.format(
                "New Bulk Collection Payment Received\n" +
                "Request ID: %s\n" +
                "User: %s (%s)\n" +
                "Category: %s\n" +
                "Amount: LKR %.2f\n" +
                "Payment Method: %s\n" +
                "Payment Reference: %s\n" +
                "Location: %s, %s\n" +
                "Status: Payment Completed - Awaiting Collector Assignment",
                bulkRequest.getRequestId(),
                bulkRequest.getUser().getName(),
                bulkRequest.getUser().getEmail(),
                bulkRequest.getCategory().getDisplayName(),
                bulkRequest.getTotalAmount(),
                bulkRequest.getPaymentMethod(),
                bulkRequest.getPaymentReference(),
                bulkRequest.getStreetAddress(),
                bulkRequest.getCity()
            );
            
            logger.info("Bulk request payment notification sent to authority for request: {}", 
                       bulkRequest.getRequestId());
            logNotification("BULK_REQUEST_PAYMENT", "authority@smartbin.com", message);
            
        } catch (Exception e) {
            logger.error("Failed to send bulk payment notification to authority for request {}: {}", 
                        bulkRequest.getRequestId(), e.getMessage());
        }
    }

    @Override
    public void sendPickupScheduleNotification(User user, BulkRequest bulkRequest) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' hh:mm a");
            String scheduledTime = bulkRequest.getScheduledDate() != null ? 
                                  bulkRequest.getScheduledDate().format(formatter) : 
                                  "To be confirmed";
            
            String message = String.format(
                "Bulk Collection Pickup Scheduled!\n\n" +
                "Request ID: %s\n" +
                "Category: %s\n" +
                "Pickup Location: %s, %s, %s\n" +
                "Scheduled Date & Time: %s\n" +
                "Collector: %s\n\n" +
                "Please ensure:\n" +
                "- Items are ready at the specified location\n" +
                "- Access is clear for the collection vehicle\n" +
                "- Someone is available during pickup time\n\n" +
                "Thank you for using SmartBin Bulk Collection Service!",
                bulkRequest.getRequestId(),
                bulkRequest.getCategory().getDisplayName(),
                bulkRequest.getStreetAddress(),
                bulkRequest.getCity(),
                bulkRequest.getZipCode(),
                scheduledTime,
                bulkRequest.getCollectorAssigned() != null ? 
                    "Assigned (ID: " + bulkRequest.getCollectorAssigned() + ")" : 
                    "To be assigned"
            );
            
            logger.info("Pickup schedule notification sent to user {} for request: {}", 
                       user.getName(), bulkRequest.getRequestId());
            logNotification("BULK_PICKUP_SCHEDULE", user.getEmail(), message);
            
        } catch (Exception e) {
            logger.error("Failed to send pickup schedule notification to user {} for request {}: {}", 
                        user.getName(), bulkRequest.getRequestId(), e.getMessage());
        }
    }

    @Override
    public void notifyCollectorBulkAssignment(User collector, BulkRequest bulkRequest) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' hh:mm a");
            String scheduledTime = bulkRequest.getScheduledDate() != null ? 
                                  bulkRequest.getScheduledDate().format(formatter) : 
                                  "Not scheduled yet";
            
            String message = String.format(
                "New Bulk Collection Assignment\n\n" +
                "Request ID: %s\n" +
                "Category: %s\n" +
                "Description: %s\n" +
                "Pickup Location: %s, %s, %s\n" +
                "Coordinates: %.6f, %.6f\n" +
                "Scheduled Date & Time: %s\n" +
                "Estimated Weight: %s kg\n" +
                "Estimated Dimensions: %s\n" +
                "Amount Collected: LKR %.2f\n\n" +
                "Please check your dashboard for complete details and update status upon completion.",
                bulkRequest.getRequestId(),
                bulkRequest.getCategory().getDisplayName(),
                bulkRequest.getDescription(),
                bulkRequest.getStreetAddress(),
                bulkRequest.getCity(),
                bulkRequest.getZipCode(),
                bulkRequest.getLatitude() != null ? bulkRequest.getLatitude() : 0.0,
                bulkRequest.getLongitude() != null ? bulkRequest.getLongitude() : 0.0,
                scheduledTime,
                bulkRequest.getEstimatedWeight() != null ? 
                    String.format("%.2f", bulkRequest.getEstimatedWeight()) : "N/A",
                bulkRequest.getEstimatedDimensions() != null ? 
                    bulkRequest.getEstimatedDimensions() : "N/A",
                bulkRequest.getTotalAmount()
            );
            
            logger.info("Bulk collection assignment notification sent to collector {} for request: {}", 
                       collector.getName(), bulkRequest.getRequestId());
            logNotification("BULK_COLLECTOR_ASSIGNMENT", collector.getEmail(), message);
            
        } catch (Exception e) {
            logger.error("Failed to send bulk assignment notification to collector {} for request {}: {}", 
                        collector.getName(), bulkRequest.getRequestId(), e.getMessage());
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
