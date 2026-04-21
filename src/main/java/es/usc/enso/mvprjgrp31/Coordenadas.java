package es.usc.enso.mvprjgrp31;

public class Coordenadas {

    private double latitud;
    private double longitud;
    private double altitud;

    public Coordenadas(double latitud, double longitud, double altitud) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.altitud = altitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getAltitud() {
        return altitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public double[] getCoordenadas() {
        return new double[]{latitud, longitud, altitud};
    }

}
