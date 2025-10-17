package com.sliit.smartbin.smartbin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bins")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bin {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String qrCode;
    
    @Column(nullable = false)
    private String location;
    
    @Column(nullable = false)
    private Double latitude;
    
    @Column(nullable = false)
    private Double longitude;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BinType binType;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BinStatus status;
    
    @Column(nullable = false)
    private Integer fillLevel; // Percentage 0-100
    
    @Column(name = "last_emptied")
    private LocalDateTime lastEmptied;
    
    @Column(name = "alert_flag")
    private Boolean alertFlag = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum BinType {
        STANDARD,
        RECYCLING,
        BULK
    }
    
    public enum BinStatus {
        EMPTY,
        PARTIAL,
        FULL,
        OVERDUE
    }
    
    // ===================================================================
    // CODE SMELL FIXES: Feature Envy & Law of Demeter
    // ===================================================================
    
    /**
     * CODE SMELL FIX: Feature Envy
     * BEFORE: Controllers checking bin status directly with if-else chains
     * AFTER: Behavior encapsulated in Bin class where it belongs
     * 
     * Check if bin needs collection
     * @return true if bin is full, overdue, or has alert flag
     */
    public boolean needsCollection() {
        return status == BinStatus.FULL ||
               status == BinStatus.OVERDUE ||
               (alertFlag != null && alertFlag);
    }
    
    /**
     * CODE SMELL FIX: Switch/If-Else Chains
     * BEFORE: Multiple if-else statements checking fill level in controllers
     * AFTER: Single method encapsulating status determination logic
     * 
     * Determine bin status based on fill level
     */
    public void updateStatusBasedOnFillLevel() {
        if (fillLevel >= 90) {
            this.status = BinStatus.FULL;
        } else if (fillLevel >= 50) {
            this.status = BinStatus.PARTIAL;
        } else {
            this.status = BinStatus.EMPTY;
        }
    }
    
    /**
     * CODE SMELL FIX: Shotgun Surgery
     * BEFORE: Marking bin as full required changes in multiple places
     * AFTER: Centralized method - one place to change
     * 
     * Mark bin as full and set alert
     */
    public void markAsFull() {
        this.status = BinStatus.FULL;
        this.alertFlag = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * CODE SMELL FIX: Shotgun Surgery
     * BEFORE: Emptying bin logic scattered across controllers and services
     * AFTER: Single method with all empty logic
     * 
     * Empty the bin and update timestamps
     */
    public void markAsEmptied() {
        this.status = BinStatus.EMPTY;
        this.fillLevel = 0;
        this.lastEmptied = LocalDateTime.now();
        this.alertFlag = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * CODE SMELL FIX: Long Method in controllers
     * BEFORE: Controllers had complex overdue checking logic
     * AFTER: Simple method in domain model
     * 
     * Check if bin is overdue for collection (>48 hours since full)
     */
    public boolean isOverdue() {
        if (status != BinStatus.FULL || lastEmptied == null) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        long hoursSinceEmptied = java.time.Duration.between(lastEmptied, now).toHours();
        return hoursSinceEmptied > 48;
    }
    
    /**
     * CODE SMELL FIX: Feature Envy
     * BEFORE: Controllers calculating fill percentage for UI display
     * AFTER: Bin knows how to represent itself
     * 
     * Get user-friendly status description
     */
    public String getStatusDescription() {
        if (isOverdue()) {
            return "Overdue (" + fillLevel + "% full for >48 hours)";
        }
        
        return switch (status) {
            case EMPTY -> "Empty (" + fillLevel + "%)";
            case PARTIAL -> "Partially Full (" + fillLevel + "%)";
            case FULL -> "Full (" + fillLevel + "%)";
            case OVERDUE -> "Overdue for Collection";
        };
    }
}

