package com.sliit.smartbin.smartbin.controller;

import com.sliit.smartbin.smartbin.dto.ReportDTO;
import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.model.Route;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.service.BinService;
import com.sliit.smartbin.smartbin.service.ReportService;
import com.sliit.smartbin.smartbin.service.RouteService;
import com.sliit.smartbin.smartbin.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/authority")
public class AuthorityController {

    private final BinService binService;
    private final RouteService routeService;
    private final ReportService reportService;
    private final UserRepository userRepository;

    public AuthorityController(BinService binService,
                               RouteService routeService,
                               ReportService reportService,
                               UserRepository userRepository) {
        this.binService = binService;
        this.routeService = routeService;
        this.reportService = reportService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.AUTHORITY) {
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
        
        // Get full bins that need collection
        List<Bin> fullBins = binService.findBinsByStatus(Bin.BinStatus.FULL);
        model.addAttribute("fullBins", fullBins);
        
        // Get overdue bins
        List<Bin> overdueBins = binService.findOverdueBins();
        model.addAttribute("overdueBins", overdueBins);
        
        // Get available collectors
        List<User> collectors = userRepository.findAll().stream()
            .filter(u -> u.getRole() == User.UserRole.COLLECTOR)
            .toList();
        model.addAttribute("collectors", collectors);
        
        model.addAttribute("user", user);
        
        return "authority/dispatch";
    }

    @PostMapping("/dispatch")
    public String processDispatch(@RequestParam List<Long> binIds,
                                @RequestParam Long collectorId,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.AUTHORITY) {
            return "redirect:/authority/login";
        }
        
        try {
            User collector = userRepository.findById(collectorId)
                .orElseThrow(() -> new RuntimeException("Collector not found"));
            
            Route route = routeService.assignRouteToCollector(binIds, collector, user);
            
            redirectAttributes.addFlashAttribute("success", 
                "Route dispatched successfully! Route ID: " + route.getId());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to dispatch route: " + e.getMessage());
        }
        
        return "redirect:/authority/dispatch";
    }

    @GetMapping("/routes")
    public String manageRoutes(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.AUTHORITY) {
            return "redirect:/authority/login";
        }
        
        List<Route> routes = routeService.findRoutesByAuthority(user);
        model.addAttribute("routes", routes);
        model.addAttribute("user", user);
        
        return "authority/routes";
    }

    @GetMapping("/collectors")
    public String manageCollectors(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.AUTHORITY) {
            return "redirect:/authority/login";
        }
        
        List<User> collectors = userRepository.findAll().stream()
            .filter(u -> u.getRole() == User.UserRole.COLLECTOR)
            .toList();
        
        model.addAttribute("collectors", collectors);
        model.addAttribute("user", user);
        
        return "authority/collectors";
    }

    @GetMapping("/reports")
    public String generateReports(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.AUTHORITY) {
            return "redirect:/authority/login";
        }
        
        model.addAttribute("user", user);
        return "authority/reports";
    }

    @PostMapping("/reports/collection")
    public String generateCollectionReport(@RequestParam(required = false) String startDate,
                                         @RequestParam(required = false) String endDate,
                                         HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.AUTHORITY) {
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
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.AUTHORITY) {
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
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.AUTHORITY) {
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
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.AUTHORITY) {
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
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.AUTHORITY) {
            return "redirect:/authority/login";
        }
        
        try {
            Optional<Bin> bin = binService.findById(id);
            if (bin.isPresent()) {
                Bin binEntity = bin.get();
                binEntity.setAlertFlag(true);
                binEntity.setStatus(Bin.BinStatus.OVERDUE);
                binService.updateBinStatus(id, Bin.BinStatus.OVERDUE, binEntity.getFillLevel());
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
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.AUTHORITY) {
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

