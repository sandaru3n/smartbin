package com.sliit.smartbin.smartbin.service.impl;

import com.sliit.smartbin.smartbin.dto.ReportDTO;
import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.model.Collection;
import com.sliit.smartbin.smartbin.model.Route;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.repository.BinRepository;
import com.sliit.smartbin.smartbin.repository.CollectionRepository;
import com.sliit.smartbin.smartbin.repository.RouteRepository;
import com.sliit.smartbin.smartbin.repository.UserRepository;
import com.sliit.smartbin.smartbin.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final BinRepository binRepository;
    private final CollectionRepository collectionRepository;
    private final RouteRepository routeRepository;
    private final UserRepository userRepository;

    public ReportServiceImpl(BinRepository binRepository, 
                           CollectionRepository collectionRepository,
                           RouteRepository routeRepository,
                           UserRepository userRepository) {
        this.binRepository = binRepository;
        this.collectionRepository = collectionRepository;
        this.routeRepository = routeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Map<String, Object> generateCollectionReport(ReportDTO reportDTO) {
        Map<String, Object> report = new HashMap<>();
        
        LocalDateTime startDate = reportDTO.getStartDate() != null ? reportDTO.getStartDate() : LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = reportDTO.getEndDate() != null ? reportDTO.getEndDate() : LocalDateTime.now();
        
        List<Collection> collections = collectionRepository.findAll();
        
        long totalCollections = collections.size();
        long completedCollections = collections.stream()
            .filter(c -> c.getStatus() == Collection.CollectionStatus.COMPLETED)
            .count();
        
        long standardCollections = collections.stream()
            .filter(c -> c.getCollectionType() == Collection.CollectionType.STANDARD)
            .count();
        
        long recyclingCollections = collections.stream()
            .filter(c -> c.getCollectionType() == Collection.CollectionType.RECYCLING)
            .count();
        
        long bulkCollections = collections.stream()
            .filter(c -> c.getCollectionType() == Collection.CollectionType.BULK)
            .count();
        
        report.put("reportType", "Collection Report");
        report.put("period", startDate + " to " + endDate);
        report.put("totalCollections", totalCollections);
        report.put("completedCollections", completedCollections);
        report.put("completionRate", totalCollections > 0 ? (double) completedCollections / totalCollections * 100 : 0);
        report.put("standardCollections", standardCollections);
        report.put("recyclingCollections", recyclingCollections);
        report.put("bulkCollections", bulkCollections);
        report.put("generatedAt", LocalDateTime.now());
        
        return report;
    }

    @Override
    public Map<String, Object> generatePerformanceReport(ReportDTO reportDTO) {
        Map<String, Object> report = new HashMap<>();
        
        List<User> collectors = userRepository.findAll().stream()
            .filter(user -> user.getRole() == User.UserRole.COLLECTOR)
            .toList();
        
        Map<String, Object> collectorPerformance = new HashMap<>();
        
        for (User collector : collectors) {
            Map<String, Object> performance = new HashMap<>();
            
            List<Collection> collections = collectionRepository.findByCollector(collector);
            Long completedCount = collectionRepository.countCompletedCollectionsByCollector(collector);
            
            List<Route> routes = routeRepository.findByCollector(collector);
            long completedRoutes = routes.stream()
                .filter(r -> r.getStatus() == Route.RouteStatus.COMPLETED)
                .count();
            
            performance.put("totalCollections", collections.size());
            performance.put("completedCollections", completedCount);
            performance.put("completionRate", collections.size() > 0 ? (double) completedCount / collections.size() * 100 : 0);
            performance.put("totalRoutes", routes.size());
            performance.put("completedRoutes", completedRoutes);
            
            collectorPerformance.put(collector.getName(), performance);
        }
        
        report.put("reportType", "Performance Report");
        report.put("collectorPerformance", collectorPerformance);
        report.put("generatedAt", LocalDateTime.now());
        
        return report;
    }

    @Override
    public Map<String, Object> generateBinStatusReport(ReportDTO reportDTO) {
        Map<String, Object> report = new HashMap<>();
        
        List<Bin> allBins = binRepository.findAll();
        
        long emptyBins = allBins.stream()
            .filter(b -> b.getStatus() == Bin.BinStatus.EMPTY)
            .count();
        
        long partialBins = allBins.stream()
            .filter(b -> b.getStatus() == Bin.BinStatus.PARTIAL)
            .count();
        
        long fullBins = allBins.stream()
            .filter(b -> b.getStatus() == Bin.BinStatus.FULL)
            .count();
        
        long overdueBins = allBins.stream()
            .filter(b -> b.getStatus() == Bin.BinStatus.OVERDUE)
            .count();
        
        long alertedBins = allBins.stream()
            .filter(Bin::getAlertFlag)
            .count();
        
        // Calculate average fill level
        double averageFillLevel = allBins.stream()
            .mapToInt(Bin::getFillLevel)
            .average()
            .orElse(0.0);
        
        report.put("reportType", "Bin Status Report");
        report.put("totalBins", allBins.size());
        report.put("emptyBins", emptyBins);
        report.put("partialBins", partialBins);
        report.put("fullBins", fullBins);
        report.put("overdueBins", overdueBins);
        report.put("alertedBins", alertedBins);
        report.put("averageFillLevel", Math.round(averageFillLevel));
        report.put("generatedAt", LocalDateTime.now());
        
        return report;
    }

    @Override
    public Map<String, Object> generateOverdueBinsReport(ReportDTO reportDTO) {
        Map<String, Object> report = new HashMap<>();
        
        List<Bin> overdueBins = binRepository.findOverdueBins(LocalDateTime.now().minusHours(48));
        List<Bin> alertedBins = binRepository.findByAlertFlagTrue();
        
        report.put("reportType", "Overdue Bins Report");
        report.put("overdueBins", overdueBins.size());
        report.put("alertedBins", alertedBins.size());
        report.put("binDetails", overdueBins);
        report.put("generatedAt", LocalDateTime.now());
        
        return report;
    }

    @Override
    public Map<String, Object> generateCollectorEfficiencyReport(ReportDTO reportDTO) {
        Map<String, Object> report = new HashMap<>();
        
        List<User> collectors = userRepository.findAll().stream()
            .filter(user -> user.getRole() == User.UserRole.COLLECTOR)
            .toList();
        
        Map<String, Object> efficiencyData = new HashMap<>();
        
        for (User collector : collectors) {
            Map<String, Object> efficiency = new HashMap<>();
            
            List<Route> routes = routeRepository.findByCollector(collector);
            List<Collection> collections = collectionRepository.findByCollector(collector);
            
            double avgRouteDuration = routes.stream()
                .filter(r -> r.getActualDurationMinutes() != null)
                .mapToInt(Route::getActualDurationMinutes)
                .average()
                .orElse(0.0);
            
            double avgEstimatedDuration = routes.stream()
                .filter(r -> r.getEstimatedDurationMinutes() != null)
                .mapToInt(Route::getEstimatedDurationMinutes)
                .average()
                .orElse(0.0);
            
            double efficiencyScore = avgEstimatedDuration > 0 ? 
                (avgEstimatedDuration - avgRouteDuration) / avgEstimatedDuration * 100 : 0;
            
            efficiency.put("totalRoutes", routes.size());
            efficiency.put("totalCollections", collections.size());
            efficiency.put("avgRouteDuration", avgRouteDuration);
            efficiency.put("avgEstimatedDuration", avgEstimatedDuration);
            efficiency.put("efficiencyScore", efficiencyScore);
            
            efficiencyData.put(collector.getName(), efficiency);
        }
        
        report.put("reportType", "Collector Efficiency Report");
        report.put("efficiencyData", efficiencyData);
        report.put("generatedAt", LocalDateTime.now());
        
        return report;
    }

    @Override
    public Map<String, Object> generateSystemOverviewReport() {
        Map<String, Object> report = new HashMap<>();
        
        long totalBins = binRepository.count();
        long totalCollections = collectionRepository.count();
        long totalRoutes = routeRepository.count();
        long totalUsers = userRepository.count();
        
        long activeCollectors = userRepository.findAll().stream()
            .filter(user -> user.getRole() == User.UserRole.COLLECTOR)
            .count();
        
        long activeAuthorities = userRepository.findAll().stream()
            .filter(user -> user.getRole() == User.UserRole.AUTHORITY)
            .count();
        
        long activeResidents = userRepository.findAll().stream()
            .filter(user -> user.getRole() == User.UserRole.RESIDENT)
            .count();
        
        // Calculate average fill level
        List<Bin> allBins = binRepository.findAll();
        double averageFillLevel = allBins.stream()
            .mapToInt(Bin::getFillLevel)
            .average()
            .orElse(0.0);
        
        // Get last collection time
        String lastCollectionTime = collectionRepository.findAll().stream()
            .filter(c -> c.getStatus() == Collection.CollectionStatus.COMPLETED)
            .max((c1, c2) -> c1.getCreatedAt().compareTo(c2.getCreatedAt()))
            .map(c -> {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime collectionTime = c.getCreatedAt();
                long hoursAgo = java.time.Duration.between(collectionTime, now).toHours();
                return hoursAgo + "h ago";
            })
            .orElse("No collections");
        
        report.put("reportType", "System Overview Report");
        report.put("totalBins", totalBins);
        report.put("totalCollections", totalCollections);
        report.put("totalRoutes", totalRoutes);
        report.put("totalUsers", totalUsers);
        report.put("activeCollectors", activeCollectors);
        report.put("activeAuthorities", activeAuthorities);
        report.put("activeResidents", activeResidents);
        report.put("averageFillLevel", Math.round(averageFillLevel));
        report.put("lastCollectionTime", lastCollectionTime);
        report.put("generatedAt", LocalDateTime.now());
        
        return report;
    }
}

