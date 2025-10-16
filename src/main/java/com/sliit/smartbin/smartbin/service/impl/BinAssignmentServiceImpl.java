package com.sliit.smartbin.smartbin.service.impl;

import com.sliit.smartbin.smartbin.model.BinAssignment;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.repository.BinAssignmentRepository;
import com.sliit.smartbin.smartbin.service.BinAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BinAssignmentServiceImpl implements BinAssignmentService {
    
    @Autowired
    private BinAssignmentRepository binAssignmentRepository;
    
    @Override
    public BinAssignment createAssignment(User collector, User assignedBy, List<Long> binIds, 
                                         List<String> binLocations, Long routeId) {
        BinAssignment assignment = new BinAssignment();
        assignment.setCollector(collector);
        assignment.setAssignedBy(assignedBy);
        assignment.setBinIds(binIds);
        assignment.setBinLocations(binLocations);
        assignment.setRouteId(routeId);
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setStatus(BinAssignment.AssignmentStatus.ASSIGNED);
        
        return binAssignmentRepository.save(assignment);
    }
    
    @Override
    public List<BinAssignment> getAllAssignments() {
        return binAssignmentRepository.findAllByOrderByAssignedAtDesc();
    }
    
    @Override
    public List<BinAssignment> getAssignmentsByCollector(Long collectorId) {
        return binAssignmentRepository.findByCollectorIdOrderByAssignedAtDesc(collectorId);
    }
    
    @Override
    public List<BinAssignment> getAssignmentsByAuthority(Long authorityId) {
        return binAssignmentRepository.findByAssignedByOrderByAssignedAtDesc(
            new User() {{ setId(authorityId); }}
        );
    }
    
    @Override
    public Optional<BinAssignment> getAssignmentById(Long id) {
        return binAssignmentRepository.findById(id);
    }
    
    @Override
    public BinAssignment updateAssignmentStatus(Long assignmentId, BinAssignment.AssignmentStatus status) {
        BinAssignment assignment = binAssignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("Assignment not found with id: " + assignmentId));
        
        assignment.setStatus(status);
        return binAssignmentRepository.save(assignment);
    }
    
    @Override
    public void deleteAssignment(Long assignmentId) {
        binAssignmentRepository.deleteById(assignmentId);
    }
    
    @Override
    public void deleteAllAssignments() {
        binAssignmentRepository.deleteAll();
    }
}

