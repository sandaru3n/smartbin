package com.sliit.smartbin.smartbin.service;

import com.sliit.smartbin.smartbin.dto.ReportDTO;

import java.util.Map;

public interface ReportService {
    Map<String, Object> generateCollectionReport(ReportDTO reportDTO);
    Map<String, Object> generatePerformanceReport(ReportDTO reportDTO);
    Map<String, Object> generateBinStatusReport(ReportDTO reportDTO);
    Map<String, Object> generateOverdueBinsReport(ReportDTO reportDTO);
    Map<String, Object> generateCollectorEfficiencyReport(ReportDTO reportDTO);
    Map<String, Object> generateSystemOverviewReport();
}

