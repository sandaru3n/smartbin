package com.sliit.smartbin.smartbin.service.impl;

import com.sliit.smartbin.smartbin.dto.RouteDTO;
import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.model.Route;
import com.sliit.smartbin.smartbin.model.RouteBin;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.repository.BinRepository;
import com.sliit.smartbin.smartbin.repository.RouteBinRepository;
import com.sliit.smartbin.smartbin.repository.RouteRepository;
import com.sliit.smartbin.smartbin.repository.UserRepository;
import com.sliit.smartbin.smartbin.service.RouteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final RouteBinRepository routeBinRepository;
    private final BinRepository binRepository;
    private final UserRepository userRepository;

    public RouteServiceImpl(RouteRepository routeRepository, 
                          RouteBinRepository routeBinRepository,
                          BinRepository binRepository,
                          UserRepository userRepository) {
        this.routeRepository = routeRepository;
        this.routeBinRepository = routeBinRepository;
        this.binRepository = binRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Route createRoute(RouteDTO routeDTO) {
        Route route = new Route();
        
        User collector = userRepository.findById(routeDTO.getCollectorId())
            .orElseThrow(() -> new RuntimeException("Collector not found with id: " + routeDTO.getCollectorId()));
        
        User authority = userRepository.findById(routeDTO.getAuthorityId())
            .orElseThrow(() -> new RuntimeException("Authority not found with id: " + routeDTO.getAuthorityId()));
        
        route.setRouteName(routeDTO.getRouteName());
        route.setCollector(collector);
        route.setAuthority(authority);
        route.setStatus(routeDTO.getStatus() != null ? routeDTO.getStatus() : Route.RouteStatus.ASSIGNED);
        route.setAssignedDate(routeDTO.getAssignedDate() != null ? routeDTO.getAssignedDate() : LocalDateTime.now());
        route.setEstimatedDurationMinutes(routeDTO.getEstimatedDurationMinutes());
        route.setTotalDistanceKm(routeDTO.getTotalDistanceKm());
        route.setNotes(routeDTO.getNotes());
        
        Route savedRoute = routeRepository.save(route);
        
        // Add bins to route if provided
        if (routeDTO.getBinIds() != null && !routeDTO.getBinIds().isEmpty()) {
            addBinsToRoute(savedRoute.getId(), routeDTO.getBinIds());
        }
        
        return savedRoute;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Route> findById(Long id) {
        return routeRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> findRoutesByCollector(User collector) {
        return routeRepository.findByCollector(collector);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> findRoutesByAuthority(User authority) {
        return routeRepository.findByAuthority(authority);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> findRoutesByStatus(Route.RouteStatus status) {
        return routeRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> findRoutesByCollectorAndDateRange(User collector, LocalDateTime startDate, LocalDateTime endDate) {
        return routeRepository.findByCollectorAndDateRange(collector, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> findAssignedRoutesByCollector(User collector) {
        return routeRepository.findAssignedRoutesByCollector(collector);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> findActiveRoutesByCollector(User collector) {
        return routeRepository.findActiveRoutesByCollector(collector);
    }

    @Override
    public Route updateRouteStatus(Long routeId, Route.RouteStatus status) {
        Route route = routeRepository.findById(routeId)
            .orElseThrow(() -> new RuntimeException("Route not found with id: " + routeId));
        
        route.setStatus(status);
        
        if (status == Route.RouteStatus.COMPLETED) {
            route.setCompletedDate(LocalDateTime.now());
        }
        
        return routeRepository.save(route);
    }

    @Override
    public Route startRoute(Long routeId) {
        Route route = routeRepository.findById(routeId)
            .orElseThrow(() -> new RuntimeException("Route not found with id: " + routeId));
        
        route.setStatus(Route.RouteStatus.IN_PROGRESS);
        route.setStartedDate(LocalDateTime.now());
        
        return routeRepository.save(route);
    }

    @Override
    public Route completeRoute(Long routeId) {
        Route route = routeRepository.findById(routeId)
            .orElseThrow(() -> new RuntimeException("Route not found with id: " + routeId));
        
        route.setStatus(Route.RouteStatus.COMPLETED);
        route.setCompletedDate(LocalDateTime.now());
        
        // Calculate actual duration
        if (route.getStartedDate() != null) {
            long durationMinutes = java.time.Duration.between(route.getStartedDate(), route.getCompletedDate()).toMinutes();
            route.setActualDurationMinutes((int) durationMinutes);
        }
        
        return routeRepository.save(route);
    }

    @Override
    public void deleteRoute(Long id) {
        routeRepository.deleteById(id);
    }

    @Override
    public Route optimizeRoute(List<Long> binIds, User collector, User authority) {
        // Retrieve bin locations from database
        List<Bin> bins = binRepository.findAllById(binIds);
        
        if (bins.isEmpty()) {
            throw new RuntimeException("No bins found for the provided IDs");
        }
        
        // Optimize route using Nearest Neighbor algorithm (simplified TSP)
        List<Long> optimizedBinIds = optimizeRouteUsingNearestNeighbor(bins);
        
        // Calculate route statistics
        double totalDistance = calculateTotalDistance(bins, optimizedBinIds);
        int estimatedDuration = calculateEstimatedDuration(bins.size(), totalDistance);
        
        // Create optimized route
        Route route = new Route();
        route.setRouteName("Optimized Route - " + LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        route.setCollector(collector);
        route.setAuthority(authority);
        route.setStatus(Route.RouteStatus.ASSIGNED);
        route.setAssignedDate(LocalDateTime.now());
        route.setEstimatedDurationMinutes(estimatedDuration);
        route.setTotalDistanceKm(totalDistance);
        route.setNotes("Route optimized using Nearest Neighbor algorithm");
        
        Route savedRoute = routeRepository.save(route);
        
        // Add bins to route in optimized order
        addBinsToRoute(savedRoute.getId(), optimizedBinIds);
        
        return savedRoute;
    }
    
    /**
     * Optimize route using Nearest Neighbor algorithm
     * This is a greedy algorithm that provides a reasonably good solution for TSP
     */
    private List<Long> optimizeRouteUsingNearestNeighbor(List<Bin> bins) {
        if (bins.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> optimizedRoute = new ArrayList<>();
        List<Bin> remainingBins = new ArrayList<>(bins);
        
        // Start with the first bin (or you could start with depot/office location)
        Bin currentBin = remainingBins.get(0);
        optimizedRoute.add(currentBin.getId());
        remainingBins.remove(0);
        
        // Repeatedly add the nearest unvisited bin
        while (!remainingBins.isEmpty()) {
            Bin nearestBin = findNearestBin(currentBin, remainingBins);
            optimizedRoute.add(nearestBin.getId());
            remainingBins.remove(nearestBin);
            currentBin = nearestBin;
        }
        
        return optimizedRoute;
    }
    
    /**
     * Find the nearest bin to the current bin using Haversine formula
     */
    private Bin findNearestBin(Bin current, List<Bin> candidates) {
        Bin nearest = candidates.get(0);
        double minDistance = calculateDistance(current, nearest);
        
        for (Bin candidate : candidates) {
            double distance = calculateDistance(current, candidate);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = candidate;
            }
        }
        
        return nearest;
    }
    
    /**
     * Calculate distance between two bins using Haversine formula
     */
    private double calculateDistance(Bin bin1, Bin bin2) {
        final double EARTH_RADIUS_KM = 6371.0;
        
        double lat1Rad = Math.toRadians(bin1.getLatitude());
        double lat2Rad = Math.toRadians(bin2.getLatitude());
        double deltaLat = Math.toRadians(bin2.getLatitude() - bin1.getLatitude());
        double deltaLon = Math.toRadians(bin2.getLongitude() - bin1.getLongitude());
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
    
    /**
     * Calculate total distance for the entire route
     */
    private double calculateTotalDistance(List<Bin> bins, List<Long> binIds) {
        double totalDistance = 0.0;
        
        for (int i = 0; i < binIds.size() - 1; i++) {
            Bin currentBin = findBinById(bins, binIds.get(i));
            Bin nextBin = findBinById(bins, binIds.get(i + 1));
            
            if (currentBin != null && nextBin != null) {
                totalDistance += calculateDistance(currentBin, nextBin);
            }
        }
        
        return Math.round(totalDistance * 100.0) / 100.0; // Round to 2 decimal places
    }
    
    /**
     * Calculate estimated duration based on number of bins and distance
     */
    private int calculateEstimatedDuration(int numBins, double totalDistance) {
        // Base time: 10 minutes per bin for collection
        int collectionTime = numBins * 10;
        
        // Travel time: average speed 30 km/h in urban areas
        int travelTime = (int) Math.ceil((totalDistance / 30.0) * 60);
        
        // Add buffer time (15% of total)
        int totalTime = collectionTime + travelTime;
        int bufferTime = (int) Math.ceil(totalTime * 0.15);
        
        return totalTime + bufferTime;
    }
    
    /**
     * Helper method to find bin by ID in a list
     */
    private Bin findBinById(List<Bin> bins, Long id) {
        return bins.stream()
            .filter(bin -> bin.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    @Override
    public Route assignRouteToCollector(List<Long> binIds, User collector, User authority) {
        return optimizeRoute(binIds, collector, authority);
    }

    private void addBinsToRoute(Long routeId, List<Long> binIds) {
        Route route = routeRepository.findById(routeId)
            .orElseThrow(() -> new RuntimeException("Route not found with id: " + routeId));
        
        List<RouteBin> routeBins = new ArrayList<>();
        
        for (int i = 0; i < binIds.size(); i++) {
            Long binId = binIds.get(i);
            Bin bin = binRepository.findById(binId)
                .orElseThrow(() -> new RuntimeException("Bin not found with id: " + binId));
            
            RouteBin routeBin = new RouteBin();
            routeBin.setRoute(route);
            routeBin.setBin(bin);
            routeBin.setSequenceOrder(i + 1);
            routeBin.setStatus(RouteBin.CollectionStatus.PENDING);
            
            routeBins.add(routeBin);
        }
        
        routeBinRepository.saveAll(routeBins);
    }
}

