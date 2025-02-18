package com.hstc.routeplanner.config;

import com.hstc.routeplanner.controller.GateController;
import com.hstc.routeplanner.repository.GateRepository;
import com.hstc.routeplanner.service.RouteService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
class TestConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("hstc_test")
                .withUsername("test")
                .withPassword("test")
                .withExposedPorts(5433);
    }

    @Bean
    public GateController gateController(GateRepository gateRepository, RouteService routeService) {
        return new GateController(gateRepository, routeService);
    }
}