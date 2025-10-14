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
}

