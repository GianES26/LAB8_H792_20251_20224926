package com.example.lab8_20224926.repository;

import com.example.lab8_20224926.entity.MonitoreoClimatico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitoreoClimaticoRepository extends JpaRepository<MonitoreoClimatico, Long> {
}