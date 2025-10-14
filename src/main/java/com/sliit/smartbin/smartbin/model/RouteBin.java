package com.sliit.smartbin.smartbin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "route_bins")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteBin {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bin_id", nullable = false)
    private Bin bin;
    
    @Column(nullable = false)
    private Integer sequenceOrder;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CollectionStatus status;
    
    @Column(name = "visited_date")
    private LocalDateTime visitedDate;
    
    @Column(name = "notes")
    private String notes;
    
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
    
    public enum CollectionStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        SKIPPED
    }
}

