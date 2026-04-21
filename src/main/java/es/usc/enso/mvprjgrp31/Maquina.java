package es.usc.enso.mvprjgrp31;

import java.util.Map;

public class Maquina {

    private int id;
    private Map<Producto,Integer> stock;
    private Coordenadas coordenadas;

    public Maquina(int id, Map<Producto,Integer> stock, Coordenadas coordenadas) {
        this.id = id;
        this.stock = stock;
        this.coordenadas = coordenadas;
    }

    public Map<Producto,Integer> consultarStock(){

        return stock;

    }

}