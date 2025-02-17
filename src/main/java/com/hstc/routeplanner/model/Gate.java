package com.hstc.routeplanner.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.Type;

@Entity
public class Gate {
    @Id
    private String id;
    private String name;

    @Column(columnDefinition = "jsonb")
    private String connections;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getConnections() { return connections; }
    public void setConnections(String connections) { this.connections = connections; }
}