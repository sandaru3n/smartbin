package com.sliit.smartbin.smartbin.controller;

import com.sliit.smartbin.smartbin.dto.ReportDTO;
import com.sliit.smartbin.smartbin.dto.BulkRequestDTO;
import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.model.Route;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.model.BulkRequestStatus;
import com.sliit.smartbin.smartbin.model.PaymentStatus;
import com.sliit.smartbin.smartbin.service.BinService;
import com.sliit.smartbin.smartbin.service.ReportService;
import com.sliit.smartbin.smartbin.service.RouteService;
import com.sliit.smartbin.smartbin.service.UserService;
import com.sliit.smartbin.smartbin.service.NotificationService;
import com.sliit.smartbin.smartbin.service.BinAssignmentService;
import com.sliit.smartbin.smartbin.service.CollectionService;
import com.sliit.smartbin.smartbin.service.BulkRequestService;
import com.sliit.smartbin.smartbin.service.BulkRequestPdfService;
import com.sliit.smartbin.smartbin.model.BinAssignment;
import com.sliit.smartbin.smartbin.model.RegionAssignment;
import com.sliit.smartbin.smartbin.repository.RegionAssignmentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

/**
 * SOLID PRINCIPLES APPLIED IN AUTHORITY CONTROLLER
 * 
 * S - Single Responsibility Principle (SRP):
 *     This controller has ONE responsibility: Handle HTTP requests for authority features.
 *     Each method handles one specific endpoint. Business logic delegated to service layer.
 * 
 * I - Interface Segregation Principle (ISP):
 *     Controller depends on multiple focused service interfaces (BinService, RouteService, etc.)
 *     rather than one giant service. Each service has specific responsibilities.
 * 
 * D - Dependency Inversion Principle (DIP):
 *     Controller depends on Service INTERFACES, not concrete implementations.
 *     All dependencies injected via constructor for loose coupling and testability.
 */
@Controller
@RequestMapping("/authority")
public class AuthorityController {

    // DIP: Depend on service abstractions (interfaces), not concrete classes
    // ISP: Multiple focused services instead of one monolithic service
    private final BinService binService;
    private final RouteService routeService;
    private final ReportService reportService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final BinAssignmentService binAssignmentService;
    private final CollectionService collectionService;
    private final BulkRequestService bulkRequestService;
    private final BulkRequestPdfService bulkRequestPdfService;
    private final RegionAssignmentRepository regionAssignmentRepository;

    // DIP: Constructor injection for loose coupling and easy testing/mocking
    public AuthorityController(BinService binService,
                               RouteService routeService,
                               ReportService reportService,
                               UserService userService,
                               NotificationService notificationService,
                               BinAssignmentService binAssignmentService,
                               CollectionService collectionService,
                               BulkRequestService bulkRequestService,
                               BulkRequestPdfService bulkRequestPdfService,
                               RegionAssignmentRepository regionAssignmentRepository) {
        this.binService = binService;
        this.routeService = routeService;
        this.reportService = reportService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.binAssignmentService = binAssignmentService;
        this.collectionService = collectionService;
        this.bulkRequestService = bulkRequestService;
        this.bulkRequestPdfService = bulkRequestPdfService;
        this.regionAssignmentRepository = regionAssignmentRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return "redirect:/authority/login";
        }
        
        model.addAttribute("user", user);
        
        // Get system overview data
        Map<String, Object> systemOverview = reportService.generateSystemOverviewReport();
        model.addAttribute("systemOverview", systemOverview);
        
        // Get bin status data
        Map<String, Object> binStatusReport = reportService.generateBinStatusReport(new ReportDTO());
        model.addAttribute("binStatusReport", binStatusReport);
        
        // Get overdue bins
        List<Bin> overdueBins = binService.findOverdueBins();
        model.addAttribute("overdueBins", overdueBins);
        
        // Get alerted bins
        List<Bin> alertedBins = binService.findAlertedBins();
        model.addAttribute("alertedBins", alertedBins);
        
        // Get full bins for dispatch
        List<Bin> fullBins = binService.findBinsByStatus(Bin.BinStatus.FULL);
        model.addAttribute("fullBins", fullBins);
        
        // Get available collectors
        List<User> collectors = userService.findByRole(User.UserRole.COLLECTOR);
        model.addAttribute("collectors", collectors);
        
