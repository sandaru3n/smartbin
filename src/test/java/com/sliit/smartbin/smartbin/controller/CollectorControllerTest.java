package com.sliit.smartbin.smartbin.controller;

import com.sliit.smartbin.smartbin.model.*;
import com.sliit.smartbin.smartbin.repository.RouteBinRepository;
import com.sliit.smartbin.smartbin.service.BinService;
import com.sliit.smartbin.smartbin.service.CollectionService;
import com.sliit.smartbin.smartbin.service.RouteService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CollectorController
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CollectorController Unit Tests")
class CollectorControllerTest {

    @Mock
    private RouteService routeService;

    @Mock
    private CollectionService collectionService;

    @Mock
    private BinService binService;

    @Mock
    private RouteBinRepository routeBinRepository;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private CollectorController collectorController;

    private User testCollector;
    private Route testRoute;
    private Bin testBin;
    private Collection testCollection;
    private RouteBin testRouteBin;

    @BeforeEach
    void setUp() {
        testCollector = new User();
        testCollector.setId(1L);
        testCollector.setName("Collector 1");
        testCollector.setEmail("collector@example.com");
        testCollector.setRole(User.UserRole.COLLECTOR);

        testRoute = new Route();
        testRoute.setId(1L);
        testRoute.setRouteName("Route 1");
        testRoute.setCollector(testCollector);
        testRoute.setStatus(Route.RouteStatus.ASSIGNED);

        testBin = new Bin();
        testBin.setId(1L);
        testBin.setQrCode("QR123");
        testBin.setLocation("Test Location");
        testBin.setLatitude(6.9271);
        testBin.setLongitude(79.8612);
        testBin.setFillLevel(80);

        testCollection = new Collection();
        testCollection.setId(1L);
        testCollection.setBin(testBin);
        testCollection.setCollector(testCollector);
        testCollection.setStatus(Collection.CollectionStatus.ASSIGNED);

        testRouteBin = new RouteBin();
        testRouteBin.setId(1L);
        testRouteBin.setRoute(testRoute);
        testRouteBin.setBin(testBin);
        testRouteBin.setSequenceOrder(1);
    }

    // ========== DASHBOARD TESTS ==========

    @Test
    @DisplayName("Should display collector dashboard successfully")
    void dashboard_withValidCollector_shouldReturnDashboardView() {
        // Given
        when(session.getAttribute("user")).thenReturn(testCollector);
        when(routeService.findAssignedRoutesByCollector(testCollector)).thenReturn(Arrays.asList(testRoute));
        when(routeService.findActiveRoutesByCollector(testCollector)).thenReturn(Collections.emptyList());
        when(collectionService.getAssignedCollectionsByCollector(testCollector)).thenReturn(Arrays.asList(testCollection));
        when(collectionService.getCompletedCollectionsCountByCollector(testCollector)).thenReturn(5L);

        // When
        String viewName = collectorController.dashboard(session, model);

        // Then
        assertEquals("collector/dashboard", viewName);
        verify(model).addAttribute("user", testCollector);
        verify(model).addAttribute(eq("assignedRoutes"), anyList());
        verify(model).addAttribute(eq("activeRoutes"), anyList());
        verify(model).addAttribute(eq("assignedCollections"), anyList());
        verify(model).addAttribute("completedCollections", 5L);
    }

    @Test
    @DisplayName("Should redirect to login when user is null")
    void dashboard_withNullUser_shouldRedirectToLogin() {
        // Given
        when(session.getAttribute("user")).thenReturn(null);

        // When
        String viewName = collectorController.dashboard(session, model);

        // Then
        assertEquals("redirect:/collector/login", viewName);
        verify(routeService, never()).findAssignedRoutesByCollector(any());
    }

    @Test
    @DisplayName("Should redirect to login when user is not a collector")
    void dashboard_withNonCollectorUser_shouldRedirectToLogin() {
        // Given
        testCollector.setRole(User.UserRole.RESIDENT);
        when(session.getAttribute("user")).thenReturn(testCollector);

        // When
        String viewName = collectorController.dashboard(session, model);

        // Then
        assertEquals("redirect:/collector/login", viewName);
    }

    // ========== VIEW ROUTES TESTS ==========

