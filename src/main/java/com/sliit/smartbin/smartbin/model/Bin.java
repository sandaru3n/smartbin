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
}

