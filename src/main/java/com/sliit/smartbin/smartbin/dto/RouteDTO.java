package com.sliit.smartbin.smartbin.dto;

import com.sliit.smartbin.smartbin.model.Route;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteDTO {
    private Long id;
    private String routeName;
    private Long collectorId;
    private Long authorityId;
    private Route.RouteStatus status;
    private LocalDateTime assignedDate;
    private LocalDateTime startedDate;
    private LocalDateTime completedDate;
    private Integer estimatedDurationMinutes;
    private Integer actualDurationMinutes;
    private Double totalDistanceKm;
    private String notes;
    private List<Long> binIds;
}

