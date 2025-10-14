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

import java.util.List;
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
        
        // Get completed collections count
        Long completedCount = collectionService.getCompletedCollectionsCountByCollector(user);
        model.addAttribute("completedCollections", completedCount);
        
        return "collector/dashboard";
    }

    @GetMapping("/routes")
    public String viewRoutes(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.COLLECTOR) {
            return "redirect:/collector/login";
        }
        
        List<Route> routes = routeService.findRoutesByCollector(user);
        model.addAttribute("routes", routes);
        model.addAttribute("user", user);
        
        return "collector/routes";
    }

    @GetMapping("/route/{id}")
    public String viewRouteDetails(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.COLLECTOR) {
            return "redirect:/collector/login";
        }
        
        Optional<Route> route = routeService.findById(id);
        if (route.isPresent()) {
            List<RouteBin> routeBins = routeBinRepository.findByRouteIdOrderBySequence(id);
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
            routeService.startRoute(id);
            redirectAttributes.addFlashAttribute("success", "Route started successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to start route: " + e.getMessage());
        }
        
        return "redirect:/collector/route/" + id;
    }

    @PostMapping("/route/{id}/complete")
    public String completeRoute(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.COLLECTOR) {
            return "redirect:/collector/login";
        }
        
        try {
            routeService.completeRoute(id);
            redirectAttributes.addFlashAttribute("success", "Route completed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to complete route: " + e.getMessage());
        }
        
        return "redirect:/collector/route/" + id;
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
            CollectionDTO collectionDTO = new CollectionDTO();
            collectionDTO.setBinId(id);
            collectionDTO.setCollectorId(user.getId());
            collectionDTO.setCollectionType(Collection.CollectionType.STANDARD);
            collectionDTO.setStatus(Collection.CollectionStatus.COMPLETED);
            collectionDTO.setWasteType(wasteType);
            collectionDTO.setWasteLevel(wasteLevel);
            collectionDTO.setNotes(notes);
            
            collectionService.createCollection(collectionDTO);
            redirectAttributes.addFlashAttribute("success", "Collection completed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to complete collection: " + e.getMessage());
        }
        
        return "redirect:/collector/dashboard";
    }

    @GetMapping("/collections")
    public String viewCollections(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.UserRole.COLLECTOR) {
            return "redirect:/collector/login";
        }
        
        List<Collection> collections = collectionService.findCollectionsByCollector(user);
        model.addAttribute("collections", collections);
        model.addAttribute("user", user);
        
        return "collector/collections";
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
}

