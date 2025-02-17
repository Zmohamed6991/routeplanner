package com.hstc.routeplanner.model;

public class TransportResponse {
    private String recommendedTransport;
    private double cost;

    public TransportResponse(String recommendedTransport, double cost) {
        this.recommendedTransport = recommendedTransport;
        this.cost = cost;
    }

    public String getRecommendedTransport() { return recommendedTransport; }
    public double getCost() { return cost; }
}