package com.sliit.smartbin.smartbin.service;

import com.sliit.smartbin.smartbin.dto.RouteDTO;
import com.sliit.smartbin.smartbin.model.Route;
import com.sliit.smartbin.smartbin.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RouteService {
    Route createRoute(RouteDTO routeDTO);
    Optional<Route> findById(Long id);
    List<Route> findRoutesByCollector(User collector);
    List<Route> findRoutesByAuthority(User authority);
    List<Route> findRoutesByStatus(Route.RouteStatus status);
    List<Route> findRoutesByCollectorAndDateRange(User collector, LocalDateTime startDate, LocalDateTime endDate);
    List<Route> findAssignedRoutesByCollector(User collector);
    List<Route> findActiveRoutesByCollector(User collector);
    Route updateRouteStatus(Long routeId, Route.RouteStatus status);
    Route startRoute(Long routeId);
    Route completeRoute(Long routeId);
    void deleteRoute(Long id);
    Route optimizeRoute(List<Long> binIds, User collector, User authority);
    Route assignRouteToCollector(List<Long> binIds, User collector, User authority);
}

