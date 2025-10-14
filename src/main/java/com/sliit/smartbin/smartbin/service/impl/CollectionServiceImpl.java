package com.sliit.smartbin.smartbin.service.impl;

import com.sliit.smartbin.smartbin.dto.CollectionDTO;
import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.model.Collection;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.repository.BinRepository;
import com.sliit.smartbin.smartbin.repository.CollectionRepository;
import com.sliit.smartbin.smartbin.repository.UserRepository;
import com.sliit.smartbin.smartbin.service.CollectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;
    private final BinRepository binRepository;
    private final UserRepository userRepository;

    public CollectionServiceImpl(CollectionRepository collectionRepository, 
                                BinRepository binRepository, 
                                UserRepository userRepository) {
        this.collectionRepository = collectionRepository;
        this.binRepository = binRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Collection createCollection(CollectionDTO collectionDTO) {
        Collection collection = new Collection();
        
        Bin bin = binRepository.findById(collectionDTO.getBinId())
            .orElseThrow(() -> new RuntimeException("Bin not found with id: " + collectionDTO.getBinId()));
        
        User collector = userRepository.findById(collectionDTO.getCollectorId())
            .orElseThrow(() -> new RuntimeException("Collector not found with id: " + collectionDTO.getCollectorId()));
        
        collection.setBin(bin);
        collection.setCollector(collector);
        collection.setCollectionType(collectionDTO.getCollectionType());
        collection.setStatus(collectionDTO.getStatus() != null ? collectionDTO.getStatus() : Collection.CollectionStatus.ASSIGNED);
        collection.setWasteType(collectionDTO.getWasteType());
        collection.setWasteLevel(collectionDTO.getWasteLevel());
        collection.setCollectionDate(collectionDTO.getCollectionDate() != null ? collectionDTO.getCollectionDate() : LocalDateTime.now());
        collection.setNotes(collectionDTO.getNotes());
        
        return collectionRepository.save(collection);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Collection> findById(Long id) {
        return collectionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Collection> findCollectionsByCollector(User collector) {
        return collectionRepository.findByCollector(collector);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Collection> findCollectionsByStatus(Collection.CollectionStatus status) {
        return collectionRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Collection> findCollectionsByType(Collection.CollectionType collectionType) {
        return collectionRepository.findByCollectionType(collectionType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Collection> findCollectionsByCollectorAndDateRange(User collector, LocalDateTime startDate, LocalDateTime endDate) {
        return collectionRepository.findByCollectorAndDateRange(collector, startDate, endDate);
    }

    @Override
    public Collection updateCollectionStatus(Long collectionId, Collection.CollectionStatus status) {
        Collection collection = collectionRepository.findById(collectionId)
            .orElseThrow(() -> new RuntimeException("Collection not found with id: " + collectionId));
        
        collection.setStatus(status);
        
        if (status == Collection.CollectionStatus.COMPLETED) {
            collection.setCompletionDate(LocalDateTime.now());
            
            // Update bin status to empty
            Bin bin = collection.getBin();
            bin.setStatus(Bin.BinStatus.EMPTY);
            bin.setFillLevel(0);
            bin.setLastEmptied(LocalDateTime.now());
            bin.setAlertFlag(false);
            binRepository.save(bin);
        }
        
        return collectionRepository.save(collection);
    }

    @Override
    public Collection completeCollection(Long collectionId, String notes) {
        Collection collection = collectionRepository.findById(collectionId)
            .orElseThrow(() -> new RuntimeException("Collection not found with id: " + collectionId));
        
        collection.setStatus(Collection.CollectionStatus.COMPLETED);
        collection.setCompletionDate(LocalDateTime.now());
        collection.setNotes(notes);
        
        // Update bin status
        Bin bin = collection.getBin();
        bin.setStatus(Bin.BinStatus.EMPTY);
        bin.setFillLevel(0);
        bin.setLastEmptied(LocalDateTime.now());
        bin.setAlertFlag(false);
        binRepository.save(bin);
        
        return collectionRepository.save(collection);
    }

    @Override
    public void deleteCollection(Long id) {
        collectionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCompletedCollectionsCountByCollector(User collector) {
        return collectionRepository.countCompletedCollectionsByCollector(collector);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Collection> getAssignedCollectionsByCollector(User collector) {
        return collectionRepository.findAssignedCollectionsByCollector(collector);
    }

    @Override
    public Collection assignCollectionToCollector(Long binId, User collector, Collection.CollectionType collectionType) {
        Bin bin = binRepository.findById(binId)
            .orElseThrow(() -> new RuntimeException("Bin not found with id: " + binId));
        
        Collection collection = new Collection();
        collection.setBin(bin);
        collection.setCollector(collector);
        collection.setCollectionType(collectionType);
        collection.setStatus(Collection.CollectionStatus.ASSIGNED);
        collection.setCollectionDate(LocalDateTime.now());
        
        return collectionRepository.save(collection);
    }
}

