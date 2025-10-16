package com.sliit.smartbin.smartbin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bin_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BinAssignment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "collector_id", nullable = false)
    private User collector;
    
    @ManyToOne
    @JoinColumn(name = "assigned_by_id", nullable = false)
    private User assignedBy;
    
    @ElementCollection
    @CollectionTable(name = "assignment_bins", joinColumns = @JoinColumn(name = "assignment_id"))
    @Column(name = "bin_id")
    private List<Long> binIds = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "assignment_bin_locations", joinColumns = @JoinColumn(name = "assignment_id"))
    @Column(name = "location")
    private List<String> binLocations = new ArrayList<>();
    
    @Column(nullable = false)
    private LocalDateTime assignedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status = AssignmentStatus.ASSIGNED;
    
    @Column(name = "route_id")
    private Long routeId;
    
    @Column(name = "notes", length = 500)
    private String notes;
    
    public enum AssignmentStatus {
        ASSIGNED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
    
    @PrePersist
    protected void onCreate() {
        if (assignedAt == null) {
            assignedAt = LocalDateTime.now();
        }
    }
}

