package com.sliit.smartbin.smartbin.controller;

import com.sliit.smartbin.smartbin.dto.BulkRequestDTO;
import com.sliit.smartbin.smartbin.model.PaymentStatus;
import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.service.BulkRequestService;
import jakarta.servlet.http.HttpSession;
// import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
// import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;
import java.util.Optional;

/**
 * SOLID PRINCIPLES APPLIED IN BULK REQUEST CONTROLLER
 * 
 * S - Single Responsibility Principle (SRP):
 *     This controller has ONE job: Handle HTTP requests for bulk waste collection requests.
 *     Business logic (fee calculation, payment processing) delegated to BulkRequestService.
 * 
 * D - Dependency Inversion Principle (DIP):
 *     Controller depends on BulkRequestService interface, not concrete implementation.
 *     Service can be swapped or mocked for testing without changing controller.
 */
@Controller
@RequestMapping("/resident")
public class BulkRequestController {
    
    // DIP: Depend on service interface abstraction
    @Autowired
    private BulkRequestService bulkRequestService;
    
    // Display bulk request form
    @GetMapping("/bulk-request")
    public String showBulkRequestForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/resident/login";
        }
        
        BulkRequestDTO bulkRequestDTO = new BulkRequestDTO();
        model.addAttribute("bulkRequest", bulkRequestDTO);
        model.addAttribute("user", user);
        return "resident/bulk-request";
    }
    
    // Submit bulk request
    @PostMapping("/bulk-request")
    public String submitBulkRequest(@ModelAttribute("bulkRequest") BulkRequestDTO bulkRequestDTO,
                                   Model model,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            System.out.println("❌ USER NOT LOGGED IN");
            return "redirect:/resident/login";
        }
        
        System.out.println("✅ User: " + user.getName());
        System.out.println("📦 Category: " + bulkRequestDTO.getCategory());
        System.out.println("📦 Description: " + bulkRequestDTO.getDescription());
        System.out.println("📦 Address: " + bulkRequestDTO.getStreetAddress());
        
        try {
            // Validate required fields
            if (bulkRequestDTO.getCategory() == null) {
                System.out.println("❌ Category is null");
                model.addAttribute("errorMessage", "Category is required");
                model.addAttribute("bulkRequest", bulkRequestDTO);
                model.addAttribute("user", user);
                return "resident/bulk-request";
            }
            
            System.out.println("✅ Calculating fee...");
            // Calculate fee
            bulkRequestDTO = bulkRequestService.calculateFee(bulkRequestDTO);
            System.out.println("💰 Total amount: LKR " + bulkRequestDTO.getTotalAmount());
            
            System.out.println("💾 Creating bulk request...");
            // Create bulk request
            BulkRequestDTO createdRequest = bulkRequestService.createBulkRequest(bulkRequestDTO, user);
            System.out.println("✅ Created! ID: " + createdRequest.getId() + ", RequestID: " + createdRequest.getRequestId());
            
            // Redirect directly to payment page for better UX
            String redirectUrl = "/resident/bulk-request/" + createdRequest.getId() + "/payment-page";
            System.out.println("🔄 Redirecting to: " + redirectUrl);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Request submitted! ID: " + createdRequest.getRequestId());
            
            return "redirect:" + redirectUrl;
            
        } catch (Exception e) {
            System.out.println("❌ ERROR:");
            e.printStackTrace(); // Log the error
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            model.addAttribute("bulkRequest", bulkRequestDTO);
            model.addAttribute("user", user);
            return "resident/bulk-request";
        }
    }
    
    // Show success page
    @GetMapping("/bulk-request-success")
    public String showSuccessPage(@RequestParam String requestId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/resident/login";
        }
        
        Optional<BulkRequestDTO> bulkRequest = bulkRequestService.getBulkRequestByRequestId(requestId);
        if (bulkRequest.isPresent()) {
            model.addAttribute("bulkRequest", bulkRequest.get());
            model.addAttribute("user", user);
            return "resident/bulk-request-success";
        } else {
            return "redirect:/resident/dashboard";
        }
    }
    
    // Show user's bulk requests
    @GetMapping("/my-bulk-requests")
    public String showMyBulkRequests(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/resident/login";
        }
        
        List<BulkRequestDTO> requests = bulkRequestService.getBulkRequestsByUser(user);
        model.addAttribute("bulkRequests", requests);
        model.addAttribute("user", user);
        return "resident/my-bulk-requests";
    }
    
    // Process payment
    @PostMapping("/bulk-request/{requestId}/payment")
    public String processPayment(@PathVariable Long requestId,
                                @RequestParam String paymentMethod,
                                @RequestParam(required = false) String paymentReference,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/resident/login";
        }
        
        try {
            BulkRequestDTO updatedRequest = bulkRequestService.processPayment(requestId, paymentMethod, paymentReference);
            
            if (updatedRequest.getPaymentStatus() == PaymentStatus.COMPLETED) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Payment successful! Your bulk collection request is now being processed. Authority has been notified.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Payment failed. Please try again or use a different payment method.");
            }
            
            return "redirect:/resident/my-bulk-requests";
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing payment: " + e.getMessage());
            return "redirect:/resident/my-bulk-requests";
        }
    }
    
    // Cancel request
    @PostMapping("/bulk-request/{requestId}/cancel")
    public String cancelRequest(@PathVariable Long requestId,
                               @RequestParam(required = false) String reason,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/resident/login";
        }
        
        try {
            bulkRequestService.cancelRequest(requestId, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Bulk collection request cancelled successfully.");
            return "redirect:/resident/my-bulk-requests";
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error cancelling request: " + e.getMessage());
            return "redirect:/resident/my-bulk-requests";
        }
    }
    
    // Calculate fee (AJAX endpoint)
    @PostMapping("/bulk-request/calculate-fee")
    @ResponseBody
    public BulkRequestDTO calculateFee(@RequestBody BulkRequestDTO bulkRequestDTO) {
        return bulkRequestService.calculateFee(bulkRequestDTO);
    }
    
    // Get request details by string ID (AJAX endpoint)
    @GetMapping("/bulk-request/{requestId}/details")
    @ResponseBody
    public BulkRequestDTO getRequestDetails(@PathVariable String requestId) {
        Optional<BulkRequestDTO> request = bulkRequestService.getBulkRequestByRequestId(requestId);
        return request.orElse(null);
    }
    
    // Get request details by numeric ID (AJAX endpoint)
    @GetMapping("/bulk-request/by-id/{id}/details")
    @ResponseBody
    public BulkRequestDTO getRequestDetailsById(@PathVariable Long id) {
        Optional<BulkRequestDTO> request = bulkRequestService.getBulkRequestById(id);
        return request.orElse(null);
    }
    
    // Show payment page
    @GetMapping("/bulk-request/{id}/payment-page")
    public String showPaymentPage(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/resident/login";
        }
        
        Optional<BulkRequestDTO> bulkRequest = bulkRequestService.getBulkRequestById(id);
        if (bulkRequest.isPresent()) {
            model.addAttribute("bulkRequest", bulkRequest.get());
            model.addAttribute("user", user);
            return "resident/bulk-request-payment";
        } else {
            return "redirect:/resident/my-bulk-requests";
        }
    }
}
