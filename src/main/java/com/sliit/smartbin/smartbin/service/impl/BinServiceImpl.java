package com.sliit.smartbin.smartbin.service.impl;

import com.sliit.smartbin.smartbin.dto.BinDTO;
import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.repository.BinRepository;
import com.sliit.smartbin.smartbin.service.BinService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BinServiceImpl implements BinService {

    private final BinRepository binRepository;

    public BinServiceImpl(BinRepository binRepository) {
        this.binRepository = binRepository;
    }

    @Override
    public Bin createBin(BinDTO binDTO) {
        Bin bin = new Bin();
        bin.setQrCode(binDTO.getQrCode());
        bin.setLocation(binDTO.getLocation());
        bin.setLatitude(binDTO.getLatitude());
        bin.setLongitude(binDTO.getLongitude());
        bin.setBinType(binDTO.getBinType());
        bin.setStatus(binDTO.getStatus() != null ? binDTO.getStatus() : Bin.BinStatus.EMPTY);
        bin.setFillLevel(binDTO.getFillLevel() != null ? binDTO.getFillLevel() : 0);
        bin.setAlertFlag(false);
        
        return binRepository.save(bin);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Bin> findById(Long id) {
        return binRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Bin> findByQrCode(String qrCode) {
        return binRepository.findByQrCode(qrCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bin> findAllBins() {
        return binRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bin> findBinsByStatus(Bin.BinStatus status) {
        return binRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bin> findBinsByType(Bin.BinType binType) {
        return binRepository.findByBinType(binType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bin> findAlertedBins() {
        return binRepository.findByAlertFlagTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bin> findOverdueBins() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(48);
        return binRepository.findOverdueBins(threshold);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bin> findNearbyBins(Double latitude, Double longitude, Double radiusKm) {
        // Simple bounding box calculation (for production, use proper geospatial queries)
        double latRange = radiusKm / 111.0; // Approximate km per degree latitude
        double lngRange = radiusKm / (111.0 * Math.cos(Math.toRadians(latitude)));
        
        return binRepository.findBinsInArea(
            latitude - latRange, latitude + latRange,
            longitude - lngRange, longitude + lngRange
        );
    }

    @Override
    public Bin updateBinStatus(Long binId, Bin.BinStatus status, Integer fillLevel) {
        Bin bin = binRepository.findById(binId)
            .orElseThrow(() -> new RuntimeException("Bin not found with id: " + binId));
        
        bin.setStatus(status);
        if (fillLevel != null) {
            bin.setFillLevel(fillLevel);
        }
        
        // Update last emptied if bin is being emptied
        if (status == Bin.BinStatus.EMPTY) {
            bin.setLastEmptied(LocalDateTime.now());
            bin.setAlertFlag(false);
        }
        
        return binRepository.save(bin);
    }

    @Override
    public Bin updateBinFillLevel(Long binId, Integer fillLevel) {
        Bin bin = binRepository.findById(binId)
            .orElseThrow(() -> new RuntimeException("Bin not found with id: " + binId));
        
        bin.setFillLevel(fillLevel);
        
        // Update status based on fill level
        if (fillLevel >= 90) {
            bin.setStatus(Bin.BinStatus.FULL);
        } else if (fillLevel >= 50) {
            bin.setStatus(Bin.BinStatus.PARTIAL);
        } else {
            bin.setStatus(Bin.BinStatus.EMPTY);
        }
        
        return binRepository.save(bin);
    }

    @Override
    public void deleteBin(Long id) {
        binRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBinOverdue(Bin bin) {
        if (bin.getStatus() == Bin.BinStatus.FULL && bin.getLastEmptied() != null) {
            return bin.getLastEmptied().isBefore(LocalDateTime.now().minusHours(48));
        }
        return false;
    }

    @Override
    public void checkAndSetAlertFlags() {
        List<Bin> overdueBins = findOverdueBins();
        for (Bin bin : overdueBins) {
            bin.setAlertFlag(true);
            bin.setStatus(Bin.BinStatus.OVERDUE);
            binRepository.save(bin);
        }
    }
}

