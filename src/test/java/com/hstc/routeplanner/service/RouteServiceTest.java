package com.hstc.routeplanner.service;

import com.hstc.routeplanner.model.Gate;
import com.hstc.routeplanner.model.RouteResponse;
import com.hstc.routeplanner.repository.GateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Optional;

class RouteServiceTest {
    @Mock
    private GateRepository gateRepository;

    private RouteService routeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        routeService = new RouteService(gateRepository);

        Gate solGate = new Gate();
        solGate.setId("SOL");
        solGate.setName("Sol");
        solGate.setConnections("[{\"id\":\"PRX\",\"hu\":\"90\"}]");

        Gate prxGate = new Gate();
        prxGate.setId("PRX");
        prxGate.setName("Proxima");
        prxGate.setConnections("[{\"id\":\"SIR\",\"hu\":\"100\"}]");

        when(gateRepository.findAll()).thenReturn(Arrays.asList(solGate, prxGate));
        when(gateRepository.findById("SOL")).thenReturn(Optional.of(solGate));
        when(gateRepository.findById("PRX")).thenReturn(Optional.of(prxGate));
    }

    @Test
    @DisplayName("Should find direct route between connected gates")
    void findDirectRoute() {
        // Setup
        Gate solGate = new Gate();
        solGate.setId("SOL");
        solGate.setName("Sol");
        solGate.setConnections("[{\"id\":\"PRX\",\"hu\":90}]");

        Gate prxGate = new Gate();
        prxGate.setId("PRX");
        prxGate.setName("Proxima");
        prxGate.setConnections("[{\"id\":\"SOL\",\"hu\":90}]");

        when(gateRepository.findById("SOL")).thenReturn(Optional.of(solGate));
        when(gateRepository.findById("PRX")).thenReturn(Optional.of(prxGate));
        when(gateRepository.findAll()).thenReturn(Arrays.asList(solGate, prxGate));

        RouteResponse response = routeService.findCheapestRoute("SOL", "PRX");

        assertNotNull(response);
        assertEquals(Arrays.asList("SOL", "PRX"), response.getRoute());
        assertEquals(90, response.getTotalHu());
        assertEquals(9.0, response.getCost(), 0.01);
    }


    @Test
    @DisplayName("Should handle non-existent route")
    void handleNonExistentRoute() {
        when(gateRepository.findById("NON_EXISTENT")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            routeService.findCheapestRoute("SOL", "NON_EXISTENT");
        });

        assertEquals("No value present", exception.getMessage());
    }
}