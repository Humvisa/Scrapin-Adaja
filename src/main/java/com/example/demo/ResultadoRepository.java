package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.HibernateHints;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResultadoRepository extends JpaRepository<ResultadoScraper, Long> {

    // 🔄 1. Limpiamos la caché para la consulta de datos actuales
    @QueryHints(@QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"))
    @Query("SELECT r FROM ResultadoScraper r ORDER BY r.id ASC")
    List<ResultadoScraper> findAllFresh();

    // 🕒 2. Limpiamos la caché para la consulta del histórico de las últimas 24 horas
    @QueryHints(@QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"))
    @Query(value = "SELECT * FROM resultados_scraper " +
            "WHERE CAST(fecha || ' ' || hora AS TIMESTAMP) >= (NOW() - INTERVAL '24 HOURS') " +
            "ORDER BY fecha ASC, hora ASC", nativeQuery = true)
    List<ResultadoScraper> findUltimas24Horas();
}