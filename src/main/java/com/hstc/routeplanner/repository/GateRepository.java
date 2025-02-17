package com.hstc.routeplanner.repository;

import com.hstc.routeplanner.model.Gate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GateRepository extends JpaRepository<Gate, String> {
}
