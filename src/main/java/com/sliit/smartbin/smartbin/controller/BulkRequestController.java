package com.sliit.smartbin.smartbin.controller;

import com.sliit.smartbin.smartbin.dto.BulkRequestDTO;
import com.sliit.smartbin.smartbin.model.BulkRequest;
import com.sliit.smartbin.smartbin.model.BulkRequestStatus;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/resident")
public class BulkRequestController {
    
    @Autowired
    private BulkRequestService bulkRequestService;
    
    // Display bulk request form
    @GetMapping("/bulk-request")
    public String showBulkRequestForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        BulkRequestDTO bulkRequestDTO = new BulkRequestDTO();
        model.addAttribute("bulkRequest", bulkRequestDTO);
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
            return "redirect:/login";
        }
        
        try {
            // Validate required fields
            if (bulkRequestDTO.getCategory() == null) {
                model.addAttribute("errorMessage", "Category is required");
                model.addAttribute("bulkRequest", bulkRequestDTO);
                return "resident/bulk-request";
            }
            
            // Calculate fee
            bulkRequestDTO = bulkRequestService.calculateFee(bulkRequestDTO);
            
            // Create bulk request
            BulkRequestDTO createdRequest = bulkRequestService.createBulkRequest(bulkRequestDTO, user);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Bulk collection request submitted successfully! Request ID: " + createdRequest.getRequestId());
            
            return "redirect:/resident/bulk-request-success?requestId=" + createdRequest.getRequestId();
            
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            model.addAttribute("errorMessage", "Error submitting request: " + e.getMessage());
            model.addAttribute("bulkRequest", bulkRequestDTO);
            return "resident/bulk-request";
        }
    }
    
    // Show success page
    @GetMapping("/bulk-request-success")
    public String showSuccessPage(@RequestParam String requestId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        Optional<BulkRequestDTO> bulkRequest = bulkRequestService.getBulkRequestByRequestId(requestId);
        if (bulkRequest.isPresent()) {
            model.addAttribute("bulkRequest", bulkRequest.get());
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
            return "redirect:/login";
        }
        
        List<BulkRequestDTO> requests = bulkRequestService.getBulkRequestsByUser(user);
        model.addAttribute("bulkRequests", requests);
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
            return "redirect:/login";
        }
        
        try {
            BulkRequestDTO updatedRequest = bulkRequestService.processPayment(requestId, paymentMethod, paymentReference);
            
            if (updatedRequest.getPaymentStatus() == PaymentStatus.COMPLETED) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Payment successful! Your bulk collection request is now being processed.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Payment failed. Please try again or use a different payment method.");
            }
            
            return "redirect:/resident/my-bulk-requests";
            
        } catch (Exception e) {
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
            return "redirect:/login";
        }
        
        try {
            bulkRequestService.cancelRequest(requestId, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Bulk collection request cancelled successfully.");
            return "redirect:/resident/my-bulk-requests";
            
        } catch (Exception e) {
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
    
    // Get request details (AJAX endpoint)
    @GetMapping("/bulk-request/{requestId}/details")
    @ResponseBody
    public BulkRequestDTO getRequestDetails(@PathVariable String requestId) {
        Optional<BulkRequestDTO> request = bulkRequestService.getBulkRequestByRequestId(requestId);
        return request.orElse(null);
    }
}
