package com.hstc.routeplanner.service;

import com.hstc.routeplanner.model.Gate;
import com.hstc.routeplanner.model.RouteResponse;
import com.hstc.routeplanner.repository.GateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service
public class RouteService {
    private final GateRepository gateRepository;
    private final ObjectMapper objectMapper;
    private static final double COST_PER_HU = 0.10;

    @Autowired
    public RouteService(GateRepository gateRepository) {
        this.gateRepository = gateRepository;
        this.objectMapper = new ObjectMapper();
    }

    public RouteResponse findCheapestRoute(String fromGateId, String toGateId) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previousNodes = new HashMap<>();
        PriorityQueue<String> queue = new PriorityQueue<>(
                (a, b) -> distances.get(a) - distances.get(b)
        );

        gateRepository.findAll().forEach(gate -> {
            distances.put(gate.getId(), Integer.MAX_VALUE);
        });
        distances.put(fromGateId, 0);
        queue.add(fromGateId);

        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            Gate currentGate = gateRepository.findById(currentId).orElseThrow();

            try {
                JsonNode connections = objectMapper.readTree(currentGate.getConnections());
                for (JsonNode conn : connections) {
                    String targetId = conn.get("id").asText();
                    int hu = conn.get("hu").asInt();

                    int newDistance = distances.get(currentId) + hu;
                    if (newDistance < distances.get(targetId)) {
                        distances.put(targetId, newDistance);
                        previousNodes.put(targetId, currentId);
                        queue.add(targetId);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Error processing gate connections", e);
            }
        }

        List<String> route = new ArrayList<>();
        String current = toGateId;
        while (current != null) {
            route.add(0, current);
            current = previousNodes.get(current);
        }

        double totalHu = distances.get(toGateId);
        double cost = totalHu * COST_PER_HU;

        return new RouteResponse(route, totalHu, cost);
    }
}