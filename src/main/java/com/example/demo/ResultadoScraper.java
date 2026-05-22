package com.example.demo;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "resultados_scraper")
public class ResultadoScraper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_rio")
    private String nombreRio;

    @Column(name = "caudal")
    private Double caudal; // Unificado a objeto Double para evitar conflictos de casteo

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "hora")
    private LocalTime hora;

    // Métodos Getter y Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreRio() { return nombreRio; }
    public void setNombreRio(String nombreRio) { this.nombreRio = nombreRio; }

    public Double getCaudal() { return caudal; }
    public void setCaudal(Double caudal) { this.caudal = caudal; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
}