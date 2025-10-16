package com.sliit.smartbin.smartbin.repository;

import com.sliit.smartbin.smartbin.model.BinAssignment;
import com.sliit.smartbin.smartbin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BinAssignmentRepository extends JpaRepository<BinAssignment, Long> {
    
    // Find all assignments for a specific collector
    List<BinAssignment> findByCollectorOrderByAssignedAtDesc(User collector);
    
    // Find all assignments for a specific collector by ID
    List<BinAssignment> findByCollectorIdOrderByAssignedAtDesc(Long collectorId);
    
    // Find all assignments made by a specific authority user
    List<BinAssignment> findByAssignedByOrderByAssignedAtDesc(User assignedBy);
    
    // Find all assignments with a specific status
    List<BinAssignment> findByStatusOrderByAssignedAtDesc(BinAssignment.AssignmentStatus status);
    
    // Find all assignments, ordered by date (most recent first)
    List<BinAssignment> findAllByOrderByAssignedAtDesc();
}

