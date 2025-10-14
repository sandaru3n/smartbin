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
        // Simple route optimization - in production, use proper algorithms like TSP
        Route route = new Route();
        route.setRouteName("Optimized Route - " + LocalDateTime.now().toString());
        route.setCollector(collector);
        route.setAuthority(authority);
        route.setStatus(Route.RouteStatus.ASSIGNED);
        route.setAssignedDate(LocalDateTime.now());
        
        // Calculate estimated duration (simplified)
        route.setEstimatedDurationMinutes(binIds.size() * 15); // 15 minutes per bin
        
        Route savedRoute = routeRepository.save(route);
        
        // Add bins to route in optimized order
        addBinsToRoute(savedRoute.getId(), binIds);
        
        return savedRoute;
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

