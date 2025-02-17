package com.hstc.routeplanner.controller;

import com.hstc.routeplanner.model.TransportResponse;
import com.hstc.routeplanner.service.TransportService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/transport")
public class TransportController {
    private final TransportService transportService;

    @Autowired
    public TransportController(TransportService transportService) {
        this.transportService = transportService;
    }

    @GetMapping("/{distance}")
    public TransportResponse calculateTransport(
            @PathVariable double distance,
            @RequestParam int passengers,
            @RequestParam int parking
    ) {
        return transportService.calculateTransport(distance, passengers, parking);
    }
}