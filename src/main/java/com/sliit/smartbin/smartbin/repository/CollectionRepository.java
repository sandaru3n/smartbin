package com.sliit.smartbin.smartbin.repository;

import com.sliit.smartbin.smartbin.model.Collection;
import com.sliit.smartbin.smartbin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {
    
    List<Collection> findByCollector(User collector);
    
    List<Collection> findByStatus(Collection.CollectionStatus status);
    
    List<Collection> findByCollectionType(Collection.CollectionType collectionType);
    
    @Query("SELECT c FROM Collection c WHERE c.collector = :collector AND c.collectionDate BETWEEN :startDate AND :endDate")
    List<Collection> findByCollectorAndDateRange(@Param("collector") User collector, 
                                               @Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(c) FROM Collection c WHERE c.status = 'COMPLETED' AND c.collector = :collector")
    Long countCompletedCollectionsByCollector(@Param("collector") User collector);
    
    @Query("SELECT c FROM Collection c WHERE c.status = 'ASSIGNED' AND c.collector = :collector")
    List<Collection> findAssignedCollectionsByCollector(@Param("collector") User collector);
}

