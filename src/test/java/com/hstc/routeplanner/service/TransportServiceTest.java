package com.hstc.routeplanner.service;

import com.hstc.routeplanner.model.TransportResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class TransportServiceTest {
    private TransportService transportService;

    @BeforeEach
    void setUp() {
        transportService = new TransportService();
    }

    @Test
    @DisplayName("Should recommend personal transport for small groups with short parking")
    void recommendPersonalTransportForSmallGroups() {
        TransportResponse response = transportService.calculateTransport(0.1, 2, 1);
        assertEquals("Personal Transport", response.getRecommendedTransport());
        assertEquals(5.03, response.getCost(), 0.01);
    }

    @Test
    @DisplayName("Should recommend HSTC transport for large groups")
    void recommendHSTCTransportForLargeGroups() {
        TransportResponse response = transportService.calculateTransport(1.0, 5, 1);
        assertEquals("HSTC Transport", response.getRecommendedTransport());
        assertEquals(0.45, response.getCost(), 0.01);
    }

}