package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PluviometroController {

    private final PluviometroService service;

    public PluviometroController(PluviometroService service) {
        this.service = service;
    }

    @GetMapping("/pluviometros")
    public ResponseEntity<?> getPluviometros() {
        try {
            List<PluviometroDTO> datos = service.obtenerPluviometros();
            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error consultando AEMET: " + e.getMessage());
        }
    }
}