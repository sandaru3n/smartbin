package com.sliit.smartbin.smartbin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private String reportType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String collectorId;
    private String region;
    private Map<String, Object> parameters;
}

