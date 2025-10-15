package com.sliit.smartbin.smartbin.controller;

import com.sliit.smartbin.smartbin.dto.ReportDTO;
import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.model.Route;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.service.BinService;
import com.sliit.smartbin.smartbin.service.ReportService;
import com.sliit.smartbin.smartbin.service.RouteService;
import com.sliit.smartbin.smartbin.service.UserService;
import com.sliit.smartbin.smartbin.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@Controller
@RequestMapping("/authority")
public class AuthorityController {

    private final BinService binService;
    private final RouteService routeService;
    private final ReportService reportService;
    private final UserService userService;
    private final NotificationService notificationService;

    public AuthorityController(BinService binService,
                               RouteService routeService,
                               ReportService reportService,
                               UserService userService,
                               NotificationService notificationService) {
        this.binService = binService;
        this.routeService = routeService;
        this.reportService = reportService;
        this.userService = userService;
        this.notificationService = notificationService;
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
        model.addAttribute("collectors", collectors);
        model.addAttribute("user", user);
        
        return "authority/manage-collectors";
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
            
            System.out.println("Assigning collector ID: " + collectorId + " to region: " + region);
            
            User collector = userService.findById(collectorId)
                .orElseThrow(() -> new RuntimeException("Collector not found"));
            
            System.out.println("Found collector: " + collector.getName() + ", current region: " + collector.getRegion());
            
            // Update collector region assignment
            collector.setRegion(region);
            User updatedCollector = userService.updateUser(collector);
            
            System.out.println("Updated collector region to: " + updatedCollector.getRegion());
            
            // Send notification to collector
            notificationService.sendRegionAssignmentNotification(collector, region);
            
            System.out.println("Sent notification to collector");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Collector " + collector.getName() + " assigned to " + region + " successfully!");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to assign collector: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
}

