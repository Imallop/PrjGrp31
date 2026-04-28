package es.usc.enso.mvprjgrp31;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.time.Instant;


public class Maquina {

    private final int id;
    private Map<Producto,Integer> stock;
    private final Coordenadas coordenadas;

    public Maquina(int id, Map<Producto,Integer> stock, Coordenadas coordenadas) {
        this.id = id;
        this.stock = stock;
        this.coordenadas = coordenadas;
    }

    public Map<Producto,Integer> consultarStock(){

        return stock;

    }

    public Map<Producto,Integer> consultarReposiciones(){

        Map<Producto,Integer> reposiciones = new HashMap<>();

        for(Map.Entry<Producto,Integer> entry : stock.entrySet()){

            if(entry.getValue() <= Constantes.STOCK_UMBRAL){
                reposiciones.put(entry.getKey(),Constantes.STOCK_MAXIMO - entry.getValue());
            }
        }
        return reposiciones;
    }

    public void venta(String nombreProducto) throws IllegalStateException,NoSuchElementException{

        for(Map.Entry<Producto,Integer> entry : stock.entrySet()){

            if(entry.getKey().getNombre().equals(nombreProducto)){

                int cantidad = entry.getValue();
                if(cantidad <= 0){
                    throw new IllegalStateException("No hay stock disponible de " + nombreProducto + ".");
                }
                entry.setValue(cantidad - 1);
                return;

            }

        }

        throw new NoSuchElementException("No existe el producto " + nombreProducto + "en esta máquina");

    }

    public Map<Instant, List<Producto>> recarga(List<Producto> recargar){

        List<Producto> productosRecargar = consultarReposiciones().keySet().stream().filter(recargar::contains).toList();

        for(Producto producto : recargar){
            stock.replace(producto,Constantes.STOCK_MAXIMO);
        }
        return Map.of(Instant.now(), productosRecargar);
    }

	public int getId() {
		return id;
	}

	public Coordenadas getCoordenadas() {
		return coordenadas;
	}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Maquina))  return false;
        Maquina other = (Maquina) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }


}
