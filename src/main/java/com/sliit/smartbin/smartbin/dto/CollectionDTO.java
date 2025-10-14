package com.sliit.smartbin.smartbin.dto;

import com.sliit.smartbin.smartbin.model.Collection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionDTO {
    private Long id;
    private Long binId;
    private Long collectorId;
    private Collection.CollectionType collectionType;
    private Collection.CollectionStatus status;
    private String wasteType;
    private Integer wasteLevel;
    private LocalDateTime collectionDate;
    private LocalDateTime completionDate;
    private String notes;
}

