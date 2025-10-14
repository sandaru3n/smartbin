package com.sliit.smartbin.smartbin.controller;

import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.service.BinService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/resident")
public class ResidentController {

    private final BinService binService;

    public ResidentController(BinService binService) {
        this.binService = binService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.RESIDENT) {
            return "redirect:/resident/login";
        }
        
        model.addAttribute("user", user);
        
        // Get nearby bins for the resident
        List<Bin> nearbyBins = binService.findNearbyBins(6.9271, 79.8612, 5.0); // Colombo coordinates
        model.addAttribute("nearbyBins", nearbyBins);
        
        // Get bins with alerts
        List<Bin> alertedBins = binService.findAlertedBins();
        model.addAttribute("alertedBins", alertedBins);
        
        return "resident/dashboard";
    }

    @GetMapping("/bins")
    public String viewBins(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.RESIDENT) {
            return "redirect:/resident/login";
        }
        
        List<Bin> allBins = binService.findAllBins();
        model.addAttribute("bins", allBins);
        model.addAttribute("user", user);
        
        return "resident/bins";
    }

    @GetMapping("/bin/{id}")
    public String viewBinDetails(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.RESIDENT) {
            return "redirect:/resident/login";
        }
        
        Optional<Bin> bin = binService.findById(id);
        if (bin.isPresent()) {
            model.addAttribute("bin", bin.get());
            model.addAttribute("user", user);
            return "resident/bin-details";
        } else {
            return "redirect:/resident/bins";
        }
    }

    @PostMapping("/bin/{id}/update-status")
    public String updateBinStatus(@PathVariable Long id, 
                                 @RequestParam Integer fillLevel,
                                 HttpSession session, 
                                 RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.RESIDENT) {
            return "redirect:/resident/login";
        }
        
        try {
            binService.updateBinFillLevel(id, fillLevel);
            redirectAttributes.addFlashAttribute("success", "Bin status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update bin status: " + e.getMessage());
        }
        
        return "redirect:/resident/bin/" + id;
    }

    @GetMapping("/search-bins")
    public String searchBins(@RequestParam(required = false) Double latitude,
                           @RequestParam(required = false) Double longitude,
                           @RequestParam(defaultValue = "5.0") Double radius,
                           HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.RESIDENT) {
            return "redirect:/resident/login";
        }
        
        List<Bin> nearbyBins;
        if (latitude != null && longitude != null) {
            nearbyBins = binService.findNearbyBins(latitude, longitude, radius);
        } else {
            // Default to Colombo coordinates
            nearbyBins = binService.findNearbyBins(6.9271, 79.8612, radius);
        }
        
        model.addAttribute("bins", nearbyBins);
        model.addAttribute("user", user);
        model.addAttribute("searchLatitude", latitude);
        model.addAttribute("searchLongitude", longitude);
        model.addAttribute("searchRadius", radius);
        
        return "resident/search-results";
    }

    @GetMapping("/reports")
    public String viewReports(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.RESIDENT) {
            return "redirect:/resident/login";
        }
        
        model.addAttribute("user", user);
        
        // Get bin statistics for the resident
        List<Bin> allBins = binService.findAllBins();
        long totalBins = allBins.size();
        long fullBins = allBins.stream().filter(b -> b.getStatus() == Bin.BinStatus.FULL).count();
        long overdueBins = allBins.stream().filter(b -> b.getStatus() == Bin.BinStatus.OVERDUE).count();
        
        model.addAttribute("totalBins", totalBins);
        model.addAttribute("fullBins", fullBins);
        model.addAttribute("overdueBins", overdueBins);
        
        return "resident/reports";
    }
}

