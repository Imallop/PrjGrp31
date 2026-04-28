package es.usc.enso.mvprjgrp31;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MaquinaDAO {
    private static MaquinaDAO instance;
    private ArrayList<Maquina> maquinas;
    private Map<Integer, Map<Producto, List<Instant>>> historialReposiciones;

    private MaquinaDAO() {
        maquinas = new ArrayList<>();
        historialReposiciones = new java.util.HashMap<>();
    }

    public static MaquinaDAO getInstance() {
        if (instance == null) {
            instance = new MaquinaDAO();
        }
        return instance;
    }

    public void addMaquina(Maquina m) {
        maquinas.add(m);
    }

    public Maquina getMaquina(int id) throws MachineNotFoundException {
        for (Maquina m : maquinas) {
            if (m.getId() == id) {
                return m;
            }
        }
        throw new MachineNotFoundException(id);
    }

    public Maquina getMaquinaCercana(Coordenadas c) throws MachineNotFoundException {
        if (maquinas.size() <= 0) {
            throw new MachineNotFoundException(c);
        }
        Maquina mCercana = null;
        double d = Double.POSITIVE_INFINITY;
        for (Maquina m : maquinas) {
            double dAux = Coordenadas.distancia(c, m.getCoordenadas());
            if (dAux < d) {
                mCercana = m;
                d = dAux;
            }
        }
        return mCercana;
    }

    public ArrayList<Maquina> getMaquinas() {
        return new ArrayList<>(maquinas);
    }

    public void registrarReposicion(int idMaquina, Producto producto, Instant tiempo) {
        historialReposiciones.computeIfAbsent(idMaquina, k -> new java.util.HashMap<>())
                .computeIfAbsent(producto, k -> new java.util.ArrayList<>())
                .add(tiempo);
    }

    public Map<Integer, Map<Producto, List<Instant>>> getHistorialReposiciones() {
        return historialReposiciones;
    }

    public Map<Producto, List<Instant>> getReposicionesMaquina(int idMaquina) {
        return historialReposiciones.getOrDefault(idMaquina, Map.of());
    }

    public List<Instant> getReposicionesProducto(int idMaquina, Producto producto) {
        return historialReposiciones.getOrDefault(idMaquina, Map.of()).getOrDefault(producto, List.of());
    }

    public Instant calcularReposicionProducto(int idMaquina, Producto producto) {
        Instant previo = Instant.now().minusSeconds(2500000);
        Long media = 0L;
        for (Instant tiempo : getReposicionesProducto(idMaquina, producto)) {
            Long diferencia = tiempo.toEpochMilli() - previo.toEpochMilli();
            media += diferencia / getReposicionesProducto(idMaquina, producto).size();
        }

        Long proxima = getReposicionesProducto(idMaquina, producto).getLast().toEpochMilli() + media;

        return Instant.ofEpochMilli(proxima);
    }

    public Map<Producto, Instant> calcularProximaReposicion(int idMaquina) {
        Map<Producto, Instant> proximaReposicion = new java.util.HashMap<>();

        for (Producto producto : getReposicionesMaquina(idMaquina).keySet()) {
            proximaReposicion.put(producto, calcularReposicionProducto(idMaquina, producto));
        }

        return proximaReposicion;
    }
}