    @Test
    @DisplayName("Should display routes list")
    void viewRoutes_withValidCollector_shouldReturnRoutesView() {
        // Given
        when(session.getAttribute("user")).thenReturn(testCollector);
        when(routeService.findRoutesByCollector(testCollector)).thenReturn(Arrays.asList(testRoute));

        // When
        String viewName = collectorController.viewRoutes(session, model);

        // Then
        assertEquals("collector/routes", viewName);
        verify(model).addAttribute(eq("routes"), anyList());
        verify(model).addAttribute("user", testCollector);
        verify(model).addAttribute("totalRoutes", 1);
    }

    // ========== VIEW ROUTE DETAILS TESTS ==========

    @Test
    @DisplayName("Should display route details")
    void viewRouteDetails_withValidRoute_shouldReturnDetailsView() {
        // Given
        when(session.getAttribute("user")).thenReturn(testCollector);
        when(routeService.findById(1L)).thenReturn(Optional.of(testRoute));
        when(routeBinRepository.findByRouteIdOrderBySequence(1L)).thenReturn(Arrays.asList(testRouteBin));

        // When
        String viewName = collectorController.viewRouteDetails(1L, session, model);

        // Then
        assertEquals("collector/route-details", viewName);
        verify(model).addAttribute("route", testRoute);
        verify(model).addAttribute(eq("routeBins"), anyList());
        verify(model).addAttribute("user", testCollector);
    }

    @Test
    @DisplayName("Should redirect when route not found")
    void viewRouteDetails_withInvalidRoute_shouldRedirectToRoutes() {
        // Given
        when(session.getAttribute("user")).thenReturn(testCollector);
        when(routeService.findById(999L)).thenReturn(Optional.empty());

        // When
        String viewName = collectorController.viewRouteDetails(999L, session, model);

        // Then
        assertEquals("redirect:/collector/routes", viewName);
    }

    @Test
    @DisplayName("Should redirect when route belongs to different collector")
    void viewRouteDetails_withOtherCollectorsRoute_shouldRedirectToDashboard() {
        // Given
        User otherCollector = new User();
        otherCollector.setId(999L);
        testRoute.setCollector(otherCollector);
        
        when(session.getAttribute("user")).thenReturn(testCollector);
        when(routeService.findById(1L)).thenReturn(Optional.of(testRoute));

        // When
        String viewName = collectorController.viewRouteDetails(1L, session, model);

        // Then
        assertEquals("redirect:/collector/dashboard", viewName);
    }

    // ========== START ROUTE TESTS ==========

    @Test
    @DisplayName("Should start route successfully")
    void startRoute_withValidRoute_shouldRedirectWithSuccess() {
        // Given
        testRoute.setStatus(Route.RouteStatus.IN_PROGRESS);
        when(session.getAttribute("user")).thenReturn(testCollector);
        when(routeService.startRoute(1L)).thenReturn(testRoute);

        // When
        String viewName = collectorController.startRoute(1L, session, redirectAttributes);

        // Then
        assertEquals("redirect:/collector/routes", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
    }

    @Test
    @DisplayName("Should handle exception when starting route")
    void startRoute_withException_shouldRedirectWithError() {
        // Given
        when(session.getAttribute("user")).thenReturn(testCollector);
        when(routeService.startRoute(1L)).thenThrow(new RuntimeException("Route error"));

        // When
        String viewName = collectorController.startRoute(1L, session, redirectAttributes);

        // Then
        assertEquals("redirect:/collector/routes", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    // ========== COMPLETE ROUTE TESTS ==========

    @Test
    @DisplayName("Should complete route successfully")
    void completeRoute_withValidRoute_shouldRedirectWithSuccess() {
        // Given
        testRoute.setStatus(Route.RouteStatus.COMPLETED);
        when(session.getAttribute("user")).thenReturn(testCollector);
        when(routeService.completeRoute(1L)).thenReturn(testRoute);

        // When
        String viewName = collectorController.completeRoute(1L, session, redirectAttributes);

        // Then
        assertEquals("redirect:/collector/routes", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
    }

    // ========== AUTHORIZATION TESTS ==========

    @Test
    @DisplayName("Should redirect all actions when user not logged in")
    void allActions_withoutUser_shouldRedirectToLogin() {
        // Given
        when(session.getAttribute("user")).thenReturn(null);

        // When & Then
        assertEquals("redirect:/collector/login", 
                collectorController.viewRoutes(session, model));
        assertEquals("redirect:/collector/login", 
                collectorController.viewRouteDetails(1L, session, model));
        assertEquals("redirect:/collector/login", 
                collectorController.startRoute(1L, session, redirectAttributes));
    }
}
