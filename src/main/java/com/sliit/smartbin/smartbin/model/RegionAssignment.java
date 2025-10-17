package com.sliit.smartbin.smartbin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity to track region assignment history for collectors
 * Provides audit trail of all region assignments
 */
@Entity
@Table(name = "region_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionAssignment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collector_id", nullable = false)
    private User collector;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by_id", nullable = false)
    private User assignedBy;
    
    @Column(name = "previous_region")
    private String previousRegion;
    
    @Column(name = "new_region", nullable = false)
    private String newRegion;
    
    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AssignmentStatus status;
    
    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
        if (status == null) {
            status = AssignmentStatus.ACTIVE;
        }
    }
    
    public enum AssignmentStatus {
        ACTIVE,
        SUPERSEDED,
        CANCELLED
    }
}

