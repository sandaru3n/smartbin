package com.sliit.smartbin.smartbin.controller;

import com.sliit.smartbin.smartbin.model.Bin;
import com.sliit.smartbin.smartbin.model.RecyclingTransaction;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.model.WasteDisposal;
import com.sliit.smartbin.smartbin.service.BinService;
import com.sliit.smartbin.smartbin.service.RecyclingService;
import com.sliit.smartbin.smartbin.service.WasteDisposalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * SOLID PRINCIPLES APPLIED IN RESIDENT CONTROLLER
 * 
 * S - Single Responsibility Principle (SRP):
 *     This controller has ONE responsibility: Handle HTTP requests for resident features.
 *     Business logic is delegated to service classes.
 * 
 * D - Dependency Inversion Principle (DIP):
 *     Controller depends on Service INTERFACES (abstractions), not concrete implementations.
 *     Services are injected via constructor, allowing easy testing and flexibility.
 */
@Controller
@RequestMapping("/resident")
public class ResidentController {

    // DIP: Depend on abstractions (Service interfaces) not concrete classes
    private final BinService binService;
    private final WasteDisposalService wasteDisposalService;
    private final RecyclingService recyclingService;

    // DIP: Constructor injection for loose coupling and testability
    public ResidentController(BinService binService, 
                            WasteDisposalService wasteDisposalService,
                            RecyclingService recyclingService) {
        this.binService = binService;
        this.wasteDisposalService = wasteDisposalService;
        this.recyclingService = recyclingService;
    }

