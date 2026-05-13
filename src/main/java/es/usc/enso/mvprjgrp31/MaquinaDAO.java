package es.usc.enso.mvprjgrp31;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
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

    public Maquina getMaquinaCercana(Coordenadas c, int idAExcluir) throws MachineNotFoundException {
        if (maquinas.size() <= 0) {
            throw new MachineNotFoundException(c);
        }
        Maquina mCercana = null;
        double d = Double.POSITIVE_INFINITY;
        for (Maquina m : maquinas) {

            if (m.getId() == idAExcluir) {
                continue;
            }

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

        List<Instant> reposiciones = new ArrayList<>(getReposicionesProducto(idMaquina, producto));

        if (reposiciones.isEmpty()) {
            throw new IllegalStateException("No hay reposiciones");
        }

        Collections.sort(reposiciones);

        if (reposiciones.size() == 1) {
            return reposiciones.get(0).plusSeconds(7 * 24 * 60 * 60);
        }

        long sumaIntervalos = 0;

        for (int i = 1; i < reposiciones.size(); i++) {
            sumaIntervalos += reposiciones.get(i).toEpochMilli()
                            - reposiciones.get(i - 1).toEpochMilli();
        }

        long media = sumaIntervalos / (reposiciones.size() - 1);

        Instant ultimo = reposiciones.get(reposiciones.size() - 1);

        return ultimo.plusMillis(media);
    }

    public Map<Producto, Instant> calcularProximaReposicion(int idMaquina) {
        Map<Producto, Instant> proximaReposicion = new java.util.HashMap<>();

        for (Producto producto : getReposicionesMaquina(idMaquina).keySet()) {
            proximaReposicion.put(producto, calcularReposicionProducto(idMaquina, producto));
        }

        return proximaReposicion;
    }

    public String sugerirDesplazamientoStock(int idObjetivo, Producto p) throws MachineNotFoundException {
        Maquina objetivo = getMaquina(idObjetivo);
        int stockObj = objetivo.consultarStock().getOrDefault(p, 0);

        if (stockObj <= Constantes.STOCK_MINIMO) {
                // Intentamos buscar la más cercana excluyendo la actual
                Maquina cercana = getMaquinaCercana(objetivo.getCoordenadas(), idObjetivo);

                if (cercana == null) {
                    return "SIN_PROVEEDOR_CERCANO";
                }

                int stockCercana = cercana.consultarStock().getOrDefault(p, 0);

                if (stockCercana > Constantes.STOCK_MAXIMO) {
                    double dist = Coordenadas.distancia(objetivo.getCoordenadas(), cercana.getCoordenadas());

                    if (dist < 1000.0) {

                        if (objetivo.getCoordenadas().getAltitud() > 2500.0 &&
                            cercana.getCoordenadas().getAltitud() > 2500.0) {
                            return "REQUIERE_TRANSPORTE_ESPECIAL_ALTITUD";
                        }

                        return "TRANSFERENCIA_VIABLE";
                    }
                    return "DISTANCIA_EXCESIVA";
                }
                return "SIN_PROVEEDOR_CERCANO";
        }
        return "STOCK_SUFICIENTE";
    }

    public void clear() {
        maquinas.clear();
        historialReposiciones.clear();
    }
}
