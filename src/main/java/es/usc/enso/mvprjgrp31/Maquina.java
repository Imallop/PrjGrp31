package es.usc.enso.mvprjgrp31;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


public class Maquina {

    private final int id;
    private Map<Producto,Integer> stock;
    private final Coordenadas coordenadas;
    private final MaquinaDAO maquinaDAO;

    public Maquina(int id, Map<Producto,Integer> stock, Coordenadas coordenadas, MaquinaDAO maquinaDAO) {
        this.id = id;
        this.stock = stock;
        this.coordenadas = coordenadas;
        this.maquinaDAO = maquinaDAO;
    }

    public Map<Producto,Integer> consultarStock(){

        return stock;

    }

    public Map<Producto,Integer> consultarReposiciones(){

        if (stock == null || stock.isEmpty()) {
            return new HashMap<>();
        }

        Map<Producto,Integer> reposiciones = new HashMap<>();

        for(Map.Entry<Producto,Integer> entry : stock.entrySet()){

            if(entry.getValue() <= Constantes.STOCK_MINIMO){
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

    public void recarga(List<Producto> recargar){

        List<Producto> productosRecargar = consultarReposiciones().keySet().stream().filter(recargar::contains).toList();

        for(Producto producto : recargar){
            stock.replace(producto,Constantes.STOCK_MAXIMO);
            maquinaDAO.registrarReposicion(id, producto, Instant.now());
        }
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
