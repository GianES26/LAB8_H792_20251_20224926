package com.example.lab8_20224926.controller;

import com.example.lab8_20224926.entity.MonitoreoClimatico;
import com.example.lab8_20224926.service.ClimaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ClimaController {

    private final ClimaService climaService;

    public ClimaController(ClimaService climaService) {
        this.climaService = climaService;
    }

    @GetMapping("/clima/actual")
    public ResponseEntity<Map<String, Object>> getClimaActual(@RequestParam(required = false) String ciudad) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Validación de parámetro 'ciudad'
            if (ciudad == null || ciudad.trim().isEmpty()) {
                response.put("error", "Por favor, especifique una ciudad válida.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Consulta al servicio
            Map<String, Object> climaData = climaService.getClimaActual(ciudad);
            Map<String, Object> clima = (Map<String, Object>) climaData.get("clima");

            // Añadir más datos disponibles
            clima.put("viento_mph", climaData.get("wind_mph"));
            clima.put("direccion_viento", climaData.get("wind_dir"));
            clima.put("presion_mb", climaData.get("pressure_mb"));
            clima.put("precipitacion_mm", climaData.get("precip_mm"));

            response.put("clima", clima);
            response.put("mensaje", "Condiciones climáticas actuales obtenidas correctamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Manejo de errores de la API externa (e.g., ciudad no encontrada)
            if (e.getMessage().contains("400 Bad Request") || e.getMessage().contains("No matching location found")) {
                response.put("error", "No se encontró la ciudad especificada. Verifique el nombre e intente de nuevo.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            response.put("error", "Error al consultar las condiciones climáticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            response.put("error", "Ocurrió un error interno en el servidor. Intente de nuevo más tarde.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/clima/horario")
    public ResponseEntity<Map<String, Object>> getPronosticoHorario(@RequestParam String ciudad) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (ciudad == null || ciudad.isBlank()) {
                response.put("error", "El parámetro 'ciudad' es obligatorio");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            return ResponseEntity.ok(climaService.getPronosticoHorario(ciudad));
        } catch (RuntimeException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("error", "Error al consultar pronóstico horario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/monitoreo")
    public ResponseEntity<Map<String, Object>> registrarMonitoreo(@RequestBody MonitoreoClimatico input) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        if (input.getCiudad() == null || input.getCiudad().isBlank() || input.getCiudad().length() > 50) {
            errors.put("ciudad", "La ciudad es obligatoria y no debe exceder 50 caracteres");
        }
        if (input.getFecha() == null) {
            errors.put("fecha", "La fecha es obligatoria");
        }
        if (input.getTemperaturaPromedio() == null) {
            errors.put("temperaturaPromedio", "La temperatura promedio es obligatoria");
        }
        if (input.getCondicionFrecuente() == null || input.getCondicionFrecuente().isBlank() || input.getCondicionFrecuente().length() > 100) {
            errors.put("condicionFrecuente", "La condición frecuente es obligatoria y no debe exceder 100 caracteres");
        }
        if (input.getTemperaturaMaxima() == null) {
            errors.put("temperaturaMaxima", "La temperatura máxima es obligatoria");
        }
        if (input.getTemperaturaMinima() == null) {
            errors.put("temperaturaMinima", "La temperatura mínima es obligatoria");
        }

        if (!errors.isEmpty()) {
            response.put("error", errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(climaService.registrarMonitoreo(input));
        } catch (Exception e) {
            response.put("error", "Error al registrar el monitoreo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}