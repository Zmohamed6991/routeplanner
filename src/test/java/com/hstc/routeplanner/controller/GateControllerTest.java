package com.hstc.routeplanner.controller;

import com.hstc.routeplanner.model.Gate;
import com.hstc.routeplanner.repository.GateRepository;
import com.hstc.routeplanner.service.RouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Optional;

@WebMvcTest(GateController.class)
class GateControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GateRepository gateRepository;

    @Autowired
    private RouteService routeService;

    @Configuration
    static class TestConfig {
        @Bean
        public GateRepository gateRepository() {
            return mock(GateRepository.class);
        }

        @Bean
        public RouteService routeService() {
            return mock(RouteService.class);
        }

        @Bean
        public GateController gateController(GateRepository gateRepository, RouteService routeService) {
            return new GateController(gateRepository, routeService);
        }
    }

    @BeforeEach
    void setUp() {
        reset(gateRepository, routeService);
    }

    @Test
    @DisplayName("Should return all gates")
    void getAllGates() throws Exception {
        Gate gate = new Gate();
        gate.setId("SOL");
        gate.setName("Sol");

        when(gateRepository.findAll()).thenReturn(Arrays.asList(gate));

        mockMvc.perform(get("/gates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("SOL"))
                .andExpect(jsonPath("$[0].name").value("Sol"));
    }

    @Test
    @DisplayName("Should return specific gate")
    void getSpecificGate() throws Exception {
        Gate gate = new Gate();
        gate.setId("SOL");
        gate.setName("Sol");

        when(gateRepository.findById("SOL")).thenReturn(Optional.of(gate));

        mockMvc.perform(get("/gates/SOL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("SOL"))
                .andExpect(jsonPath("$.name").value("Sol"));
    }

    @Test
    @DisplayName("Should return 404 for non-existent gate")
    void handleNonExistentGate() throws Exception {
        when(gateRepository.findById("NON_EXISTENT")).thenReturn(Optional.empty());

        mockMvc.perform(get("/gates/NON_EXISTENT"))
                .andExpect(status().isNotFound());
    }
}