package com.sliit.smartbin.smartbin.repository;

import com.sliit.smartbin.smartbin.model.Bin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BinRepository extends JpaRepository<Bin, Long> {
    
    Optional<Bin> findByQrCode(String qrCode);
    
    List<Bin> findByStatus(Bin.BinStatus status);
    
    List<Bin> findByBinType(Bin.BinType binType);
    
    List<Bin> findByAlertFlagTrue();
    
    @Query("SELECT b FROM Bin b WHERE b.status = 'FULL' AND b.lastEmptied < :threshold")
    List<Bin> findOverdueBins(@Param("threshold") LocalDateTime threshold);
    
    @Query("SELECT b FROM Bin b WHERE b.latitude BETWEEN :minLat AND :maxLat AND b.longitude BETWEEN :minLng AND :maxLng")
    List<Bin> findBinsInArea(@Param("minLat") Double minLat, @Param("maxLat") Double maxLat, 
                           @Param("minLng") Double minLng, @Param("maxLng") Double maxLng);
    
    @Query("SELECT b FROM Bin b WHERE b.fillLevel > :threshold")
    List<Bin> findBinsWithHighFillLevel(@Param("threshold") Integer threshold);
}

