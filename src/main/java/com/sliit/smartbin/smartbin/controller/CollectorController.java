package com.sliit.smartbin.smartbin.controller;

import com.sliit.smartbin.smartbin.dto.CollectionDTO;
import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.model.Collection;
import com.sliit.smartbin.smartbin.model.Route;
import com.sliit.smartbin.smartbin.model.RouteBin;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.service.BinService;
import com.sliit.smartbin.smartbin.service.CollectionService;
import com.sliit.smartbin.smartbin.service.RouteService;
import com.sliit.smartbin.smartbin.repository.RouteBinRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Optional;

@Controller
@RequestMapping("/collector")
public class CollectorController {

    private final RouteService routeService;
    private final CollectionService collectionService;
    private final BinService binService;
    private final RouteBinRepository routeBinRepository;

    public CollectorController(RouteService routeService,
                               CollectionService collectionService,
                               BinService binService,
                               RouteBinRepository routeBinRepository) {
        this.routeService = routeService;
        this.collectionService = collectionService;
        this.binService = binService;
        this.routeBinRepository = routeBinRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.COLLECTOR) {
            return "redirect:/collector/login";
        }
        
        model.addAttribute("user", user);
        
        // Get assigned routes
        List<Route> assignedRoutes = routeService.findAssignedRoutesByCollector(user);
        model.addAttribute("assignedRoutes", assignedRoutes);
        
        // Get active routes
        List<Route> activeRoutes = routeService.findActiveRoutesByCollector(user);
        model.addAttribute("activeRoutes", activeRoutes);
        
        // Get assigned collections
        List<Collection> assignedCollections = collectionService.getAssignedCollectionsByCollector(user);
        model.addAttribute("assignedCollections", assignedCollections);
        
        // Get completed collections count for today
        Long completedCount = collectionService.getCompletedCollectionsCountByCollector(user);
        model.addAttribute("completedCollections", completedCount);
        
        // Mock notification count (in real app, this would come from a notification service)
        model.addAttribute("newNotifications", assignedRoutes.size() > 0 ? 1 : 0);
        
