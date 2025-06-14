package com.example.lab8_20224926.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "monitoreo_climatico")
@Data
public class MonitoreoClimatico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ciudad", nullable = false, length = 50)
    private String ciudad;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "temperatura_promedio", nullable = false)
    private Double temperaturaPromedio;

    @Column(name = "condicion_frecuente", nullable = false, length = 100)
    private String condicionFrecuente;

    @Column(name = "temperatura_maxima", nullable = false)
    private Double temperaturaMaxima;

    @Column(name = "temperatura_minima", nullable = false)
    private Double temperaturaMinima;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
}