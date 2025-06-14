package com.example.lab8_20224926.service;

import com.example.lab8_20224926.entity.MonitoreoClimatico;
import com.example.lab8_20224926.repository.MonitoreoClimaticoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClimaService {

    private final RestTemplate restTemplate;
    private final MonitoreoClimaticoRepository monitoreoRepository;

    @Value("${weather.api.key}")
    private String apiKey;

    public ClimaService(RestTemplate restTemplate, MonitoreoClimaticoRepository monitoreoRepository) {
        this.restTemplate = restTemplate;
        this.monitoreoRepository = monitoreoRepository;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getClimaActual(String ciudad) {
        String url = String.format("https://api.weatherapi.com/v1/current.json?key=%s&q=%s", apiKey, ciudad);
        Map<String, Object> apiResponse = restTemplate.getForObject(url, Map.class);
        Map<String, Object> current = (Map<String, Object>) apiResponse.get("current");
        Map<String, Object> condition = (Map<String, Object>) current.get("condition");

        Map<String, Object> clima = new HashMap<>();
        clima.put("temperatura_actual", current.get("temp_c"));
        clima.put("condicion_clima", condition.get("text"));
        clima.put("sensacion_termica", current.get("feelslike_c"));
        clima.put("humedad", current.get("humidity"));

        Map<String, Object> response = new HashMap<>();
        response.put("clima", clima);
        response.put("mensaje", "Condiciones climáticas actuales obtenidas correctamente");
        return response;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getPronosticoHorario(String ciudad) {
        String url = String.format("https://api.weatherapi.com/v1/forecast.json?key=%s&q=%s&days=1", apiKey, ciudad);
        Map<String, Object> apiResponse = restTemplate.getForObject(url, Map.class);
        Map<String, Object> forecast = (Map<String, Object>) apiResponse.get("forecast");
        List<Map<String, Object>> forecastDays = (List<Map<String, Object>>) forecast.get("forecastday");

        String today = LocalDate.now().toString();
        Optional<Map<String, Object>> targetDay = forecastDays.stream()
                .filter(day -> today.equals(day.get("date")))
                .findFirst();

        if (targetDay.isPresent()) {
            List<Map<String, Object>> hours = (List<Map<String, Object>>) targetDay.get().get("hour");
            List<Map<String, Object>> hourlyForecast = hours.stream().map(hour -> {
                Map<String, Object> condition = (Map<String, Object>) hour.get("condition");
                return new HashMap<String, Object>() {{
                    put("hora", hour.get("time"));
                    put("temperatura", hour.get("temp_c"));
                    put("condicion", condition.get("text"));
                }};
            }).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("pronostico_horario", hourlyForecast);
            response.put("mensaje", "Pronóstico horario obtenido correctamente");
            return response;
        } else {
            throw new RuntimeException("No se encontró pronóstico para el día de hoy");
        }
    }

    public Map<String, Object> registrarMonitoreo(MonitoreoClimatico monitoreo) {
        monitoreo.setFechaRegistro(LocalDateTime.now());
        monitoreo = monitoreoRepository.save(monitoreo);
        Map<String, Object> response = new HashMap<>();
        response.put("monitoreo", convertToMap(monitoreo));
        response.put("mensaje", "Monitoreo registrado correctamente");
        return response;
    }

    private Map<String, Object> convertToMap(MonitoreoClimatico monitoreo) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", monitoreo.getId());
        map.put("ciudad", monitoreo.getCiudad());
        map.put("fecha", monitoreo.getFecha().toString());
        map.put("temperaturaPromedio", monitoreo.getTemperaturaPromedio());
        map.put("condicionFrecuente", monitoreo.getCondicionFrecuente());
        map.put("temperaturaMaxima", monitoreo.getTemperaturaMaxima());
        map.put("temperaturaMinima", monitoreo.getTemperaturaMinima());
        map.put("fechaRegistro", monitoreo.getFechaRegistro().toString());
        return map;
    }
}