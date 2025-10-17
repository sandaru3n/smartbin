package com.sliit.smartbin.smartbin.repository;

import com.sliit.smartbin.smartbin.model.RegionAssignment;
import com.sliit.smartbin.smartbin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for RegionAssignment entity
 * Provides database access for region assignment history
 */
@Repository
public interface RegionAssignmentRepository extends JpaRepository<RegionAssignment, Long> {
    
    /**
     * Find all assignments for a specific collector
     */
    List<RegionAssignment> findByCollectorOrderByAssignedAtDesc(User collector);
    
    /**
     * Find all assignments by a specific authority
     */
    List<RegionAssignment> findByAssignedByOrderByAssignedAtDesc(User assignedBy);
    
    /**
     * Find assignments for a specific region
     */
    List<RegionAssignment> findByNewRegionOrderByAssignedAtDesc(String region);
    
    /**
     * Find active assignments for a collector
     */
    List<RegionAssignment> findByCollectorAndStatus(User collector, RegionAssignment.AssignmentStatus status);
}