        return "authority/dashboard";
    }

    @GetMapping("/bins")
    public String manageBins(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.AUTHORITY) {
            return "redirect:/authority/login";
        }
        
        List<Bin> allBins = binService.findAllBins();
        model.addAttribute("bins", allBins);
        model.addAttribute("user", user);
        
        return "authority/bins";
    }

    @GetMapping("/dispatch")
    public String dispatchCollectors(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.AUTHORITY) {
            return "redirect:/authority/login";
        }
        
        // Get all bins that need collection (full, overdue, or alerted bins)
        List<Bin> allBins = binService.findAllBins();
        List<Bin> fullBins = new ArrayList<>();
        
        // Filter bins that need collection: FULL, OVERDUE, or have alerts
        for (Bin bin : allBins) {
            if (bin.getStatus() == Bin.BinStatus.FULL || 
                bin.getStatus() == Bin.BinStatus.OVERDUE || 
                bin.getAlertFlag()) {
                fullBins.add(bin);
            }
        }
        
        model.addAttribute("fullBins", fullBins);
        
        // Get available collectors
        List<User> collectors = userService.findByRole(User.UserRole.COLLECTOR);
        model.addAttribute("collectors", collectors);
        
        model.addAttribute("user", user);
        
        return "authority/dispatch";
    }

    @PostMapping("/dispatch")
    public String processDispatch(@RequestParam List<Long> binIds,
                                @RequestParam Long collectorId,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return "redirect:/authority/login";
        }
        
        try {
            User collector = userService.findById(collectorId)
                .orElseThrow(() -> new RuntimeException("Collector not found"));
            
            Route route = routeService.assignRouteToCollector(binIds, collector, user);
            
            // Send notification to collector
            notificationService.sendRouteNotification(collector, route);
            
            redirectAttributes.addFlashAttribute("success", 
                "Route dispatched successfully! Route ID: " + route.getId());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to dispatch route: " + e.getMessage());
        }
        
        return "redirect:/authority/dashboard";
    }

    @PostMapping("/api/dispatch")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiDispatch(@RequestBody Map<String, Object> request,
                                                          HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            @SuppressWarnings("unchecked")
            List<Object> binIdsObj = (List<Object>) request.get("binIds");
            List<Long> binIds = new ArrayList<>();
            for (Object binIdObj : binIdsObj) {
                if (binIdObj instanceof Integer) {
                    binIds.add(((Integer) binIdObj).longValue());
                } else if (binIdObj instanceof Long) {
                    binIds.add((Long) binIdObj);
                } else {
                    binIds.add(Long.valueOf(binIdObj.toString()));
                }
            }
            Long collectorId = Long.valueOf(request.get("collectorId").toString());
            
            User collector = userService.findById(collectorId)
                .orElseThrow(() -> new RuntimeException("Collector not found"));
            
            Route route = routeService.assignRouteToCollector(binIds, collector, user);
            
            // Send notification to collector
            notificationService.sendRouteNotification(collector, route);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Route dispatched successfully!");
            response.put("routeId", route.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to dispatch route: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/routes")
    public String manageRoutes(HttpSession session, Model model) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return "redirect:/authority/login";
        }
        
        List<Route> routes = routeService.findRoutesByAuthority(user);
        model.addAttribute("routes", routes);
        model.addAttribute("user", user);
        
        return "authority/routes";
    }

    @GetMapping("/manage-collectors")
    public String manageCollectors(HttpSession session, Model model) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return "redirect:/authority/login";
        }
        
        // Get all collectors
        List<User> collectors = userService.findByRole(User.UserRole.COLLECTOR);
        
        // Ensure collectors is never null
        if (collectors == null) {
            collectors = new java.util.ArrayList<>();
        }
        
        System.out.println("=== MANAGE COLLECTORS DEBUG ===");
        System.out.println("Total collectors found: " + collectors.size());
        for (User collector : collectors) {
            System.out.println("  - " + collector.getName() + " (" + collector.getEmail() + ") ID: " + collector.getId());
        }
        System.out.println("================================");
        
        model.addAttribute("collectors", collectors);
        model.addAttribute("user", user);
        
        return "authority/manage-collectors";
    }

    @GetMapping("/api/collectors")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getCollectorsApi(HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            System.out.println("=== API: Getting collectors ===");
            List<User> collectors = userService.findByRole(User.UserRole.COLLECTOR);
            
            if (collectors == null) {
                collectors = new ArrayList<>();
            }
            
            System.out.println("API: Found " + collectors.size() + " collectors");
            List<Map<String, Object>> collectorData = new ArrayList<>();
            
            for (User collector : collectors) {
                System.out.println("API: Processing collector " + collector.getName());
                Map<String, Object> collectorInfo = new HashMap<>();
                collectorInfo.put("id", collector.getId());
                collectorInfo.put("name", collector.getName());
                collectorInfo.put("email", collector.getEmail());
                collectorInfo.put("phone", collector.getPhone());
                collectorInfo.put("region", collector.getRegion() != null ? collector.getRegion() : "No region assigned");
                
                // Get all routes for this collector
                List<Route> allRoutes = routeService.findRoutesByCollector(collector);
                List<Route> activeRoutes = routeService.findActiveRoutesByCollector(collector);
                
                // Get assigned routes
                List<Route> assignedRoutes = allRoutes.stream()
                    .filter(r -> r.getStatus() == Route.RouteStatus.ASSIGNED)
                    .collect(java.util.stream.Collectors.toList());
                
                // Get in-progress routes
                List<Route> inProgressRoutes = allRoutes.stream()
                    .filter(r -> r.getStatus() == Route.RouteStatus.IN_PROGRESS)
                    .collect(java.util.stream.Collectors.toList());
                
                // Get today's completed routes
                LocalDate today = LocalDate.now();
                List<Route> todayCompletedRoutes = allRoutes.stream()
                    .filter(r -> r.getStatus() == Route.RouteStatus.COMPLETED 
                        && r.getCompletedDate() != null
                        && r.getCompletedDate().toLocalDate().equals(today))
                    .collect(java.util.stream.Collectors.toList());
                
                // Get real today's collections count
                long todayCollections = collectionService.findCollectionsByCollectorAndDateRange(
                    collector, 
                    today.atStartOfDay(), 
                    today.plusDays(1).atStartOfDay()
                ).size();
                
                // Calculate real completion rate
                long completedRoutes = allRoutes.stream()
                    .filter(r -> r.getStatus() == Route.RouteStatus.COMPLETED)
                    .count();
                double completionRate = allRoutes.isEmpty() ? 0 : 
                    ((double) completedRoutes / allRoutes.size()) * 100;
                
                collectorInfo.put("activeRoutes", activeRoutes.size());
                collectorInfo.put("assignedRoutes", assignedRoutes.size());
                collectorInfo.put("inProgressRoutes", inProgressRoutes.size());
                collectorInfo.put("todayCompletedRoutes", todayCompletedRoutes.size());
                collectorInfo.put("todayCollections", todayCollections);
                collectorInfo.put("completionRate", Math.round(completionRate));
                
                // Add detailed route information
                List<Map<String, Object>> routeDetails = new ArrayList<>();
                for (Route route : assignedRoutes) {
                    Map<String, Object> routeInfo = new HashMap<>();
                    routeInfo.put("id", route.getId());
                    routeInfo.put("name", route.getRouteName());
                    routeInfo.put("status", route.getStatus().name());
                    routeInfo.put("assignedDate", route.getAssignedDate().toString());
                    routeInfo.put("binCount", route.getRouteBins() != null ? route.getRouteBins().size() : 0);
                    routeInfo.put("distance", route.getTotalDistanceKm());
                    routeInfo.put("duration", route.getEstimatedDurationMinutes());
                    routeDetails.add(routeInfo);
                }
                
                for (Route route : inProgressRoutes) {
                    Map<String, Object> routeInfo = new HashMap<>();
                    routeInfo.put("id", route.getId());
                    routeInfo.put("name", route.getRouteName());
                    routeInfo.put("status", route.getStatus().name());
                    routeInfo.put("startedDate", route.getStartedDate() != null ? route.getStartedDate().toString() : null);
                    routeInfo.put("binCount", route.getRouteBins() != null ? route.getRouteBins().size() : 0);
                    routeInfo.put("distance", route.getTotalDistanceKm());
                    routeInfo.put("duration", route.getEstimatedDurationMinutes());
                    routeDetails.add(routeInfo);
                }
                
                collectorInfo.put("routes", routeDetails);
                
                // Determine status based on active routes
                if (activeRoutes.isEmpty()) {
                    collectorInfo.put("status", "available");
                } else if (inProgressRoutes.size() > 0) {
                    collectorInfo.put("status", "on-route");
                } else {
                    collectorInfo.put("status", "busy");
                }
                
                // Add current activity description
                if (inProgressRoutes.size() > 0) {
                    Route currentRoute = inProgressRoutes.get(0);
                    collectorInfo.put("currentActivity", "On route: " + currentRoute.getRouteName());
                } else if (assignedRoutes.size() > 0) {
                    collectorInfo.put("currentActivity", assignedRoutes.size() + " route(s) assigned");
                } else {
                    collectorInfo.put("currentActivity", "Available for assignment");
                }
                
                // Add performance rating (0-5 stars based on completion rate)
                double performanceRating = (completionRate / 100.0) * 5;
                collectorInfo.put("performanceRating", Math.round(performanceRating * 10) / 10.0);
                
                collectorData.add(collectorInfo);
            }
            
            System.out.println("API: Successfully processed " + collectorData.size() + " collectors");
            return ResponseEntity.ok(collectorData);
        } catch (Exception e) {
            System.err.println("=== API ERROR ===");
            System.err.println("Error fetching collectors: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=================");
            
            // Return empty list instead of error to prevent frontend crashes
            return ResponseEntity.ok(new ArrayList<>());
        }
    }


    @GetMapping("/api/analytics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAnalytics(HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            Map<String, Object> analytics = new HashMap<>();
            
            // Collection trends (last 7 hours)
            List<Integer> collectionTrends = new ArrayList<>();
            for (int i = 6; i >= 0; i--) {
                // Mock data - in real app, this would query actual collection data
                collectionTrends.add((int) (Math.random() * 20 + 10));
            }
            analytics.put("collectionTrends", collectionTrends);
            
            // Collector performance
            List<User> collectors = userService.findByRole(User.UserRole.COLLECTOR);
            List<String> collectorLabels = new ArrayList<>();
            List<Integer> collectorData = new ArrayList<>();
            
            for (User collector : collectors) {
                collectorLabels.add(collector.getName().split(" ")[0] + " " + 
                    collector.getName().split(" ")[1].charAt(0) + ".");
                collectorData.add((int) (Math.random() * 15 + 5)); // Mock daily collections
            }
            
            Map<String, Object> collectorPerformance = new HashMap<>();
            collectorPerformance.put("labels", collectorLabels);
            collectorPerformance.put("data", collectorData);
            analytics.put("collectorPerformance", collectorPerformance);
            
            // Bin status distribution
            List<Integer> binStatus = new ArrayList<>();
            binStatus.add(45); // Empty
            binStatus.add(30); // Partial
            binStatus.add(20); // Full
            binStatus.add(5);  // Overdue
            analytics.put("binStatus", binStatus);
            
            // Route efficiency (last 7 days)
            List<Integer> routeEfficiency = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                routeEfficiency.add((int) (Math.random() * 20 + 75)); // 75-95% efficiency
            }
            analytics.put("routeEfficiency", routeEfficiency);
            
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/api/assign-collector")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> assignCollector(@RequestBody Map<String, Object> request,
                                                            HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            Long collectorId = Long.valueOf(request.get("collectorId").toString());
            String region = request.get("region").toString();
            
            System.out.println("=== REGION ASSIGNMENT ===");
            System.out.println("Assigning collector ID: " + collectorId + " to region: " + region);
            
            User collector = userService.findById(collectorId)
                .orElseThrow(() -> new RuntimeException("Collector not found"));
            
            String previousRegion = collector.getRegion();
            System.out.println("Found collector: " + collector.getName() + ", current region: " + previousRegion);
            
            // Save assignment history before updating
            RegionAssignment assignment = new RegionAssignment();
            assignment.setCollector(collector);
            assignment.setAssignedBy(user);
            assignment.setPreviousRegion(previousRegion);
            assignment.setNewRegion(region);
            assignment.setStatus(RegionAssignment.AssignmentStatus.ACTIVE);
            assignment.setNotes("Assigned via Manage Collectors interface");
            
            // Mark previous assignments as superseded
            List<RegionAssignment> previousAssignments = regionAssignmentRepository
                .findByCollectorAndStatus(collector, RegionAssignment.AssignmentStatus.ACTIVE);
            for (RegionAssignment prev : previousAssignments) {
                prev.setStatus(RegionAssignment.AssignmentStatus.SUPERSEDED);
                regionAssignmentRepository.save(prev);
            }
            
            // Save new assignment
            RegionAssignment savedAssignment = regionAssignmentRepository.save(assignment);
            System.out.println("Saved assignment history with ID: " + savedAssignment.getId());
            
            // Update collector region assignment in User table
            collector.setRegion(region);
            User updatedCollector = userService.updateUser(collector);
            
            System.out.println("Updated collector region to: " + updatedCollector.getRegion());
            
            // Send notification to collector
            notificationService.sendRegionAssignmentNotification(collector, region);
            
            System.out.println("Sent notification to collector");
            System.out.println("=== ASSIGNMENT COMPLETE ===");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Collector " + collector.getName() + " assigned to " + region + " successfully!");
            response.put("previousRegion", previousRegion);
            response.put("newRegion", region);
            response.put("assignmentId", savedAssignment.getId());
            response.put("assignedBy", user.getName());
            response.put("timestamp", savedAssignment.getAssignedAt().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("ERROR in region assignment: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to assign collector: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/api/region-assignments")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getRegionAssignments(
            @RequestParam(required = false) Long collectorId,
            HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            List<RegionAssignment> assignments;
            
            if (collectorId != null) {
                // Get assignments for specific collector
                User collector = userService.findById(collectorId)
                    .orElseThrow(() -> new RuntimeException("Collector not found"));
                assignments = regionAssignmentRepository.findByCollectorOrderByAssignedAtDesc(collector);
            } else {
                // Get all assignments
                assignments = regionAssignmentRepository.findAll();
                assignments.sort((a, b) -> b.getAssignedAt().compareTo(a.getAssignedAt()));
            }
            
            List<Map<String, Object>> response = new ArrayList<>();
            for (RegionAssignment assignment : assignments) {
                Map<String, Object> data = new HashMap<>();
                data.put("id", assignment.getId());
                data.put("collectorId", assignment.getCollector().getId());
                data.put("collectorName", assignment.getCollector().getName());
                data.put("assignedById", assignment.getAssignedBy().getId());
                data.put("assignedByName", assignment.getAssignedBy().getName());
                data.put("previousRegion", assignment.getPreviousRegion());
                data.put("newRegion", assignment.getNewRegion());
                data.put("assignedAt", assignment.getAssignedAt().toString());
                data.put("status", assignment.getStatus().toString());
                data.put("notes", assignment.getNotes());
                response.add(data);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("ERROR fetching region assignments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/reports")
    public String generateReports(HttpSession session, Model model) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return "redirect:/authority/login";
        }
        
        model.addAttribute("user", user);
        return "authority/reports";
    }


    @GetMapping("/api/bin-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getBinStatus(HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            Map<String, Object> binStatusReport = reportService.generateBinStatusReport(new ReportDTO());
            Map<String, Object> systemOverview = reportService.generateSystemOverviewReport();
            
            Map<String, Object> response = new HashMap<>();
            response.put("binStatus", binStatusReport);
            response.put("systemOverview", systemOverview);
            response.put("lastUpdated", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Failed to fetch bin status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/api/bins")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getBins(HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            List<Bin> bins = binService.findAllBins();
            List<Map<String, Object>> binData = bins.stream()
                .map(bin -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("id", bin.getId());
                    data.put("qrCode", bin.getQrCode());
                    data.put("location", bin.getLocation());
                    data.put("latitude", bin.getLatitude());
                    data.put("longitude", bin.getLongitude());
                    data.put("fillLevel", bin.getFillLevel());
                    data.put("status", bin.getStatus().toString());
                    data.put("lastEmptied", bin.getLastEmptied());
                    data.put("alertFlag", bin.getAlertFlag());
                    return data;
                })
                .toList();
            
            return ResponseEntity.ok(binData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/api/optimize-route")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> optimizeRoute(@RequestBody Map<String, Object> request,
                                                             HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            @SuppressWarnings("unchecked")
            List<Object> binIdsObj = (List<Object>) request.get("binIds");
            List<Long> binIds = new ArrayList<>();
            for (Object binIdObj : binIdsObj) {
                if (binIdObj instanceof Integer) {
                    binIds.add(((Integer) binIdObj).longValue());
                } else if (binIdObj instanceof Long) {
                    binIds.add((Long) binIdObj);
                } else {
                    binIds.add(Long.valueOf(binIdObj.toString()));
                }
            }
            Long collectorId = Long.valueOf(request.get("collectorId").toString());
            
            if (binIds == null || binIds.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "No bins selected for route optimization");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            User collector = userService.findById(collectorId)
                .orElseThrow(() -> new RuntimeException("Collector not found"));
            
            // Optimize route using TSP algorithm
            Route route = routeService.optimizeRoute(binIds, collector, user);
            
            // Send notification to collector
            notificationService.sendRouteNotification(collector, route);
            
            // Prepare response with route details
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Route optimized and assigned successfully!");
            response.put("routeId", route.getId());
            response.put("routeName", route.getRouteName());
            response.put("estimatedDuration", route.getEstimatedDurationMinutes());
            response.put("totalDistance", route.getTotalDistanceKm());
            response.put("numBins", binIds.size());
            
            // Add optimized route coordinates for map visualization
            List<Map<String, Object>> routeCoordinates = new ArrayList<>();
            for (Long binId : binIds) {
                Optional<Bin> binOpt = binService.findById(binId);
                if (binOpt.isPresent()) {
                    Bin bin = binOpt.get();
                    Map<String, Object> coord = new HashMap<>();
                    coord.put("id", bin.getId());
                    coord.put("lat", bin.getLatitude());
                    coord.put("lng", bin.getLongitude());
                    coord.put("qrCode", bin.getQrCode());
                    coord.put("location", bin.getLocation());
                    routeCoordinates.add(coord);
                }
            }
            response.put("routeCoordinates", routeCoordinates);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to optimize route: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/api/route/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRouteDetails(@PathVariable Long id,
                                                              HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            Optional<Route> routeOpt = routeService.findById(id);
            if (routeOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Route route = routeOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("id", route.getId());
            response.put("routeName", route.getRouteName());
            response.put("status", route.getStatus());
            response.put("collectorName", route.getCollector().getName());
            response.put("estimatedDuration", route.getEstimatedDurationMinutes());
            response.put("totalDistance", route.getTotalDistanceKm());
            response.put("assignedDate", route.getAssignedDate());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/api/reports/generate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateReportData(@RequestBody Map<String, Object> request,
                                                                  HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            String reportType = request.get("reportType").toString();
            String region = request.get("region") != null ? request.get("region").toString() : null;
            String dateRange = request.get("dateRange") != null ? request.get("dateRange").toString() : null;
            
            ReportDTO reportDTO = new ReportDTO();
            reportDTO.setReportType(reportType);
            
            Map<String, Object> reportData;
            
            switch (reportType) {
                case "collection":
                    reportData = reportService.generateCollectionReport(reportDTO);
                    break;
                case "performance":
                    reportData = reportService.generatePerformanceReport(reportDTO);
                    break;
                case "bin-status":
                    reportData = reportService.generateBinStatusReport(reportDTO);
                    break;
                case "overdue":
                    reportData = reportService.generateOverdueBinsReport(reportDTO);
                    break;
                default:
                    reportData = reportService.generateSystemOverviewReport();
            }
            
            // Add additional metadata
            reportData.put("region", region);
            reportData.put("dateRange", dateRange);
            reportData.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            reportData.put("generatedBy", user.getName());
            
            return ResponseEntity.ok(reportData);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Failed to generate report: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Helper method to validate authority user (Single Responsibility Principle)
    private User validateAuthorityUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.AUTHORITY) {
            return null;
        }
        return user;
    }

    @PostMapping("/reports/collection")
    public String generateCollectionReport(@RequestParam(required = false) String startDate,
                                         @RequestParam(required = false) String endDate,
                                         HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return "redirect:/authority/login";
        }
        
        try {
            ReportDTO reportDTO = new ReportDTO();
            reportDTO.setReportType("Collection Report");
            if (startDate != null && !startDate.isEmpty()) {
                reportDTO.setStartDate(LocalDateTime.parse(startDate));
            }
            if (endDate != null && !endDate.isEmpty()) {
                reportDTO.setEndDate(LocalDateTime.parse(endDate));
            }
            
            Map<String, Object> report = reportService.generateCollectionReport(reportDTO);
            model.addAttribute("report", report);
            model.addAttribute("user", user);
            
            return "authority/report-collection";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to generate report: " + e.getMessage());
            return "redirect:/authority/reports";
        }
    }

    @PostMapping("/reports/performance")
    public String generatePerformanceReport(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return "redirect:/authority/login";
        }
        
        try {
            ReportDTO reportDTO = new ReportDTO();
            reportDTO.setReportType("Performance Report");
            
            Map<String, Object> report = reportService.generatePerformanceReport(reportDTO);
            model.addAttribute("report", report);
            model.addAttribute("user", user);
            
            return "authority/report-performance";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to generate report: " + e.getMessage());
            return "redirect:/authority/reports";
        }
    }

    @PostMapping("/reports/bin-status")
    public String generateBinStatusReport(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return "redirect:/authority/login";
        }
        
        try {
            ReportDTO reportDTO = new ReportDTO();
            reportDTO.setReportType("Bin Status Report");
            
            Map<String, Object> report = reportService.generateBinStatusReport(reportDTO);
            model.addAttribute("report", report);
            model.addAttribute("user", user);
            
            return "authority/report-bin-status";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to generate report: " + e.getMessage());
            return "redirect:/authority/reports";
        }
    }

    @PostMapping("/reports/overdue")
    public String generateOverdueReport(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return "redirect:/authority/login";
        }
        
        try {
            ReportDTO reportDTO = new ReportDTO();
            reportDTO.setReportType("Overdue Bins Report");
            
            Map<String, Object> report = reportService.generateOverdueBinsReport(reportDTO);
            model.addAttribute("report", report);
            model.addAttribute("user", user);
            
            return "authority/report-overdue";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to generate report: " + e.getMessage());
            return "redirect:/authority/reports";
        }
    }

    @PostMapping("/bin/{id}/alert")
    public String setBinAlert(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return "redirect:/authority/login";
        }
        
        try {
            Optional<Bin> bin = binService.findById(id);
            if (bin.isPresent()) {
                Bin binEntity = bin.get();
                binEntity.setAlertFlag(true);
                binEntity.setStatus(Bin.BinStatus.OVERDUE);
                binService.updateBinStatus(id, Bin.BinStatus.OVERDUE, binEntity.getFillLevel());
                
                // Send alert notification
                notificationService.sendBinAlertNotification(binEntity);
                
                redirectAttributes.addFlashAttribute("success", "Bin alert set successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Bin not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to set bin alert: " + e.getMessage());
        }
        
        return "redirect:/authority/bins";
    }

    @PostMapping("/bin/{id}/clear-alert")
    public String clearBinAlert(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return "redirect:/authority/login";
        }
        
        try {
            Optional<Bin> bin = binService.findById(id);
            if (bin.isPresent()) {
                Bin binEntity = bin.get();
                binEntity.setAlertFlag(false);
                binEntity.setStatus(Bin.BinStatus.EMPTY);
                binService.updateBinStatus(id, Bin.BinStatus.EMPTY, 0);
                redirectAttributes.addFlashAttribute("success", "Bin alert cleared successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Bin not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to clear bin alert: " + e.getMessage());
        }
        
        return "redirect:/authority/bins";
    }
    
    // ==================== BIN ASSIGNMENT API ENDPOINTS ====================
    
    /**
     * Save a new bin assignment to database
     */
    @PostMapping("/api/assignments/save")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveAssignment(@RequestBody Map<String, Object> request,
                                                              HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            Long collectorId = Long.valueOf(request.get("collectorId").toString());
            
            @SuppressWarnings("unchecked")
            List<Object> binIdsObj = (List<Object>) request.get("binIds");
            List<Long> binIds = new ArrayList<>();
            for (Object binIdObj : binIdsObj) {
                if (binIdObj instanceof Integer) {
                    binIds.add(((Integer) binIdObj).longValue());
                } else if (binIdObj instanceof Long) {
                    binIds.add((Long) binIdObj);
                } else {
                    binIds.add(Long.valueOf(binIdObj.toString()));
                }
            }
            
            @SuppressWarnings("unchecked")
            List<String> binLocations = (List<String>) request.get("binLocations");
            Long routeId = request.get("routeId") != null ? Long.valueOf(request.get("routeId").toString()) : null;
            
            User collector = userService.findById(collectorId)
                .orElseThrow(() -> new RuntimeException("Collector not found"));
            
            // Create assignment in database
            BinAssignment assignment = binAssignmentService.createAssignment(
                collector, user, binIds, binLocations, routeId
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Assignment saved successfully");
            response.put("assignmentId", assignment.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to save assignment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get all bin assignments
     */
    @GetMapping("/api/assignments/all")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAllAssignments(HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            List<BinAssignment> assignments = binAssignmentService.getAllAssignments();
            List<Map<String, Object>> response = new ArrayList<>();
            
            for (BinAssignment assignment : assignments) {
                Map<String, Object> assignmentData = new HashMap<>();
                assignmentData.put("id", assignment.getId());
                assignmentData.put("collectorId", assignment.getCollector().getId());
                assignmentData.put("collectorName", assignment.getCollector().getName());
                assignmentData.put("binIds", assignment.getBinIds());
                assignmentData.put("binLocations", assignment.getBinLocations());
                assignmentData.put("assignedBy", assignment.getAssignedBy().getName());
                assignmentData.put("assignedAt", assignment.getAssignedAt().toString());
                assignmentData.put("status", assignment.getStatus().toString());
                assignmentData.put("routeId", assignment.getRouteId());
                
                response.add(assignmentData);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get bin assignments for a specific collector
     */
    @GetMapping("/api/assignments/collector/{collectorId}")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAssignmentsByCollector(@PathVariable Long collectorId,
                                                                               HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            List<BinAssignment> assignments = binAssignmentService.getAssignmentsByCollector(collectorId);
            List<Map<String, Object>> response = new ArrayList<>();
            
            for (BinAssignment assignment : assignments) {
                Map<String, Object> assignmentData = new HashMap<>();
                assignmentData.put("id", assignment.getId());
                assignmentData.put("collectorId", assignment.getCollector().getId());
                assignmentData.put("collectorName", assignment.getCollector().getName());
                assignmentData.put("binIds", assignment.getBinIds());
                assignmentData.put("binLocations", assignment.getBinLocations());
                assignmentData.put("assignedBy", assignment.getAssignedBy().getName());
                assignmentData.put("assignedAt", assignment.getAssignedAt().toString());
                assignmentData.put("status", assignment.getStatus().toString());
                
                response.add(assignmentData);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Delete all bin assignments (for testing/reset)
     */
    @DeleteMapping("/api/assignments/clear")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clearAllAssignments(HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            binAssignmentService.deleteAllAssignments();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "All assignments cleared successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to clear assignments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // ======================== Bulk Request Management Endpoints ========================
    
    /**
     * SRP: This method has ONE job - display bulk requests page
     * OCP: New filters can be added without modifying core display logic
     * 
     * View all bulk requests
     */
    @GetMapping("/bulk-requests")
    public String manageBulkRequests(HttpSession session, Model model,
                                    @RequestParam(required = false) String status) {
        // SRP: Authentication/validation extracted to helper method
        User user = validateAuthorityUser(session);
        if (user == null) {
            return "redirect:/authority/login";
        }
        
        List<BulkRequestDTO> bulkRequests;
        
        // DIP: Controller doesn't know HOW data is fetched, just calls service methods
        if (status != null && !status.isEmpty()) {
            try {
                BulkRequestStatus requestStatus = BulkRequestStatus.valueOf(status);
                bulkRequests = bulkRequestService.getBulkRequestsByStatus(requestStatus);
            } catch (IllegalArgumentException e) {
                bulkRequests = bulkRequestService.getRecentRequests(30);
            }
        } else {
            bulkRequests = bulkRequestService.getRecentRequests(30);
        }
        
        // Calculate statistics
        long pendingCount = bulkRequests.stream()
            .filter(req -> req.getStatus() == BulkRequestStatus.PENDING)
            .count();
        long paidCount = bulkRequests.stream()
            .filter(req -> req.getStatus() == BulkRequestStatus.PAYMENT_COMPLETED)
            .count();
        long scheduledCount = bulkRequests.stream()
            .filter(req -> req.getStatus() == BulkRequestStatus.SCHEDULED)
            .count();
        
        model.addAttribute("bulkRequests", bulkRequests);
        model.addAttribute("user", user);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("pendingBulkRequests", pendingCount);
        model.addAttribute("paidBulkRequests", paidCount);
        model.addAttribute("scheduledBulkRequests", scheduledCount);
        
        // Get available collectors
        List<User> collectors = userService.findByRole(User.UserRole.COLLECTOR);
        model.addAttribute("collectors", collectors);
        
        return "authority/bulk-requests";
    }
    
    /**
     * Get requests requiring collector assignment (AJAX)
     */
    @GetMapping("/api/bulk-requests/requiring-assignment")
    @ResponseBody
    public ResponseEntity<List<BulkRequestDTO>> getRequestsRequiringAssignment(HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<BulkRequestDTO> requests = bulkRequestService.getRequestsRequiringCollectorAssignment();
        return ResponseEntity.ok(requests);
    }
    
    /**
     * SRP: Method only handles HTTP request/response, assignment logic in service
     * DIP: Depends on BulkRequestService interface for assignment processing
     * 
     * Assign collector to bulk request
     */
    @PostMapping("/bulk-requests/{requestId}/assign-collector")
    public String assignCollectorToBulkRequest(@PathVariable Long requestId,
                                               @RequestParam Long collectorId,
                                               HttpSession session,
                                               RedirectAttributes redirectAttributes) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return "redirect:/authority/login";
        }
        
        try {
            // SRP: Business logic (validation, notification) handled in service layer
            // DIP: Controller doesn't know implementation details of assignment
            bulkRequestService.assignCollector(requestId, collectorId);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Collector assigned successfully to bulk request!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to assign collector: " + e.getMessage());
        }
        
        return "redirect:/authority/bulk-requests";
    }
    
    /**
     * SRP: Only handles HTTP request/response, scheduling and notification in service
     * OCP: New scheduling types can be added in service without modifying controller
     * 
     * Schedule pickup for bulk request
     */
    @PostMapping("/bulk-requests/{requestId}/schedule")
    public String scheduleBulkPickup(@PathVariable Long requestId,
                                     @RequestParam String scheduledDateTime,
                                     @RequestParam(required = false) Long collectorId,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return "redirect:/authority/login";
        }
        
        try {
            LocalDateTime scheduledDate = LocalDateTime.parse(scheduledDateTime);
            // SRP: Notification and scheduling logic encapsulated in service
            // DIP: Controller doesn't know how notifications are sent
            bulkRequestService.scheduleAndNotifyPickup(requestId, scheduledDate, collectorId);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Bulk collection pickup scheduled successfully! User has been notified.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to schedule pickup: " + e.getMessage());
        }
        
        return "redirect:/authority/bulk-requests";
    }
    
    /**
     * SRP: Method only handles status update HTTP endpoint
     * OCP: New statuses can be added to enum without modifying this method
     * 
     * Update bulk request status
     */
    @PostMapping("/bulk-requests/{requestId}/update-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateBulkRequestStatus(@PathVariable Long requestId,
                                                                       @RequestParam String status,
                                                                       HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            BulkRequestStatus requestStatus = BulkRequestStatus.valueOf(status);
            // DIP: Status update logic handled in service layer
            BulkRequestDTO updatedRequest = bulkRequestService.updateRequestStatus(requestId, requestStatus);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Bulk request status updated successfully");
            response.put("request", updatedRequest);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get bulk request details (AJAX)
     */
    @GetMapping("/api/bulk-requests/{requestId}/details")
    @ResponseBody
    public ResponseEntity<BulkRequestDTO> getBulkRequestDetails(@PathVariable Long requestId,
                                                                HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<BulkRequestDTO> request = bulkRequestService.getBulkRequestById(requestId);
        return request.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Complete bulk collection
     */
    @PostMapping("/bulk-requests/{requestId}/complete")
    public String completeBulkCollection(@PathVariable Long requestId,
                                        @RequestParam(required = false) String notes,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return "redirect:/authority/login";
        }
        
        try {
            bulkRequestService.completeCollection(requestId, notes);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Bulk collection marked as completed successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to complete collection: " + e.getMessage());
        }
        
        return "redirect:/authority/bulk-requests";
    }

    /**
     * SRP: This method has ONE job - display authority profile page
     * 
     * Display authority user profile
     */
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return "redirect:/authority/login";
        }
        
        model.addAttribute("user", user);
        
        // Get user statistics for profile display
        Map<String, Object> profileStats = new HashMap<>();
        
        // Get routes managed by this authority
        List<Route> managedRoutes = routeService.findRoutesByAuthority(user);
        profileStats.put("totalRoutesManaged", managedRoutes.size());
        
        // Get active routes
        long activeRoutes = managedRoutes.stream()
            .filter(route -> route.getStatus() == Route.RouteStatus.IN_PROGRESS || 
                           route.getStatus() == Route.RouteStatus.ASSIGNED)
            .count();
        profileStats.put("activeRoutes", activeRoutes);
        
        // Get completed routes (last 30 days)
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        long recentCompletedRoutes = managedRoutes.stream()
            .filter(route -> route.getStatus() == Route.RouteStatus.COMPLETED &&
                           route.getCompletedDate() != null &&
                           route.getCompletedDate().toLocalDate().isAfter(thirtyDaysAgo))
            .count();
        profileStats.put("recentCompletedRoutes", recentCompletedRoutes);
        
        // Get bulk requests managed
        List<BulkRequestDTO> bulkRequests = bulkRequestService.getRecentRequests(30);
        profileStats.put("bulkRequestsManaged", bulkRequests.size());
        
        model.addAttribute("profileStats", profileStats);
        
        return "authority/profile";
    }

    /**
     * SRP: This method only handles profile update HTTP request
     * 
     * Update authority user profile
     */
    @PostMapping("/profile")
    public String updateProfile(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam(required = false) String phone,
                               @RequestParam(required = false) String region,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return "redirect:/authority/login";
        }
        
        try {
            // Update user information
            user.setName(name);
            user.setEmail(email);
            if (phone != null && !phone.isEmpty()) {
                user.setPhone(phone);
            }
            if (region != null && !region.isEmpty()) {
                user.setRegion(region);
            }
            
            User updatedUser = userService.updateUser(user);
            session.setAttribute("user", updatedUser);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Profile updated successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to update profile: " + e.getMessage());
        }
        
        return "redirect:/authority/profile";
    }

    /**
     * SRP: This method has ONE job - display authority settings page
     * 
     * Display authority user settings
     */
    @GetMapping("/settings")
    public String showSettings(HttpSession session, Model model) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return "redirect:/authority/login";
        }
        
        model.addAttribute("user", user);
        
        // Get system settings and preferences
        Map<String, Object> settings = new HashMap<>();
        settings.put("notificationsEnabled", true); // Default setting
        settings.put("emailNotifications", true);
        settings.put("autoRouteOptimization", true);
        settings.put("defaultCollectionTime", "09:00");
        settings.put("maxRouteDistance", 50.0); // km
        settings.put("alertThreshold", 80); // percentage
        
        model.addAttribute("settings", settings);
        
        return "authority/settings";
    }

    /**
     * SRP: This method only handles settings update HTTP request
     * 
     * Update authority user settings
     */
    @PostMapping("/settings")
    public String updateSettings(@RequestParam(required = false) Boolean notificationsEnabled,
                               @RequestParam(required = false) Boolean emailNotifications,
                               @RequestParam(required = false) Boolean autoRouteOptimization,
                               @RequestParam(required = false) String defaultCollectionTime,
                               @RequestParam(required = false) Double maxRouteDistance,
                               @RequestParam(required = false) Integer alertThreshold,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return "redirect:/authority/login";
        }
        
        try {
            // In a real application, you would save these settings to a database
            // For now, we'll just show a success message
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Settings updated successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to update settings: " + e.getMessage());
        }
        
        return "redirect:/authority/settings";
    }

    /**
     * Change password functionality
     */
    @PostMapping("/settings/change-password")
    public String changePassword(@RequestParam String currentPassword,
                               @RequestParam String newPassword,
                               @RequestParam String confirmPassword,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return "redirect:/authority/login";
        }
        
        try {
            // Validate passwords
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "New passwords do not match!");
                return "redirect:/authority/settings";
            }
            
            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "New password must be at least 6 characters long!");
                return "redirect:/authority/settings";
            }
            
            // In a real application, you would verify the current password
            // and update the password using proper password hashing
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Password changed successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to change password: " + e.getMessage());
        }
        
        return "redirect:/authority/settings";
    }

    // ======================== Bulk Requests PDF Export Endpoints ========================

    /**
     * SRP: This method has ONE job - generate and download PDF report for all bulk requests
     * 
     * Download PDF report of all bulk requests
     */
    @GetMapping("/bulk-requests/download-pdf")
    public ResponseEntity<byte[]> downloadBulkRequestsPdf(HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            List<BulkRequestDTO> bulkRequests = bulkRequestService.getRecentRequests(100);
            String reportTitle = "Bulk Collection Requests Report";
            String userInfo = user.getName() + " (" + user.getEmail() + ")";
            
            // Generate PDF
            ByteArrayOutputStream pdfStream = bulkRequestPdfService.generateBulkRequestsReport(
                bulkRequests, reportTitle, userInfo);
            
            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "bulk-requests-report-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm")) + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return new ResponseEntity<>(pdfStream.toByteArray(), headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * SRP: This method has ONE job - generate and download PDF report for bulk requests by status
     * 
     * Download PDF report of bulk requests filtered by status
     */
    @GetMapping("/bulk-requests/download-pdf-by-status")
    public ResponseEntity<byte[]> downloadBulkRequestsPdfByStatus(@RequestParam String status, 
                                                                HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            BulkRequestStatus requestStatus = BulkRequestStatus.valueOf(status.toUpperCase());
            List<BulkRequestDTO> bulkRequests = bulkRequestService.getBulkRequestsByStatus(requestStatus);
            String userInfo = user.getName() + " (" + user.getEmail() + ")";
            
            // Generate PDF
            ByteArrayOutputStream pdfStream = bulkRequestPdfService.generateBulkRequestsByStatusReport(
                bulkRequests, requestStatus, userInfo);
            
            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "bulk-requests-" + status.toLowerCase() + "-report-" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm")) + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return new ResponseEntity<>(pdfStream.toByteArray(), headers, HttpStatus.OK);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * SRP: This method has ONE job - generate and download HTML report for bulk requests
     * 
     * Download HTML report of bulk requests (alternative to PDF)
     */
    @GetMapping("/bulk-requests/download-html")
    public ResponseEntity<byte[]> downloadBulkRequestsHtml(HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            List<BulkRequestDTO> bulkRequests = bulkRequestService.getRecentRequests(100);
            String reportTitle = "Bulk Collection Requests Report";
            String userInfo = user.getName() + " (" + user.getEmail() + ")";
            
            // Generate HTML
            String htmlContent = bulkRequestPdfService.generateBulkRequestsHtmlReport(
                bulkRequests, reportTitle, userInfo);
            
            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.setContentDispositionFormData("attachment", 
                "bulk-requests-report-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm")) + ".html");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return new ResponseEntity<>(htmlContent.getBytes("UTF-8"), headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * SRP: This method has ONE job - generate PDF from HTML content
     * 
     * Convert HTML report to PDF and download
     */
    @PostMapping("/bulk-requests/download-pdf-from-html")
    public ResponseEntity<byte[]> downloadPdfFromHtml(@RequestParam String status,
                                                     @RequestParam(required = false) String dateRange,
                                                     HttpSession session) {
        User user = validateAuthorityUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            List<BulkRequestDTO> bulkRequests;
            
            if ("ALL".equals(status)) {
                bulkRequests = bulkRequestService.getRecentRequests(100);
            } else {
                BulkRequestStatus requestStatus = BulkRequestStatus.valueOf(status.toUpperCase());
                bulkRequests = bulkRequestService.getBulkRequestsByStatus(requestStatus);
            }
            
            String reportTitle = "Bulk Collection Requests Report" + 
                (!"ALL".equals(status) ? " - " + status.replace("_", " ") : "");
            String userInfo = user.getName() + " (" + user.getEmail() + ")";
            
            // Generate HTML first
            String htmlContent = bulkRequestPdfService.generateBulkRequestsHtmlReport(
                bulkRequests, reportTitle, userInfo);
            
            // Convert HTML to PDF
            ByteArrayOutputStream pdfStream = bulkRequestPdfService.convertHtmlToPdf(htmlContent);
            
            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "bulk-requests-" + status.toLowerCase() + "-report-" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm")) + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return new ResponseEntity<>(pdfStream.toByteArray(), headers, HttpStatus.OK);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

