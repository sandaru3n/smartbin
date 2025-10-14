package com.sliit.smartbin.smartbin.repository;

import com.sliit.smartbin.smartbin.model.Route;
import com.sliit.smartbin.smartbin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    
    List<Route> findByCollector(User collector);
    
    List<Route> findByAuthority(User authority);
    
    List<Route> findByStatus(Route.RouteStatus status);
    
    @Query("SELECT r FROM Route r WHERE r.collector = :collector AND r.assignedDate BETWEEN :startDate AND :endDate")
    List<Route> findByCollectorAndDateRange(@Param("collector") User collector, 
                                          @Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT r FROM Route r WHERE r.status = 'ASSIGNED' AND r.collector = :collector")
    List<Route> findAssignedRoutesByCollector(@Param("collector") User collector);
    
    @Query("SELECT r FROM Route r WHERE r.status = 'IN_PROGRESS' AND r.collector = :collector")
    List<Route> findActiveRoutesByCollector(@Param("collector") User collector);
}

