package com.sliit.smartbin.smartbin.service;

import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.model.Route;
import com.sliit.smartbin.smartbin.model.User;

import java.util.List;

/**
 * Service interface for Notification operations following SOLID principles
 * Single Responsibility: Handles only notification-related operations
 * Open/Closed: Can be extended without modification
 * Liskov Substitution: Implementations can be substituted
 * Interface Segregation: Focused interface for notification operations
 * Dependency Inversion: Depends on abstractions
 */
public interface NotificationService {
    
    /**
     * Send route notification to collector
     * @param collector Collector user
     * @param route Route assigned
     */
    void sendRouteNotification(User collector, Route route);
    
    /**
     * Send region assignment notification to collector
     * @param collector Collector user
     * @param region Assigned region
     */
    void sendRegionAssignmentNotification(User collector, String region);
    
    /**
     * Send bin alert notification
     * @param bin Bin with alert
     */
    void sendBinAlertNotification(Bin bin);
    
    /**
     * Send collection completion notification
     * @param collector Collector user
     * @param bin Collected bin
     */
    void sendCollectionCompletionNotification(User collector, Bin bin);
    
    /**
     * Send overdue bin notification to authority
     * @param bin Overdue bin
     */
    void sendOverdueBinNotification(Bin bin);
    
    /**
     * Send system status notification
     * @param message Notification message
     * @param recipients List of recipient users
     */
    void sendSystemNotification(String message, List<User> recipients);
}
