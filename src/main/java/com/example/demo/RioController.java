package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import java.util.List;
import java.util.ArrayList;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*") // Permite la comunicación directa con tu frontend en React sin bloqueos CORS
public class RioController {

    @Autowired
    private ResultadoRepository repository;

    /**
     * 1. Endpoint para el último dato en tiempo real (Marcador del Mapa)
     * Forzamos una transacción nueva para romper el aislamiento de datos antiguos de Hibernate.
     */
    @GetMapping("/api/rios")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<ResultadoScraper> obtenerDatosRios() {
        try {
            // Utiliza el método personalizado sin caché de la base de datos
            List<ResultadoScraper> lista = repository.findAllFresh();
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            System.out.println("❌ Error al obtener datos actuales en RioController: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 2. Endpoint para el histórico de las últimas 24 horas (Gráfica Lineal)
     * Trae estrictamente el tramo de tiempo del último día basándose en el reloj local.
     */
    @GetMapping("/api/rios/historico")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<ResultadoScraper> obtenerHistorico24Horas() {
        try {
            List<ResultadoScraper> ultimas24Horas = repository.findUltimas24Horas();

            if (ultimas24Horas == null || ultimas24Horas.isEmpty()) {
                return new ArrayList<>();
            }

            // Nota: Los datos ya vienen ordenados cronológicamente (Pasado -> Presente)
            // desde la base de datos gracias al ORDER BY de la query nativa.
            return ultimas24Horas;

        } catch (Exception e) {
            System.out.println("❌ Error al obtener histórico de 24 horas en RioController: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}