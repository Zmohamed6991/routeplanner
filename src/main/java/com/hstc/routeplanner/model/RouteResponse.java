package com.hstc.routeplanner.model;

import java.util.List;

public class RouteResponse {
    private List<String> route;
    private double totalHu;
    private double cost;

    public RouteResponse(List<String> route, double totalHu, double cost) {
        this.route = route;
        this.totalHu = totalHu;
        this.cost = cost;
    }

    public List<String> getRoute() { return route; }
    public double getTotalHu() { return totalHu; }
    public double getCost() { return cost; }
}