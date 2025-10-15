package com.sliit.smartbin.smartbin.service;

import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.repository.BinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Service for dynamic bin data updates
 * Updates bin fill levels and statuses every 30 seconds
 */
@Service
public class DynamicDataService {

    @Autowired
    private BinRepository binRepository;

    private final Random random = new Random();

    /**
     * Update bin data every 30 seconds
     * This simulates real-time bin monitoring
     */
    @Scheduled(fixedRate = 30000) // 30 seconds
    public void updateBinData() {
        List<Bin> bins = binRepository.findAll();
        
        if (bins.isEmpty()) {
            System.out.println("No bins found in database. Initializing sample data...");
            initializeSampleData();
            return;
        }

        System.out.println("Updating " + bins.size() + " bins with dynamic data...");
        
        for (Bin bin : bins) {
            updateBinFillLevel(bin);
            updateBinStatus(bin);
            binRepository.save(bin);
        }
        
        System.out.println("✓ Updated " + bins.size() + " bins at " + LocalDateTime.now());
    }

    /**
     * Update bin fill level with realistic changes
     */
    private void updateBinFillLevel(Bin bin) {
        int currentFill = bin.getFillLevel();
        
        // More realistic changes: smaller increments
        int change;
        if (currentFill < 20) {
            // Empty bins fill up slowly
            change = random.nextInt(6) + 1; // +1 to +6
        } else if (currentFill > 80) {
            // Full bins might overflow slightly or stay full
            change = random.nextInt(5) - 2; // -2 to +2
        } else {
            // Partially full bins change moderately
            change = random.nextInt(11) - 5; // -5 to +5
        }
        
        int newFill = Math.max(0, Math.min(100, currentFill + change));
        bin.setFillLevel(newFill);
    }

    /**
     * Update bin status based on fill level and last emptied time
     */
    private void updateBinStatus(Bin bin) {
        int fillLevel = bin.getFillLevel();
        LocalDateTime lastEmptied = bin.getLastEmptied();
        long hoursSinceEmptied = java.time.Duration.between(lastEmptied, LocalDateTime.now()).toHours();

        if (fillLevel >= 90) {
            if (hoursSinceEmptied > 48) {
                bin.setStatus(Bin.BinStatus.OVERDUE);
                bin.setAlertFlag(true);
            } else {
                bin.setStatus(Bin.BinStatus.FULL);
                bin.setAlertFlag(fillLevel >= 95);
            }
        } else if (fillLevel >= 60) {
            bin.setStatus(Bin.BinStatus.PARTIAL);
            bin.setAlertFlag(false);
        } else {
            bin.setStatus(Bin.BinStatus.EMPTY);
            bin.setAlertFlag(false);
        }
    }

