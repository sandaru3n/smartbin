package com.sliit.smartbin.smartbin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "waste_disposals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WasteDisposal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "bin_id", nullable = false)
    private Bin bin;
    
    @Column(nullable = false)
    private Integer reportedFillLevel;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DisposalStatus status;
    
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum DisposalStatus {
        SUBMITTED,
        CONFIRMED,
        FAILED
    }
}

