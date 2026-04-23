package es.usc.enso.mvprjgrp31;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class Producto {

    private final String nombre;
    private final float precio;
    private final int id;

    public Producto(String nombre, float precio, int id) {
        this.nombre = nombre;
        this.precio = precio;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public float getPrecio() {
        return precio;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Producto)) return false;
        Producto other = (Producto) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

}
