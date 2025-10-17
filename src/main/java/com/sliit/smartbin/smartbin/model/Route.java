package com.sliit.smartbin.smartbin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "routes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String routeName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collector_id", nullable = false)
    private User collector;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authority_id", nullable = false)
    private User authority;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RouteStatus status;
    
    @Column(name = "assigned_date")
    private LocalDateTime assignedDate;
    
    @Column(name = "started_date")
    private LocalDateTime startedDate;
    
    @Column(name = "completed_date")
    private LocalDateTime completedDate;
    
    @Column(name = "estimated_duration")
    private Integer estimatedDurationMinutes;
    
    @Column(name = "actual_duration")
    private Integer actualDurationMinutes;
    
    @Column(name = "total_distance")
    private Double totalDistanceKm;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RouteBin> routeBins;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum RouteStatus {
        ASSIGNED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
    
    // ===================================================================
    // CODE SMELL FIXES: Law of Demeter & Feature Envy
    // ===================================================================
    
    /**
     * CODE SMELL FIX: Law of Demeter Violation
     * BEFORE: if (!route.getCollector().getId().equals(user.getId()))
     * AFTER: if (!route.isAssignedTo(user))
     * 
     * BENEFIT: No reaching through multiple objects
     * 
     * Check if route is assigned to specific collector
     * @param collector Collector to check
     * @return true if route is assigned to this collector
     */
    public boolean isAssignedTo(User collector) {
        return this.collector != null && 
               collector != null &&
               this.collector.getId().equals(collector.getId());
    }
    
    /**
     * CODE SMELL FIX: Shotgun Surgery
     * BEFORE: Starting route logic scattered across controller and service
     * AFTER: Centralized in one place
     * 
     * Start the route
     */
    public void start() {
        if (this.status != RouteStatus.ASSIGNED) {
            throw new IllegalStateException("Can only start assigned routes");
        }
        this.status = RouteStatus.IN_PROGRESS;
        this.startedDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * CODE SMELL FIX: Shotgun Surgery
     * BEFORE: Completing route logic in multiple places
     * AFTER: Single method
     * 
     * Complete the route
     */
    public void complete() {
        if (this.status != RouteStatus.IN_PROGRESS) {
            throw new IllegalStateException("Can only complete in-progress routes");
        }
        this.status = RouteStatus.COMPLETED;
        this.completedDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        // Calculate actual duration
        if (this.startedDate != null) {
            long minutes = java.time.Duration.between(this.startedDate, this.completedDate).toMinutes();
            this.actualDurationMinutes = (int) minutes;
        }
    }
    
    /**
     * CODE SMELL FIX: Feature Envy
     * BEFORE: Controllers checking if route can be started
     * AFTER: Route knows its own state
     * 
     * Check if route can be started
     */
    public boolean canBeStarted() {
        return this.status == RouteStatus.ASSIGNED;
    }
    
    /**
     * CODE SMELL FIX: Feature Envy
     * BEFORE: Controllers checking if route is active
     * AFTER: Route knows if it's active
     * 
     * Check if route is currently active
     */
    public boolean isActive() {
        return this.status == RouteStatus.IN_PROGRESS;
    }
    
    /**
     * CODE SMELL FIX: Law of Demeter
     * BEFORE: route.getCollector().getName()
     * AFTER: route.getCollectorName()
     * 
     * Get collector name without exposing internal structure
     */
    public String getCollectorName() {
        return collector != null ? collector.getName() : "Unassigned";
    }
    
    /**
     * CODE SMELL FIX: Long Method in controllers
     * BEFORE: Complex progress calculation in controllers
     * AFTER: Encapsulated in domain model
     * 
     * Calculate route completion percentage
     */
    public int getCompletionPercentage() {
        if (routeBins == null || routeBins.isEmpty()) {
            return 0;
        }
        
        long completed = routeBins.stream()
                .filter(rb -> rb.getStatus() == RouteBin.CollectionStatus.COMPLETED)
                .count();
        
        return (int) ((completed * 100) / routeBins.size());
    }
}

