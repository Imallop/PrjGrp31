package es.usc.enso.mvprjgrp31;

public class Producto {

    private String nombre;
    private float precio;
    private int id;

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

}
