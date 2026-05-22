package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class PluviometroService {

    private static final String API_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnbWlsYWdyb3NtakBnbWFpbC5jb20iLCJqdGkiOiJiMjMwOWYwZi0xMmMzLTRiYWQtOWE3My0xYWE0YThjMDY0NGUiLCJpc3MiOiJBRU1FVCIsImlhdCI6MTcyOTU4MTI4NiwidXNlcklkIjoiYjIzMDlmMGYtMTJjMy00YmFkLTlhNzMtMWFhNGE4YzA2NDRlIiwicm9sZSI6IiJ9.TLY3IjMM4vgJOVYMeHmCMPWwWM0jwgldhHABn61jiKE";
    private static final String AEMET_URL =
            "https://opendata.aemet.es/opendata/api/observacion/convencional/todas?api_key=" + API_KEY;

    public List<PluviometroDTO> obtenerPluviometros() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        // PASO 1: Obtener la URL real (esto sí devuelve JSON normal)
        Map paso1 = restTemplate.getForObject(AEMET_URL, Map.class);
        String urlDatos = (String) paso1.get("datos");

        // PASO 2: Leer como String porque AEMET devuelve text/plain
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "*/*");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                urlDatos, HttpMethod.GET, entity, String.class
        );

        // Parsear el String como JSON
        List<Map> estaciones = mapper.readValue(response.getBody(), List.class);

        List<PluviometroDTO> resultado = new ArrayList<>();
        for (Map estacion : estaciones) {
            try {
                String id = (String) estacion.get("idema");
                String nombre = (String) estacion.get("ubi");
                double lat = ((Number) estacion.get("lat")).doubleValue();
                double lon = ((Number) estacion.get("lon")).doubleValue();
                double prec = estacion.get("prec") != null
                        ? ((Number) estacion.get("prec")).doubleValue()
                        : 0.0;
                String fecha = (String) estacion.get("fint");
                // Coordenadas de Castilla y León
                boolean enCyL = lat >= 40.0 && lat <= 43.5 && lon >= -7.0 && lon <= -2.0;

                if (enCyL) {
                    resultado.add(new PluviometroDTO(id, nombre, lat, lon, prec, fecha));
                }
            } catch (Exception e) {
                // Ignorar estaciones con datos incompletos
            }
        }

        return resultado;
    }
}