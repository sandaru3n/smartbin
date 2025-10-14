package com.sliit.smartbin.smartbin.service;

import com.sliit.smartbin.smartbin.dto.BinDTO;
import com.sliit.smartbin.smartbin.model.Bin;

import java.util.List;
import java.util.Optional;

public interface BinService {
    Bin createBin(BinDTO binDTO);
    Optional<Bin> findById(Long id);
    Optional<Bin> findByQrCode(String qrCode);
    List<Bin> findAllBins();
    List<Bin> findBinsByStatus(Bin.BinStatus status);
    List<Bin> findBinsByType(Bin.BinType binType);
    List<Bin> findAlertedBins();
    List<Bin> findOverdueBins();
    List<Bin> findNearbyBins(Double latitude, Double longitude, Double radiusKm);
    Bin updateBinStatus(Long binId, Bin.BinStatus status, Integer fillLevel);
    Bin updateBinFillLevel(Long binId, Integer fillLevel);
    void deleteBin(Long id);
    boolean isBinOverdue(Bin bin);
    void checkAndSetAlertFlags();
}

