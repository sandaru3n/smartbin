package com.sliit.smartbin.smartbin.repository;

import com.sliit.smartbin.smartbin.model.RouteBin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteBinRepository extends JpaRepository<RouteBin, Long> {
    
    List<RouteBin> findByRouteId(Long routeId);
    
    List<RouteBin> findByBinId(Long binId);
    
    List<RouteBin> findByStatus(RouteBin.CollectionStatus status);
    
    @Query("SELECT rb FROM RouteBin rb WHERE rb.route.id = :routeId ORDER BY rb.sequenceOrder")
    List<RouteBin> findByRouteIdOrderBySequence(@Param("routeId") Long routeId);
    
    @Query("SELECT rb FROM RouteBin rb WHERE rb.route.id = :routeId AND rb.status = 'PENDING' ORDER BY rb.sequenceOrder")
    List<RouteBin> findPendingBinsByRoute(@Param("routeId") Long routeId);
}