        return "collector/dashboard";
    }

    @GetMapping("/routes")
    public String viewRoutes(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.COLLECTOR) {
            return "redirect:/collector/login";
        }
        
        List<Route> routes = routeService.findRoutesByCollector(user);
        
        // Calculate route counts by status
        long assignedCount = routes.stream().filter(r -> r.getStatus() == Route.RouteStatus.ASSIGNED).count();
        long inProgressCount = routes.stream().filter(r -> r.getStatus() == Route.RouteStatus.IN_PROGRESS).count();
        long completedCount = routes.stream().filter(r -> r.getStatus() == Route.RouteStatus.COMPLETED).count();
        
        model.addAttribute("routes", routes);
        model.addAttribute("user", user);
        model.addAttribute("assignedCount", assignedCount);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("totalRoutes", routes.size());
        
        return "collector/routes";
    }

    @GetMapping("/route/{id}")
    @Transactional(readOnly = true)
    public String viewRouteDetails(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.COLLECTOR) {
            return "redirect:/collector/login";
        }
        
        Optional<Route> route = routeService.findById(id);
        if (route.isPresent()) {
            // Verify that the route belongs to the current collector
            if (!route.get().getCollector().getId().equals(user.getId())) {
                return "redirect:/collector/dashboard";
            }
            
            List<RouteBin> routeBins = routeBinRepository.findByRouteIdOrderBySequence(id);
            
            // Eagerly initialize the bin proxy to avoid LazyInitializationException
            routeBins.forEach(routeBin -> {
                if (routeBin.getBin() != null) {
                    // Access a property to force initialization
                    routeBin.getBin().getLocation();
                }
            });
            
            model.addAttribute("route", route.get());
            model.addAttribute("routeBins", routeBins);
            model.addAttribute("user", user);
            return "collector/route-details";
        } else {
            return "redirect:/collector/routes";
        }
    }

    @PostMapping("/route/{id}/start")
    public String startRoute(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.COLLECTOR) {
            return "redirect:/collector/login";
        }
        
        try {
            Route route = routeService.startRoute(id);
            redirectAttributes.addFlashAttribute("success", "Route '" + route.getRouteName() + "' started successfully! You can now start collecting bins.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to start route: " + e.getMessage());
        }
        
        return "redirect:/collector/routes";
    }

    @PostMapping("/route/{id}/complete")
    public String completeRoute(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.COLLECTOR) {
            return "redirect:/collector/login";
        }
        
        try {
            Route route = routeService.completeRoute(id);
            redirectAttributes.addFlashAttribute("success", "Route '" + route.getRouteName() + "' completed successfully! Great job!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to complete route: " + e.getMessage());
        }
        
        return "redirect:/collector/routes";
    }

    @GetMapping("/scan-qr")
    public String scanQR(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.COLLECTOR) {
            return "redirect:/collector/login";
        }
        
        model.addAttribute("user", user);
        return "collector/scan-qr";
    }

    @PostMapping("/scan-qr")
    public String processQRScan(@RequestParam String qrCode, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.COLLECTOR) {
            return "redirect:/collector/login";
        }
        
        try {
            Optional<Bin> bin = binService.findByQrCode(qrCode);
            if (bin.isPresent()) {
                redirectAttributes.addFlashAttribute("bin", bin.get());
                redirectAttributes.addFlashAttribute("success", "QR Code scanned successfully!");
                return "redirect:/collector/bin/" + bin.get().getId() + "/collect";
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid QR Code!");
                return "redirect:/collector/scan-qr";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to scan QR Code: " + e.getMessage());
            return "redirect:/collector/scan-qr";
        }
    }

    @GetMapping("/bin/{id}/collect")
    public String collectBin(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.COLLECTOR) {
            return "redirect:/collector/login";
        }
        
        Optional<Bin> bin = binService.findById(id);
        if (bin.isPresent()) {
            model.addAttribute("bin", bin.get());
            model.addAttribute("user", user);
            return "collector/collect-bin";
        } else {
            return "redirect:/collector/dashboard";
        }
    }

    @PostMapping("/bin/{id}/collect")
    public String processCollection(@PathVariable Long id,
                                   @RequestParam String wasteType,
                                   @RequestParam Integer wasteLevel,
                                   @RequestParam(required = false) String notes,
                                   HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.COLLECTOR) {
            return "redirect:/collector/login";
        }
        
        try {
            // 8a. System logs collection in database
            Optional<Bin> binOpt = binService.findById(id);
            if (!binOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Bin not found!");
                return "redirect:/collector/scan-qr";
            }
            
            Bin bin = binOpt.get();
            
            // Determine collection type based on bin type
            Collection.CollectionType collectionType;
            if (bin.getBinType() == Bin.BinType.RECYCLING) {
                collectionType = Collection.CollectionType.RECYCLING;
            } else if (bin.getBinType() == Bin.BinType.BULK) {
                collectionType = Collection.CollectionType.BULK;
            } else {
                collectionType = Collection.CollectionType.STANDARD;
            }
            
            CollectionDTO collectionDTO = new CollectionDTO();
            collectionDTO.setBinId(id);
            collectionDTO.setCollectorId(user.getId());
            collectionDTO.setCollectionType(collectionType);
            collectionDTO.setStatus(Collection.CollectionStatus.COMPLETED);
            collectionDTO.setWasteType(wasteType);
            collectionDTO.setWasteLevel(wasteLevel);
            collectionDTO.setNotes(notes);
            
            // 8a. Log collection in database
            Collection collection = collectionService.createCollection(collectionDTO);
            
            // 7b. System resets bin stats (for recycle bins)
            if (bin.getBinType() == Bin.BinType.RECYCLING) {
                // Reset fill level to 0 for recycle bins
                binService.updateBinStatus(id, Bin.BinStatus.EMPTY, 0);
            } else {
                // For standard bins, update status based on collection
                int newFillLevel = Math.max(0, bin.getFillLevel() - wasteLevel);
                Bin.BinStatus newStatus = newFillLevel < 25 ? Bin.BinStatus.EMPTY : 
                                         newFillLevel < 75 ? Bin.BinStatus.PARTIAL : Bin.BinStatus.FULL;
                binService.updateBinStatus(id, newStatus, newFillLevel);
            }
            
            // 8b. Database confirms collection logged
            // 9a. System confirms completion to collector
            String successMessage = String.format(
                "Collection completed successfully! Bin %s at %s - Type: %s, Level collected: %d%%",
                bin.getQrCode(), bin.getLocation(), collectionType, wasteLevel
            );
            redirectAttributes.addFlashAttribute("success", successMessage);
            redirectAttributes.addFlashAttribute("collectionId", collection.getId());
            
            // 9b. System updates authority report (logged in collection record)
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to complete collection: " + e.getMessage());
        }
        
        return "redirect:/collector/dashboard";
    }

    @GetMapping("/collections")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String viewCollections(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.COLLECTOR) {
            return "redirect:/collector/login";
        }
        
        List<Collection> collections = collectionService.findCollectionsByCollector(user);
        
        // Eagerly load bin relationships to avoid LazyInitializationException
        collections.forEach(c -> {
            if (c.getBin() != null) {
                c.getBin().getLocation(); // Force initialization
            }
        });
        
        // Calculate collection statistics
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate weekStart = today.minusDays(7);
        java.time.LocalDate monthStart = today.minusDays(30);
        
        long todayCount = collections.stream()
            .filter(c -> c.getCollectionDate().toLocalDate().equals(today))
            .count();
        
        long weekCount = collections.stream()
            .filter(c -> c.getCollectionDate().toLocalDate().isAfter(weekStart))
            .count();
        
        long monthCount = collections.stream()
            .filter(c -> c.getCollectionDate().toLocalDate().isAfter(monthStart))
            .count();
        
        long standardCount = collections.stream()
            .filter(c -> c.getCollectionType() == Collection.CollectionType.STANDARD)
            .count();
        
        long recyclingCount = collections.stream()
            .filter(c -> c.getCollectionType() == Collection.CollectionType.RECYCLING)
            .count();
        
        long bulkCount = collections.stream()
            .filter(c -> c.getCollectionType() == Collection.CollectionType.BULK)
            .count();
        
        model.addAttribute("collections", collections);
        model.addAttribute("user", user);
        model.addAttribute("todayCount", todayCount);
        model.addAttribute("weekCount", weekCount);
        model.addAttribute("monthCount", monthCount);
        model.addAttribute("standardCount", standardCount);
        model.addAttribute("recyclingCount", recyclingCount);
        model.addAttribute("bulkCount", bulkCount);
        model.addAttribute("totalCollections", collections.size());
        
        return "collector/collections";
    }

    @GetMapping("/api/live-updates")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getLiveUpdates(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.COLLECTOR) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            List<Map<String, Object>> updates = new ArrayList<>();
            
            // Get recent route assignments
            List<Route> recentRoutes = routeService.findRoutesByCollector(user);
            for (Route route : recentRoutes) {
                if (route.getAssignedDate() != null && 
                    route.getAssignedDate().isAfter(LocalDateTime.now().minusHours(24))) {
                    
                    Map<String, Object> update = new HashMap<>();
                    update.put("type", "route");
                    update.put("title", "New Route Assigned");
                    update.put("message", "Route " + route.getRouteName() + " has been assigned to you");
                    update.put("timestamp", route.getAssignedDate());
                    updates.add(update);
                }
            }
            
            // Get recent collections
            List<Collection> recentCollections = collectionService.findCollectionsByCollector(user);
            for (Collection collection : recentCollections) {
                if (collection.getCollectionDate() != null && 
                    collection.getCollectionDate().isAfter(LocalDateTime.now().minusHours(24))) {
                    
                    Map<String, Object> update = new HashMap<>();
                    update.put("type", "completion");
                    update.put("title", "Collection Completed");
                    update.put("message", "Successfully collected from bin " + collection.getBin().getQrCode());
                    update.put("timestamp", collection.getCollectionDate());
                    updates.add(update);
                }
            }
            
            // Sort by timestamp (newest first)
            updates.sort((a, b) -> {
                LocalDateTime timeA = (LocalDateTime) a.get("timestamp");
                LocalDateTime timeB = (LocalDateTime) b.get("timestamp");
                return timeB.compareTo(timeA);
            });
            
            return ResponseEntity.ok(updates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/api/notifications")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getNotifications(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.COLLECTOR) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            List<Map<String, Object>> notifications = new ArrayList<>();
            
            // Check for new route assignments
            List<Route> assignedRoutes = routeService.findAssignedRoutesByCollector(user);
            for (Route route : assignedRoutes) {
                Map<String, Object> notification = new HashMap<>();
                notification.put("type", "route");
                notification.put("title", "New Route Assignment");
                notification.put("message", "You have been assigned route: " + route.getRouteName());
                notification.put("timestamp", LocalDateTime.now());
                notification.put("seen", false);
                notifications.add(notification);
            }
            
            // Check for overdue bins in collector's region
            List<Bin> overdueBins = binService.findOverdueBins();
            for (Bin bin : overdueBins) {
                if (bin.getLocation() != null && bin.getLocation().contains(user.getRegion())) {
                    Map<String, Object> notification = new HashMap<>();
                    notification.put("type", "alert");
                    notification.put("title", "Overdue Bin Alert");
                    notification.put("message", "Bin " + bin.getQrCode() + " is overdue for collection");
                    notification.put("timestamp", LocalDateTime.now());
                    notification.put("seen", false);
                    notifications.add(notification);
                }
            }
            
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/api/route-status")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getRouteStatus(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.COLLECTOR) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            List<Route> routes = routeService.findRoutesByCollector(user);
            List<Map<String, Object>> routeStatuses = new ArrayList<>();
            
            for (Route route : routes) {
                Map<String, Object> routeData = new HashMap<>();
                routeData.put("id", route.getId());
                routeData.put("status", route.getStatus().name());
                routeData.put("routeName", route.getRouteName());
                
                // Add route bins with details
                List<RouteBin> routeBins = route.getRouteBins();
                if (routeBins != null && !routeBins.isEmpty()) {
                    List<Map<String, Object>> binsData = new ArrayList<>();
                    
                    for (int i = 0; i < routeBins.size(); i++) {
                        RouteBin routeBin = routeBins.get(i);
                        Map<String, Object> binData = new HashMap<>();
                        
                        binData.put("sequenceOrder", routeBin.getSequenceOrder());
                        binData.put("qrCode", routeBin.getBin().getQrCode());
                        binData.put("location", routeBin.getBin().getLocation());
                        binData.put("status", routeBin.getBin().getStatus().name());
                        binData.put("fillLevel", routeBin.getBin().getFillLevel());
                        binData.put("binType", routeBin.getBin().getBinType().name());
                        
                        // Calculate distance to next bin (mock calculation)
                        if (i < routeBins.size() - 1) {
                            double distance = calculateDistance(
                                routeBin.getBin().getLatitude(), routeBin.getBin().getLongitude(),
                                routeBins.get(i + 1).getBin().getLatitude(), routeBins.get(i + 1).getBin().getLongitude()
                            );
                            binData.put("distanceToNext", Math.round(distance * 10.0) / 10.0); // Round to 1 decimal
                        } else {
                            binData.put("distanceToNext", 0.0);
                        }
                        
                        binsData.add(binData);
                    }
                    
                    routeData.put("routeBins", binsData);
                    routeData.put("totalBins", routeBins.size());
                    
                    // Calculate progress for in-progress routes
                    if (route.getStatus() == Route.RouteStatus.IN_PROGRESS) {
                        long completedBins = routeBins.stream()
                            .mapToLong(rb -> rb.getStatus() == RouteBin.CollectionStatus.COMPLETED ? 1 : 0)
                            .sum();
                        
                        double progressPercent = (double) completedBins / routeBins.size() * 100;
                        routeData.put("progressPercent", progressPercent);
                        routeData.put("completedBins", completedBins);
                    }
                    
                    // Calculate duration
                    if (route.getStartedDate() != null) {
                        long durationMinutes = java.time.Duration.between(
                            route.getStartedDate(), 
                            LocalDateTime.now()
                        ).toMinutes();
                        routeData.put("duration", durationMinutes);
                    }
                }
                
                routeStatuses.add(routeData);
            }
            
            return ResponseEntity.ok(routeStatuses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/performance")
    public String viewPerformance(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.COLLECTOR) {
            return "redirect:/collector/login";
        }
        
        List<Collection> collections = collectionService.findCollectionsByCollector(user);
        List<Route> routes = routeService.findRoutesByCollector(user);
        
        Long completedCollections = collectionService.getCompletedCollectionsCountByCollector(user);
        long completedRoutes = routes.stream()
            .filter(r -> r.getStatus() == Route.RouteStatus.COMPLETED)
            .count();
        
        model.addAttribute("user", user);
        model.addAttribute("totalCollections", collections.size());
        model.addAttribute("completedCollections", completedCollections);
        model.addAttribute("totalRoutes", routes.size());
        model.addAttribute("completedRoutes", completedRoutes);
        
        return "collector/performance";
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }
}

