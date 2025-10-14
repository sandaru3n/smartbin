package com.sliit.smartbin.smartbin.config;

import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.model.Collection;
import com.sliit.smartbin.smartbin.model.Route;
import com.sliit.smartbin.smartbin.model.RouteBin;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.repository.BinRepository;
import com.sliit.smartbin.smartbin.repository.CollectionRepository;
import com.sliit.smartbin.smartbin.repository.RouteBinRepository;
import com.sliit.smartbin.smartbin.repository.RouteRepository;
import com.sliit.smartbin.smartbin.repository.UserRepository;
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

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataInitializer(UserRepository userRepository,
                           BinRepository binRepository,
                           CollectionRepository collectionRepository,
                           RouteRepository routeRepository,
                           RouteBinRepository routeBinRepository) {
        this.userRepository = userRepository;
        this.binRepository = binRepository;
        this.collectionRepository = collectionRepository;
        this.routeRepository = routeRepository;
        this.routeBinRepository = routeBinRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (userRepository.count() > 0) {
            System.out.println("Database already contains data. Skipping initialization.");
            return;
        }

        System.out.println("Initializing database with sample data...");

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
        
        // Colombo area bins
        createBin("QR001", "Colombo Fort", 6.9344, 79.8428, Bin.BinType.STANDARD, Bin.BinStatus.FULL, 95);
        createBin("QR002", "Pettah Market", 6.9369, 79.8581, Bin.BinType.STANDARD, Bin.BinStatus.PARTIAL, 65);
        createBin("QR003", "Galle Face Green", 6.9271, 79.8612, Bin.BinType.RECYCLING, Bin.BinStatus.EMPTY, 10);
        createBin("QR004", "Liberty Plaza", 6.9105, 79.8547, Bin.BinType.STANDARD, Bin.BinStatus.FULL, 90);
        createBin("QR005", "Bambalapitiya", 6.8881, 79.8603, Bin.BinType.STANDARD, Bin.BinStatus.PARTIAL, 45);
        
        // Kandy area bins
        createBin("QR006", "Kandy City Center", 7.2906, 80.6337, Bin.BinType.STANDARD, Bin.BinStatus.FULL, 85);
        createBin("QR007", "Temple of Tooth", 7.2944, 80.6414, Bin.BinType.RECYCLING, Bin.BinStatus.EMPTY, 5);
        createBin("QR008", "Kandy Lake", 7.2931, 80.6406, Bin.BinType.STANDARD, Bin.BinStatus.PARTIAL, 55);
        
        // Galle area bins
        createBin("QR009", "Galle Fort", 6.0535, 80.2210, Bin.BinType.STANDARD, Bin.BinStatus.FULL, 88);
        createBin("QR010", "Galle Market", 6.0556, 80.2181, Bin.BinType.RECYCLING, Bin.BinStatus.EMPTY, 15);
        
        System.out.println("✓ Created 10 sample bins");
    }
    
    private void createBin(String qrCode, String location, Double latitude, Double longitude, 
                          Bin.BinType binType, Bin.BinStatus status, Integer fillLevel) {
        Bin bin = new Bin();
        bin.setQrCode(qrCode);
        bin.setLocation(location);
        bin.setLatitude(latitude);
        bin.setLongitude(longitude);
        bin.setBinType(binType);
        bin.setStatus(status);
        bin.setFillLevel(fillLevel);
        bin.setAlertFlag(status == Bin.BinStatus.FULL && fillLevel > 90);
        bin.setLastEmptied(LocalDateTime.now().minusHours(24));
        
        binRepository.save(bin);
        System.out.println("✓ Created bin: " + qrCode + " at " + location);
    }
    
    private void createSampleCollections() {
        System.out.println("Creating sample collections...");
        
        List<User> collectors = userRepository.findAll().stream()
            .filter(user -> user.getRole() == User.UserRole.COLLECTOR)
            .toList();
        
        List<Bin> bins = binRepository.findAll();
        
        if (!collectors.isEmpty() && !bins.isEmpty()) {
            User collector = collectors.get(0);
            
            // Create some completed collections
            for (int i = 0; i < 3; i++) {
                Bin bin = bins.get(i);
                createCollection(bin, collector, Collection.CollectionType.STANDARD, 
                               Collection.CollectionStatus.COMPLETED, "Mixed waste", 100);
            }
            
            // Create some assigned collections
            for (int i = 3; i < 5; i++) {
                Bin bin = bins.get(i);
                createCollection(bin, collector, Collection.CollectionType.STANDARD, 
                               Collection.CollectionStatus.ASSIGNED, null, null);
            }
        }
        
        System.out.println("✓ Created sample collections");
    }
    
    private void createCollection(Bin bin, User collector, Collection.CollectionType type, 
                                 Collection.CollectionStatus status, String wasteType, Integer wasteLevel) {
        Collection collection = new Collection();
        collection.setBin(bin);
        collection.setCollector(collector);
        collection.setCollectionType(type);
        collection.setStatus(status);
        collection.setWasteType(wasteType);
        collection.setWasteLevel(wasteLevel);
        collection.setCollectionDate(LocalDateTime.now().minusDays(1));
        
        if (status == Collection.CollectionStatus.COMPLETED) {
            collection.setCompletionDate(LocalDateTime.now().minusHours(2));
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
            User collector = collectors.get(0);
            User authority = authorities.get(0);
            
            // Create a completed route
            Route completedRoute = new Route();
            completedRoute.setRouteName("Colombo Central Route");
            completedRoute.setCollector(collector);
            completedRoute.setAuthority(authority);
            completedRoute.setStatus(Route.RouteStatus.COMPLETED);
            completedRoute.setAssignedDate(LocalDateTime.now().minusDays(1));
            completedRoute.setStartedDate(LocalDateTime.now().minusDays(1).plusHours(1));
            completedRoute.setCompletedDate(LocalDateTime.now().minusHours(2));
            completedRoute.setEstimatedDurationMinutes(120);
            completedRoute.setActualDurationMinutes(95);
            completedRoute.setTotalDistanceKm(15.5);
            
            Route savedRoute = routeRepository.save(completedRoute);
            
            // Add bins to the route
            List<Bin> routeBins = bins.subList(0, Math.min(3, bins.size()));
            for (int i = 0; i < routeBins.size(); i++) {
                RouteBin routeBin = new RouteBin();
                routeBin.setRoute(savedRoute);
                routeBin.setBin(routeBins.get(i));
                routeBin.setSequenceOrder(i + 1);
                routeBin.setStatus(RouteBin.CollectionStatus.COMPLETED);
                routeBin.setVisitedDate(LocalDateTime.now().minusHours(3));
                
                routeBinRepository.save(routeBin);
            }
            
            // Create an assigned route
            Route assignedRoute = new Route();
            assignedRoute.setRouteName("Kandy City Route");
            assignedRoute.setCollector(collectors.size() > 1 ? collectors.get(1) : collector);
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
        }
        
        System.out.println("✓ Created sample routes");
    }
}

