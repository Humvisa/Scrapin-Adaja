package com.example.demo;

public class PluviometroDTO {
    private String id;
    private String nombre;
    private double lat;
    private double lon;
    private double precipitacion; // mm
    private String fecha;

    // Constructor
    public PluviometroDTO(String id, String nombre, double lat, double lon, double precipitacion, String fecha) {
        this.id = id;
        this.nombre = nombre;
        this.lat = lat;
        this.lon = lon;
        this.precipitacion = precipitacion;
        this.fecha = fecha;
    }

    // Getters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }
    public double getPrecipitacion() { return precipitacion; }
    public String getFecha() { return fecha; }
}