    // SRP: This method has ONE job - prepare and return the dashboard view
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.RESIDENT) {
            return "redirect:/resident/login";
        }
        
        model.addAttribute("user", user);
        
        // SRP: Data fetching delegated to service layer, not handled in controller
        // DIP: Using service interfaces instead of direct database access
        List<Bin> nearbyBins = binService.findNearbyBins(6.9271, 79.8612, 5.0); // Colombo coordinates
        model.addAttribute("nearbyBins", nearbyBins);
        
        List<Bin> alertedBins = binService.findAlertedBins();
        model.addAttribute("alertedBins", alertedBins);
        
        Double recyclingPoints = user.getRecyclingPoints() != null ? user.getRecyclingPoints() : 0.0;
        model.addAttribute("recyclingPoints", recyclingPoints);
        
        // DIP: Controller doesn't know HOW data is fetched, just calls service methods
        List<RecyclingTransaction> recentTransactions = recyclingService.getUserTransactions(user);
        model.addAttribute("recentTransactions", recentTransactions.isEmpty() ? recentTransactions : recentTransactions.subList(0, Math.min(5, recentTransactions.size())));
        
        List<WasteDisposal> recentDisposals = wasteDisposalService.getUserDisposals(user);
        model.addAttribute("recentDisposals", recentDisposals.isEmpty() ? recentDisposals : recentDisposals.subList(0, Math.min(5, recentDisposals.size())));
        
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
        
        // Default to Colombo coordinates if not provided
        double searchLat = latitude != null ? latitude : 6.9271;
        double searchLon = longitude != null ? longitude : 79.8612;
        
        List<Bin> nearbyBins = binService.findNearbyBins(searchLat, searchLon, radius);
        
        model.addAttribute("bins", nearbyBins);
        model.addAttribute("user", user);
        model.addAttribute("searchLatitude", searchLat);
        model.addAttribute("searchLongitude", searchLon);
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
    
    // QR Code scanning and waste disposal endpoints
    
    @GetMapping("/scan-bin")
    public String scanBinPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.RESIDENT) {
            return "redirect:/resident/login";
        }
        model.addAttribute("user", user);
        return "resident/scan-bin";
    }
    
    // SRP: Method only handles HTTP request/response, business logic in service
    // OCP: Method is closed for modification but open for extension (service can add new disposal types)
    @PostMapping("/submit-disposal")
    public String submitDisposal(@RequestParam String qrCode,
                                 @RequestParam Integer fillLevel,
                                 @RequestParam(required = false) String notes,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.RESIDENT) {
            return "redirect:/resident/login";
        }
        
        try {
            // DIP: Controller calls service abstraction, doesn't know implementation details
            // SRP: Disposal processing logic is in WasteDisposalService, not here
            WasteDisposal disposal = wasteDisposalService.submitDisposal(user, qrCode, fillLevel, notes);
            
            if (disposal.getStatus() == WasteDisposal.DisposalStatus.CONFIRMED) {
                redirectAttributes.addFlashAttribute("success", "Waste disposal recorded successfully! Thank you for contributing to a cleaner environment.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to record disposal. Please try again.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Network error: " + e.getMessage() + ". Please check your connection and try again.");
        }
        
        return "redirect:/resident/dashboard";
    }
    
    // Recycling endpoints
    
    // ISP: This endpoint provides only recycling unit search functionality
    // User doesn't need bin management or waste disposal features to find recycling units
    @GetMapping("/recycling-units")
    public String findRecyclingUnits(@RequestParam(required = false) Double latitude,
                                    @RequestParam(required = false) Double longitude,
                                    @RequestParam(defaultValue = "10.0") Double radius,
                                    HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.RESIDENT) {
            return "redirect:/resident/login";
        }
        
        // Default to Colombo coordinates
        double lat = latitude != null ? latitude : 6.9271;
        double lon = longitude != null ? longitude : 79.8612;
        
        // DIP: Using RecyclingService interface for location-based queries
        List<RecyclingService.RecyclingUnitLocation> units = recyclingService.getNearbyRecyclingUnits(lat, lon, radius);
        
        model.addAttribute("user", user);
        model.addAttribute("recyclingUnits", units);
        model.addAttribute("searchLatitude", lat);
        model.addAttribute("searchLongitude", lon);
        
        return "resident/recycling-units";
    }
    
    @GetMapping("/recycle")
    public String recyclePage(@RequestParam(required = false) String unitQrCode,
                             HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.RESIDENT) {
            return "redirect:/resident/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("unitQrCode", unitQrCode);
        
        return "resident/recycle";
    }
    
    // SRP: Controller handles HTTP, RecyclingService handles transaction processing
    // OCP: New item types can be added in service without modifying this controller
    @PostMapping("/submit-recycling")
    public String submitRecycling(@RequestParam String unitQrCode,
                                  @RequestParam String itemType,
                                  @RequestParam Double weight,
                                  @RequestParam(required = false) Integer quantity,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.RESIDENT) {
            return "redirect:/resident/login";
        }
        
        try {
            // DIP: Controller depends on RecyclingService interface, not implementation
            // SRP: Point calculation, pricing, and validation are all in the service
            RecyclingTransaction transaction = recyclingService.processRecyclingTransaction(
                user, unitQrCode, itemType, weight, quantity != null ? quantity : 1
            );
            
            if (transaction.getStatus() == RecyclingTransaction.TransactionStatus.CONFIRMED) {
                redirectAttributes.addFlashAttribute("success", 
                    String.format("Recycling confirmed! You earned %.2f points (Value: LKR %.2f). Keep up the great work!", 
                    transaction.getPointsEarned(), transaction.getPriceValue()));
            } else if (transaction.getStatus() == RecyclingTransaction.TransactionStatus.FAILED) {
                redirectAttributes.addFlashAttribute("error", 
                    "Recycling unit failed to connect. Please retry or contact support.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Network failure: Unable to process recycling. Please check your connection and try again.");
        }
        
        return "redirect:/resident/dashboard";
    }
    
    @GetMapping("/my-recycling")
    public String myRecycling(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.RESIDENT) {
            return "redirect:/resident/login";
        }
        
        List<RecyclingTransaction> transactions = recyclingService.getUserTransactions(user);
        
        model.addAttribute("user", user);
        model.addAttribute("transactions", transactions);
        model.addAttribute("totalPoints", user.getRecyclingPoints());
        
        return "resident/my-recycling";
    }
}

