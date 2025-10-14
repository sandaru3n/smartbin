package com.sliit.smartbin.smartbin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "collections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bin_id", nullable = false)
    private Bin bin;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collector_id", nullable = false)
    private User collector;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CollectionType collectionType;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CollectionStatus status;
    
    @Column(name = "waste_type")
    private String wasteType;
    
    @Column(name = "waste_level")
    private Integer wasteLevel; // Percentage
    
    @Column(name = "collection_date")
    private LocalDateTime collectionDate;
    
    @Column(name = "completion_date")
    private LocalDateTime completionDate;
    
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
    
    public enum CollectionType {
        STANDARD,
        RECYCLING,
        BULK
    }
    
    public enum CollectionStatus {
        ASSIGNED,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
}

