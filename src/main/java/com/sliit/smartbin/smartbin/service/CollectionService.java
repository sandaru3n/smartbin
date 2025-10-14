package com.sliit.smartbin.smartbin.service;

import com.sliit.smartbin.smartbin.dto.CollectionDTO;
import com.sliit.smartbin.smartbin.model.Collection;
import com.sliit.smartbin.smartbin.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CollectionService {
    Collection createCollection(CollectionDTO collectionDTO);
    Optional<Collection> findById(Long id);
    List<Collection> findCollectionsByCollector(User collector);
    List<Collection> findCollectionsByStatus(Collection.CollectionStatus status);
    List<Collection> findCollectionsByType(Collection.CollectionType collectionType);
    List<Collection> findCollectionsByCollectorAndDateRange(User collector, LocalDateTime startDate, LocalDateTime endDate);
    Collection updateCollectionStatus(Long collectionId, Collection.CollectionStatus status);
    Collection completeCollection(Long collectionId, String notes);
    void deleteCollection(Long id);
    Long getCompletedCollectionsCountByCollector(User collector);
    List<Collection> getAssignedCollectionsByCollector(User collector);
    Collection assignCollectionToCollector(Long binId, User collector, Collection.CollectionType collectionType);
}

