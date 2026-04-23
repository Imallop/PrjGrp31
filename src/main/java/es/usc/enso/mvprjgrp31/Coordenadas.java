package es.usc.enso.mvprjgrp31;

public class Coordenadas {

    private final double latitud;
    private final double longitud;
    private final double altitud;

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

    public static double distancia(Coordenadas a, Coordenadas b) {
        double lat1Rad = Math.toRadians(a.latitud);
        double lat2Rad = Math.toRadians(b.latitud);
        double deltaLat = Math.toRadians(b.latitud - a.latitud);
        double deltaLon = Math.toRadians(b.longitud - a.longitud);

        double sinLat = Math.sin(deltaLat / 2.0);
        double sinLon = Math.sin(deltaLon / 2.0);
        double h = sinLat * sinLat
                + Math.cos(lat1Rad) * Math.cos(lat2Rad) * sinLon * sinLon;
        double c = 2.0 * Math.atan2(Math.sqrt(h), Math.sqrt(1.0 - h));

        double earthRadiusMeters = 6371000.0;
        double surfaceDistance = earthRadiusMeters * c;
        double deltaAlt = b.altitud - a.altitud;
        return Math.sqrt(surfaceDistance * surfaceDistance + deltaAlt * deltaAlt);
    }

}