    /**
     * Initialize sample data if database is empty
     */
    private void initializeSampleData() {
        System.out.println("Creating all 32 sample bins for dynamic updates...");
        
        // Colombo area bins - North District
        createSampleBin("QR001", "Colombo Fort Station", 6.9344, 79.8428, Bin.BinType.STANDARD, 85);
        createSampleBin("QR002", "Pettah Market", 6.9369, 79.8581, Bin.BinType.STANDARD, 65);
        createSampleBin("QR003", "Galle Face Green", 6.9271, 79.8612, Bin.BinType.RECYCLING, 25);
        createSampleBin("QR004", "Liberty Plaza", 6.9105, 79.8547, Bin.BinType.STANDARD, 90);
        createSampleBin("QR005", "Bambalapitiya Junction", 6.8881, 79.8603, Bin.BinType.STANDARD, 45);
        createSampleBin("QR006", "Maradana Railway", 6.9297, 79.8656, Bin.BinType.BULK, 95);
        createSampleBin("QR007", "Slave Island", 6.9250, 79.8500, Bin.BinType.STANDARD, 70);
        
        // Colombo area bins - South District
        createSampleBin("QR008", "Wellawatte Beach", 6.8700, 79.8600, Bin.BinType.RECYCLING, 55);
        createSampleBin("QR009", "Dehiwala Zoo", 6.8500, 79.8700, Bin.BinType.STANDARD, 15);
        createSampleBin("QR010", "Mount Lavinia", 6.8380, 79.8630, Bin.BinType.STANDARD, 88);
        createSampleBin("QR011", "Kollupitiya", 6.9150, 79.8500, Bin.BinType.STANDARD, 60);
        createSampleBin("QR012", "Cinnamon Gardens", 6.9050, 79.8650, Bin.BinType.RECYCLING, 20);
        
        // Colombo area bins - East District
        createSampleBin("QR013", "Borella Junction", 6.9150, 79.8800, Bin.BinType.STANDARD, 85);
        createSampleBin("QR014", "Rajagiriya", 6.9080, 79.8900, Bin.BinType.STANDARD, 70);
        createSampleBin("QR015", "Nugegoda", 6.8650, 79.8900, Bin.BinType.BULK, 96);
        createSampleBin("QR016", "Kotte Parliament", 6.8950, 79.9100, Bin.BinType.STANDARD, 25);
        createSampleBin("QR017", "Battaramulla", 6.8980, 79.9200, Bin.BinType.RECYCLING, 50);
        
        // Colombo area bins - West District
        createSampleBin("QR018", "Negombo Beach", 7.2083, 79.8358, Bin.BinType.STANDARD, 90);
        createSampleBin("QR019", "Wattala", 6.9900, 79.8900, Bin.BinType.STANDARD, 55);
        createSampleBin("QR020", "Ja-Ela", 7.0750, 79.8920, Bin.BinType.STANDARD, 18);
        createSampleBin("QR021", "Katunayake Airport", 7.1807, 79.8841, Bin.BinType.BULK, 94);
        createSampleBin("QR022", "Seeduwa", 7.1200, 79.8850, Bin.BinType.RECYCLING, 48);
        
        // Kandy area bins
        createSampleBin("QR023", "Kandy City Center", 7.2906, 80.6337, Bin.BinType.STANDARD, 85);
        createSampleBin("QR024", "Temple of Tooth", 7.2944, 80.6414, Bin.BinType.RECYCLING, 5);
        createSampleBin("QR025", "Kandy Lake", 7.2931, 80.6406, Bin.BinType.STANDARD, 55);
        createSampleBin("QR026", "Peradeniya Gardens", 7.2700, 80.5950, Bin.BinType.STANDARD, 88);
        createSampleBin("QR027", "Kandy Market", 7.2950, 80.6350, Bin.BinType.BULK, 97);
        
        // Galle area bins
        createSampleBin("QR028", "Galle Fort", 6.0535, 80.2210, Bin.BinType.STANDARD, 88);
        createSampleBin("QR029", "Galle Market", 6.0556, 80.2181, Bin.BinType.RECYCLING, 15);
        createSampleBin("QR030", "Unawatuna Beach", 6.0100, 80.2500, Bin.BinType.STANDARD, 62);
        createSampleBin("QR031", "Galle Bus Stand", 6.0560, 80.2200, Bin.BinType.STANDARD, 91);
        createSampleBin("QR032", "Hikkaduwa", 6.1400, 80.1000, Bin.BinType.RECYCLING, 22);
        
        System.out.println("✓ Created all 32 sample bins for dynamic updates");
    }

    private void createSampleBin(String qrCode, String location, Double latitude, Double longitude, 
                               Bin.BinType binType, int fillLevel) {
        Bin bin = new Bin();
        bin.setQrCode(qrCode);
        bin.setLocation(location);
        bin.setLatitude(latitude);
        bin.setLongitude(longitude);
        bin.setBinType(binType);
        bin.setFillLevel(fillLevel);
        bin.setLastEmptied(LocalDateTime.now().minusHours(random.nextInt(48) + 1));
        
        // Set initial status based on fill level
        if (fillLevel >= 90) {
            bin.setStatus(Bin.BinStatus.FULL);
            bin.setAlertFlag(true);
        } else if (fillLevel >= 60) {
            bin.setStatus(Bin.BinStatus.PARTIAL);
            bin.setAlertFlag(false);
        } else {
            bin.setStatus(Bin.BinStatus.EMPTY);
            bin.setAlertFlag(false);
        }
        
        binRepository.save(bin);
    }
}
