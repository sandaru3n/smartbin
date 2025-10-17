package com.sliit.smartbin.smartbin.config;

import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.model.Collection;
import com.sliit.smartbin.smartbin.model.Route;
import com.sliit.smartbin.smartbin.model.RouteBin;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.repository.BinRepository;
import com.sliit.smartbin.smartbin.repository.BulkRequestRepository;
import com.sliit.smartbin.smartbin.repository.CollectionRepository;
import com.sliit.smartbin.smartbin.repository.RecyclingTransactionRepository;
import com.sliit.smartbin.smartbin.repository.RouteBinRepository;
import com.sliit.smartbin.smartbin.repository.RouteRepository;
import com.sliit.smartbin.smartbin.repository.UserRepository;
import com.sliit.smartbin.smartbin.repository.WasteDisposalRepository;
import com.sliit.smartbin.smartbin.repository.BinAssignmentRepository;
import com.sliit.smartbin.smartbin.repository.RegionAssignmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BinRepository binRepository;
    private final CollectionRepository collectionRepository;
    private final RouteRepository routeRepository;
    private final RouteBinRepository routeBinRepository;

    private final RecyclingTransactionRepository recyclingTransactionRepository;
    private final WasteDisposalRepository wasteDisposalRepository;
    private final BinAssignmentRepository binAssignmentRepository;
    private final BulkRequestRepository bulkRequestRepository;
    private final RegionAssignmentRepository regionAssignmentRepository;


    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataInitializer(UserRepository userRepository,
                           BinRepository binRepository,
                           CollectionRepository collectionRepository,
                           RouteRepository routeRepository,
                           RouteBinRepository routeBinRepository,
                           RecyclingTransactionRepository recyclingTransactionRepository,
                           WasteDisposalRepository wasteDisposalRepository,
                           BinAssignmentRepository binAssignmentRepository,
                           BulkRequestRepository bulkRequestRepository,
                           RegionAssignmentRepository regionAssignmentRepository) {

        this.userRepository = userRepository;
        this.binRepository = binRepository;
        this.collectionRepository = collectionRepository;
        this.routeRepository = routeRepository;
        this.routeBinRepository = routeBinRepository;
        this.recyclingTransactionRepository = recyclingTransactionRepository;
        this.wasteDisposalRepository = wasteDisposalRepository;
        this.binAssignmentRepository = binAssignmentRepository;
        this.bulkRequestRepository = bulkRequestRepository;
        this.regionAssignmentRepository = regionAssignmentRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Initializing database with sample data...");
        
        // Clear existing data to ensure fresh start
        System.out.println("Clearing existing data...");
        binAssignmentRepository.deleteAll(); // Delete bin assignments first (has FK to users)
        regionAssignmentRepository.deleteAll(); // Delete region assignments (has FK to users)
        routeBinRepository.deleteAll();
        collectionRepository.deleteAll();
        routeRepository.deleteAll();
        
        // Delete new waste management tables
        wasteDisposalRepository.deleteAll();
        recyclingTransactionRepository.deleteAll();
        
        // Delete bulk requests before users (FK constraint)
        bulkRequestRepository.deleteAll();
        
        binRepository.deleteAll();
        userRepository.deleteAll();

        // Create sample residents
        createUser(
            "John Doe",
            "john.resident@smartbin.com",
            "password123",
            "+94 771234567",
            "123 Main Street, Colombo 05",
            User.UserRole.RESIDENT
        );

        createUser(
            "Sarah Williams",
            "sarah.resident@smartbin.com",
            "password123",
            "+94 772345678",
            "456 Lake Road, Kandy",
            User.UserRole.RESIDENT
        );

        createUser(
            "Michael Brown",
            "michael.resident@smartbin.com",
            "password123",
            "+94 773456789",
            "789 Park Avenue, Galle",
            User.UserRole.RESIDENT
        );

        // Create sample waste collectors
        createUser(
            "David Collector",
            "david.collector@smartbin.com",
            "password123",
            "+94 774567890",
            "321 Collector Lane, Colombo 03",
            User.UserRole.COLLECTOR
        );

        createUser(
            "Emma Wilson",
            "emma.collector@smartbin.com",
            "password123",
            "+94 775678901",
            "654 Service Road, Negombo",
            User.UserRole.COLLECTOR
        );

        createUser(
            "James Taylor",
            "james.collector@smartbin.com",
            "password123",
            "+94 776789012",
            "987 Industrial Area, Kelaniya",
            User.UserRole.COLLECTOR
        );

        createUser(
            "Collector One",
            "collecter1@gmail.com",
            "password123",
            "+94 771234567",
            "Collector Street, Colombo",
            User.UserRole.COLLECTOR
        );

        // Create sample authority users
        createUser(
            "Admin Authority",
            "admin.authority@smartbin.com",
            "password123",
            "+94 777890123",
            "Municipal Building, Colombo 07",
            User.UserRole.AUTHORITY
        );

        createUser(
            "Lisa Manager",
            "lisa.authority@smartbin.com",
            "password123",
            "+94 778901234",
            "City Council Office, Kandy",
            User.UserRole.AUTHORITY
        );

        createUser(
            "Robert Supervisor",
            "robert.authority@smartbin.com",
            "password123",
            "+94 779012345",
            "District Office, Galle",
            User.UserRole.AUTHORITY
        );

        createUser(
            "Waste Manager",
            "waste@gmail.com",
            "password123",
            "+94 770123456",
            "Waste Management Office, Colombo",
            User.UserRole.AUTHORITY
        );

        // Create sample bins
        createSampleBins();
        
        // Create sample collections
        createSampleCollections();
        
        // Create sample routes
        createSampleRoutes();
        
        System.out.println("✓ Sample data initialized successfully!");
        System.out.println("\n==============================================");
        System.out.println("SAMPLE LOGIN CREDENTIALS");
        System.out.println("==============================================");
        System.out.println("\nRESIDENT ACCOUNTS:");
        System.out.println("  Email: john.resident@smartbin.com");
        System.out.println("  Email: sarah.resident@smartbin.com");
        System.out.println("  Email: michael.resident@smartbin.com");
        System.out.println("\nCOLLECTOR ACCOUNTS:");
        System.out.println("  Email: david.collector@smartbin.com");
        System.out.println("  Email: emma.collector@smartbin.com");
        System.out.println("  Email: james.collector@smartbin.com");
        System.out.println("  Email: collecter1@gmail.com");
        System.out.println("\nAUTHORITY ACCOUNTS:");
        System.out.println("  Email: admin.authority@smartbin.com");
        System.out.println("  Email: lisa.authority@smartbin.com");
        System.out.println("  Email: robert.authority@smartbin.com");
        System.out.println("\nPassword for all accounts: password123");
        System.out.println("==============================================\n");
    }

    private void createUser(String name, String email, String password, 
                           String phone, String address, User.UserRole role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setAddress(address);
        user.setRole(role);
        
        userRepository.save(user);
        System.out.println("✓ Created " + role + " user: " + name + " (" + email + ")");
    }
    
    private void createSampleBins() {
        System.out.println("Creating sample bins...");
        
        // Colombo area bins - North District
        createBin("QR001", "Colombo Fort Station", 6.9344, 79.8428, Bin.BinType.STANDARD, Bin.BinStatus.FULL, 95, 50);
        createBin("QR002", "Pettah Market", 6.9369, 79.8581, Bin.BinType.STANDARD, Bin.BinStatus.PARTIAL, 65, 12);
        createBin("QR003", "Galle Face Green", 6.9271, 79.8612, Bin.BinType.RECYCLING, Bin.BinStatus.EMPTY, 10, 2);
        createBin("QR004", "Liberty Plaza", 6.9105, 79.8547, Bin.BinType.STANDARD, Bin.BinStatus.FULL, 90, 36);
        createBin("QR005", "Bambalapitiya Junction", 6.8881, 79.8603, Bin.BinType.STANDARD, Bin.BinStatus.PARTIAL, 45, 8);
        createBin("QR006", "Maradana Railway", 6.9297, 79.8656, Bin.BinType.BULK, Bin.BinStatus.OVERDUE, 98, 72);
        createBin("QR007", "Slave Island", 6.9250, 79.8500, Bin.BinType.STANDARD, Bin.BinStatus.FULL, 88, 28);
        
        // Colombo area bins - South District
        createBin("QR008", "Wellawatte Beach", 6.8700, 79.8600, Bin.BinType.RECYCLING, Bin.BinStatus.PARTIAL, 55, 10);
        createBin("QR009", "Dehiwala Zoo", 6.8500, 79.8700, Bin.BinType.STANDARD, Bin.BinStatus.EMPTY, 15, 3);
        createBin("QR010", "Mount Lavinia", 6.8380, 79.8630, Bin.BinType.STANDARD, Bin.BinStatus.FULL, 92, 40);
        createBin("QR011", "Kollupitiya", 6.9150, 79.8500, Bin.BinType.STANDARD, Bin.BinStatus.PARTIAL, 60, 18);
        createBin("QR012", "Cinnamon Gardens", 6.9050, 79.8650, Bin.BinType.RECYCLING, Bin.BinStatus.EMPTY, 20, 5);
        
        // Colombo area bins - East District
        createBin("QR013", "Borella Junction", 6.9150, 79.8800, Bin.BinType.STANDARD, Bin.BinStatus.FULL, 85, 32);
        createBin("QR014", "Rajagiriya", 6.9080, 79.8900, Bin.BinType.STANDARD, Bin.BinStatus.PARTIAL, 70, 15);
        createBin("QR015", "Nugegoda", 6.8650, 79.8900, Bin.BinType.BULK, Bin.BinStatus.OVERDUE, 96, 55);
        createBin("QR016", "Kotte Parliament", 6.8950, 79.9100, Bin.BinType.STANDARD, Bin.BinStatus.EMPTY, 25, 6);
        createBin("QR017", "Battaramulla", 6.8980, 79.9200, Bin.BinType.RECYCLING, Bin.BinStatus.PARTIAL, 50, 12);
        
        // Colombo area bins - West District
        createBin("QR018", "Negombo Beach", 7.2083, 79.8358, Bin.BinType.STANDARD, Bin.BinStatus.FULL, 90, 38);
        createBin("QR019", "Wattala", 6.9900, 79.8900, Bin.BinType.STANDARD, Bin.BinStatus.PARTIAL, 55, 14);
        createBin("QR020", "Ja-Ela", 7.0750, 79.8920, Bin.BinType.STANDARD, Bin.BinStatus.EMPTY, 18, 4);
        createBin("QR021", "Katunayake Airport", 7.1807, 79.8841, Bin.BinType.BULK, Bin.BinStatus.FULL, 94, 42);
        createBin("QR022", "Seeduwa", 7.1200, 79.8850, Bin.BinType.RECYCLING, Bin.BinStatus.PARTIAL, 48, 10);
        
        // Kandy area bins
        createBin("QR023", "Kandy City Center", 7.2906, 80.6337, Bin.BinType.STANDARD, Bin.BinStatus.FULL, 85, 30);
        createBin("QR024", "Temple of Tooth", 7.2944, 80.6414, Bin.BinType.RECYCLING, Bin.BinStatus.EMPTY, 5, 1);
        createBin("QR025", "Kandy Lake", 7.2931, 80.6406, Bin.BinType.STANDARD, Bin.BinStatus.PARTIAL, 55, 16);
        createBin("QR026", "Peradeniya Gardens", 7.2700, 80.5950, Bin.BinType.STANDARD, Bin.BinStatus.FULL, 88, 35);
        createBin("QR027", "Kandy Market", 7.2950, 80.6350, Bin.BinType.BULK, Bin.BinStatus.OVERDUE, 97, 68);
        
        // Galle area bins
        createBin("QR028", "Galle Fort", 6.0535, 80.2210, Bin.BinType.STANDARD, Bin.BinStatus.FULL, 88, 33);
        createBin("QR029", "Galle Market", 6.0556, 80.2181, Bin.BinType.RECYCLING, Bin.BinStatus.EMPTY, 15, 3);
        createBin("QR030", "Unawatuna Beach", 6.0100, 80.2500, Bin.BinType.STANDARD, Bin.BinStatus.PARTIAL, 62, 20);
        createBin("QR031", "Galle Bus Stand", 6.0560, 80.2200, Bin.BinType.STANDARD, Bin.BinStatus.FULL, 91, 40);
        createBin("QR032", "Hikkaduwa", 6.1400, 80.1000, Bin.BinType.RECYCLING, Bin.BinStatus.EMPTY, 22, 5);
        
        System.out.println("✓ Created 32 sample bins with varied statuses");
    }
    
    private void createBin(String qrCode, String location, Double latitude, Double longitude, 
                          Bin.BinType binType, Bin.BinStatus status, Integer fillLevel, int hoursAgo) {
        // Check if bin with this QR code already exists
        if (binRepository.findByQrCode(qrCode).isPresent()) {
            System.out.println("⚠ Skipped bin: " + qrCode + " already exists");
            return;
        }
        
        Bin bin = new Bin();
        bin.setQrCode(qrCode);
        bin.setLocation(location);
        bin.setLatitude(latitude);
        bin.setLongitude(longitude);
        bin.setBinType(binType);
        bin.setStatus(status);
        bin.setFillLevel(fillLevel);
        bin.setAlertFlag(status == Bin.BinStatus.OVERDUE || (status == Bin.BinStatus.FULL && fillLevel > 90));
        bin.setLastEmptied(LocalDateTime.now().minusHours(hoursAgo));
        
        binRepository.save(bin);
        System.out.println("✓ Created bin: " + qrCode + " at " + location + " (Fill: " + fillLevel + "%)");
    }
    
    private void createSampleCollections() {
        System.out.println("Creating sample collections...");
        
        List<User> collectors = userRepository.findAll().stream()
            .filter(user -> user.getRole() == User.UserRole.COLLECTOR)
            .toList();
        
        List<Bin> bins = binRepository.findAll();
        
        if (!collectors.isEmpty() && !bins.isEmpty()) {
            // Create collections for the past week with different collectors
            int collectionCount = 0;
            
            // Today's collections (completed)
            for (int i = 0; i < 5; i++) {
                if (i < bins.size()) {
                    User collector = collectors.get(i % collectors.size());
                    Bin bin = bins.get(i);
                    createCollection(bin, collector, Collection.CollectionType.STANDARD, 
                                   Collection.CollectionStatus.COMPLETED, "Mixed waste", 95, 0);
                    collectionCount++;
                }
            }
            
            // Yesterday's collections (completed)
            for (int i = 5; i < 12; i++) {
                if (i < bins.size()) {
                    User collector = collectors.get(i % collectors.size());
                    Bin bin = bins.get(i);
                    Collection.CollectionType type = (i % 3 == 0) ? Collection.CollectionType.RECYCLING : Collection.CollectionType.STANDARD;
                    createCollection(bin, collector, type, 
                                   Collection.CollectionStatus.COMPLETED, "Recyclable materials", 88, 1);
                    collectionCount++;
                }
            }
            
            // 2 days ago collections (completed)
            for (int i = 12; i < 18; i++) {
                if (i < bins.size()) {
                    User collector = collectors.get(i % collectors.size());
                    Bin bin = bins.get(i);
                    createCollection(bin, collector, Collection.CollectionType.STANDARD, 
                                   Collection.CollectionStatus.COMPLETED, "General waste", 92, 2);
                    collectionCount++;
                }
            }
            
            // 3 days ago collections (completed)
            for (int i = 18; i < 23; i++) {
                if (i < bins.size()) {
                    User collector = collectors.get(i % collectors.size());
                    Bin bin = bins.get(i);
                    Collection.CollectionType type = (i % 2 == 0) ? Collection.CollectionType.BULK : Collection.CollectionType.STANDARD;
                    createCollection(bin, collector, type, 
                                   Collection.CollectionStatus.COMPLETED, "Bulk waste", 85, 3);
                    collectionCount++;
                }
            }
            
            // Last week collections (completed)
            for (int i = 23; i < 28; i++) {
                if (i < bins.size()) {
                    User collector = collectors.get(i % collectors.size());
                    Bin bin = bins.get(i);
                    createCollection(bin, collector, Collection.CollectionType.STANDARD, 
                                   Collection.CollectionStatus.COMPLETED, "Mixed waste", 90, 7);
                    collectionCount++;
                }
            }
            
            // Assigned collections (pending)
            for (int i = 28; i < 32; i++) {
                if (i < bins.size()) {
                    User collector = collectors.get(i % collectors.size());
                    Bin bin = bins.get(i);
                    createCollection(bin, collector, Collection.CollectionType.STANDARD, 
                                   Collection.CollectionStatus.ASSIGNED, null, null, 0);
                    collectionCount++;
                }
            }
            
            System.out.println("✓ Created " + collectionCount + " sample collections");
            
            // Ensure specific collector (collecter1@gmail.com) has collections for testing
            User testCollector = userRepository.findByEmail("collecter1@gmail.com").orElse(null);
            if (testCollector != null && bins.size() >= 10) {
                // Add 10 more collections specifically for this collector
                for (int i = 0; i < 10; i++) {
                    Bin bin = bins.get(i);
                    Collection.CollectionType type = (i % 3 == 0) ? Collection.CollectionType.RECYCLING : 
                                                     (i % 3 == 1) ? Collection.CollectionType.BULK : 
                                                     Collection.CollectionType.STANDARD;
                    int daysAgo = i / 3; // 0, 0, 0, 1, 1, 1, 2, 2, 2, 3
                    createCollection(bin, testCollector, type, 
                                   Collection.CollectionStatus.COMPLETED, 
                                   type == Collection.CollectionType.RECYCLING ? "Recyclables" : 
                                   type == Collection.CollectionType.BULK ? "Bulk waste" : "Mixed waste", 
                                   85 + i, daysAgo);
                }
                System.out.println("✓ Added 10 collections for test collector (collecter1@gmail.com)");
            }
        }
    }
    
    private void createCollection(Bin bin, User collector, Collection.CollectionType type, 
                                 Collection.CollectionStatus status, String wasteType, Integer wasteLevel, int daysAgo) {
        Collection collection = new Collection();
        collection.setBin(bin);
        collection.setCollector(collector);
        collection.setCollectionType(type);
        collection.setStatus(status);
        collection.setWasteType(wasteType);
        collection.setWasteLevel(wasteLevel);
        collection.setCollectionDate(LocalDateTime.now().minusDays(daysAgo));
        
        if (status == Collection.CollectionStatus.COMPLETED) {
            collection.setCompletionDate(LocalDateTime.now().minusDays(daysAgo).plusHours(2));
        }
        
        collectionRepository.save(collection);
    }
    
    private void createSampleRoutes() {
        System.out.println("Creating sample routes...");
        
        List<User> collectors = userRepository.findAll().stream()
            .filter(user -> user.getRole() == User.UserRole.COLLECTOR)
            .toList();
        
        List<User> authorities = userRepository.findAll().stream()
            .filter(user -> user.getRole() == User.UserRole.AUTHORITY)
            .toList();
        
        List<Bin> bins = binRepository.findAll();
        
        if (!collectors.isEmpty() && !authorities.isEmpty() && !bins.isEmpty()) {
            int routeCount = 0;
            
            // Create completed routes for the past week
            String[] routeNames = {
                "Colombo Central Route", "North District Route", "South District Route",
                "East District Route", "West District Route", "Kandy City Route",
                "Galle Fort Route", "Express Collection Route"
            };
            
            // Completed routes (last 7 days)
            for (int day = 0; day < 7; day++) {
                User collector = collectors.get(day % collectors.size());
                User authority = authorities.get(day % authorities.size());
                String routeName = routeNames[day % routeNames.length] + " - Day " + (day + 1);
                
                Route completedRoute = new Route();
                completedRoute.setRouteName(routeName);
                completedRoute.setCollector(collector);
                completedRoute.setAuthority(authority);
                completedRoute.setStatus(Route.RouteStatus.COMPLETED);
                completedRoute.setAssignedDate(LocalDateTime.now().minusDays(day).minusHours(8));
                completedRoute.setStartedDate(LocalDateTime.now().minusDays(day).minusHours(7));
                completedRoute.setCompletedDate(LocalDateTime.now().minusDays(day).minusHours(3));
                completedRoute.setEstimatedDurationMinutes(120 + (day * 10));
                completedRoute.setActualDurationMinutes(95 + (day * 8));
                completedRoute.setTotalDistanceKm(15.5 + (day * 2.5));
                
                Route savedRoute = routeRepository.save(completedRoute);
                
                // Add 4-5 bins to each route
                int binCount = 4 + (day % 2);
                int startIndex = (day * 4) % bins.size();
                for (int i = 0; i < binCount && (startIndex + i) < bins.size(); i++) {
                    RouteBin routeBin = new RouteBin();
                    routeBin.setRoute(savedRoute);
                    routeBin.setBin(bins.get(startIndex + i));
                    routeBin.setSequenceOrder(i + 1);
                    routeBin.setStatus(RouteBin.CollectionStatus.COMPLETED);
                    routeBin.setVisitedDate(LocalDateTime.now().minusDays(day).minusHours(6 - i));
                    
                    routeBinRepository.save(routeBin);
                }
                routeCount++;
            }
            
            // Create in-progress routes
            for (int i = 0; i < 2; i++) {
                User collector = collectors.get(i % collectors.size());
                User authority = authorities.get(0);
                
                Route inProgressRoute = new Route();
                inProgressRoute.setRouteName("Active Route " + (i + 1));
                inProgressRoute.setCollector(collector);
                inProgressRoute.setAuthority(authority);
                inProgressRoute.setStatus(Route.RouteStatus.IN_PROGRESS);
                inProgressRoute.setAssignedDate(LocalDateTime.now().minusHours(3));
                inProgressRoute.setStartedDate(LocalDateTime.now().minusHours(2));
                inProgressRoute.setEstimatedDurationMinutes(150);
                inProgressRoute.setTotalDistanceKm(18.0);
                
                Route savedRoute = routeRepository.save(inProgressRoute);
                
                // Add bins to in-progress route
                int startIndex = 28 + (i * 2);
                for (int j = 0; j < 3 && (startIndex + j) < bins.size(); j++) {
                    RouteBin routeBin = new RouteBin();
                    routeBin.setRoute(savedRoute);
                    routeBin.setBin(bins.get(startIndex + j));
                    routeBin.setSequenceOrder(j + 1);
                    routeBin.setStatus(j == 0 ? RouteBin.CollectionStatus.COMPLETED : RouteBin.CollectionStatus.PENDING);
                    if (j == 0) {
                        routeBin.setVisitedDate(LocalDateTime.now().minusMinutes(30));
                    }
                    
                    routeBinRepository.save(routeBin);
                }
                routeCount++;
            }
            
            // Create assigned routes (pending)
            User collector = collectors.get(0);
            User authority = authorities.get(0);
            
            Route assignedRoute = new Route();
            assignedRoute.setRouteName("Scheduled Morning Route");
            assignedRoute.setCollector(collector);
            assignedRoute.setAuthority(authority);
            assignedRoute.setStatus(Route.RouteStatus.ASSIGNED);
            assignedRoute.setAssignedDate(LocalDateTime.now());
            assignedRoute.setEstimatedDurationMinutes(90);
            assignedRoute.setTotalDistanceKm(12.0);
            
            Route savedAssignedRoute = routeRepository.save(assignedRoute);
            
            // Add bins to the assigned route
            List<Bin> assignedRouteBins = bins.subList(3, Math.min(6, bins.size()));
            for (int i = 0; i < assignedRouteBins.size(); i++) {
                RouteBin routeBin = new RouteBin();
                routeBin.setRoute(savedAssignedRoute);
                routeBin.setBin(assignedRouteBins.get(i));
                routeBin.setSequenceOrder(i + 1);
                routeBin.setStatus(RouteBin.CollectionStatus.PENDING);
                
                routeBinRepository.save(routeBin);
            }
            routeCount++;
            
            System.out.println("✓ Created " + routeCount + " sample routes (7 completed, 2 in-progress, 1 assigned)");
        }
    }
}

