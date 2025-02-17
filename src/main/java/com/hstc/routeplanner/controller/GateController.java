package com.hstc.routeplanner.controller;

import com.hstc.routeplanner.model.Gate;
import com.hstc.routeplanner.model.RouteResponse;
import com.hstc.routeplanner.repository.GateRepository;
import com.hstc.routeplanner.service.RouteService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/gates")
public class GateController {
    private final GateRepository gateRepository;
    private final RouteService routeService;

    @Autowired
    public GateController(GateRepository gateRepository, RouteService routeService) {
        this.gateRepository = gateRepository;
        this.routeService = routeService;
    }

    @GetMapping
    public List<Gate> getAllGates() {
        return gateRepository.findAll();
    }

    @GetMapping("/{gateCode}")
    public Gate getGate(@PathVariable String gateCode) {
        return gateRepository.findById(gateCode)
                .orElseThrow(() -> new RuntimeException("Gate not found"));
    }

    @GetMapping("/{gateCode}/to/{targetGateCode}")
    public RouteResponse findRoute(
            @PathVariable String gateCode,
            @PathVariable String targetGateCode
    ) {
        return routeService.findCheapestRoute(gateCode, targetGateCode);
    }
}