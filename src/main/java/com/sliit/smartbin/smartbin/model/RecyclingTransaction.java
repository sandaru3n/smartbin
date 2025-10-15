package com.sliit.smartbin.smartbin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recycling_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecyclingTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String recyclingUnitQrCode;
    
    @Column(nullable = false)
    private String itemType; // plastic, paper, metal, glass, etc.
    
    @Column(nullable = false)
    private Double weight; // in kg
    
    private Integer quantity;
    
    @Column(nullable = false)
    private Double pointsEarned;
    
    @Column(nullable = false)
    private Double priceValue; // monetary value
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    
    private String location;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum TransactionStatus {
        PENDING,
        CONFIRMED,
        FAILED,
        CANCELLED
    }
}

