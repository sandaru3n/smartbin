package com.sliit.smartbin.smartbin.service;

import com.sliit.smartbin.smartbin.model.BinAssignment;
import com.sliit.smartbin.smartbin.model.User;

import java.util.List;
import java.util.Optional;

public interface BinAssignmentService {
    
    // Create a new bin assignment
    BinAssignment createAssignment(User collector, User assignedBy, List<Long> binIds, 
                                   List<String> binLocations, Long routeId);
    
    // Get all assignments
    List<BinAssignment> getAllAssignments();
    
    // Get assignments for a specific collector
    List<BinAssignment> getAssignmentsByCollector(Long collectorId);
    
    // Get assignments made by a specific authority user
    List<BinAssignment> getAssignmentsByAuthority(Long authorityId);
    
    // Get assignment by ID
    Optional<BinAssignment> getAssignmentById(Long id);
    
    // Update assignment status
    BinAssignment updateAssignmentStatus(Long assignmentId, BinAssignment.AssignmentStatus status);
    
    // Delete assignment
    void deleteAssignment(Long assignmentId);
    
    // Delete all assignments (for testing/reset)
    void deleteAllAssignments();
}

