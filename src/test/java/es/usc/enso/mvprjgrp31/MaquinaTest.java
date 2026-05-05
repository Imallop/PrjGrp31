package es.usc.enso.mvprjgrp31;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.time.Instant;

public class MaquinaTest {

	private MaquinaDAO maquinaDAO;

	@BeforeEach
	void setUp() {
		maquinaDAO = MaquinaDAO.getInstance();
		maquinaDAO.clear();
	}

	@AfterEach
	void tearDown() {
		maquinaDAO = null;
	}

	@Test
    void testEqualsReflexivo() {
        Maquina m = new Maquina(1, new HashMap<>(), new Coordenadas(68.98,27.124,500.85), maquinaDAO);
        assertEquals(m, m);
    }

    @Test
    void testEqualsMismoContenido() {
    	Maquina m1 = new Maquina(1, new HashMap<>(), new Coordenadas(68.98,27.124,500.85), maquinaDAO);
    	Maquina m2 = new Maquina(1, new HashMap<>(), new Coordenadas(68.98,27.124,500.85), maquinaDAO);

        assertEquals(m1, m2);
    }

    @Test
    void testNotEquals() {
    	Maquina m1 = new Maquina(1, new HashMap<>(), new Coordenadas(68.98,27.124,500.85), maquinaDAO);
    	Maquina m2 = new Maquina(2, new HashMap<>(), new Coordenadas(68.98,27.124,500.85), maquinaDAO);

        assertNotEquals(m1, m2);
    }

    @Test
    void testEqualsNull() {
    	Maquina m = new Maquina(1, new HashMap<>(), new Coordenadas(68.98,27.124,500.85), maquinaDAO);
        assertNotEquals(null, m);
    }

    @Test
    @DisplayName("Consultar Stock")
    void testConsultarStock() {
    	HashMap<Producto,Integer> stock = new HashMap<>();
    	Producto chocolate = new Producto("Chocolate", (float) 25.0, 1);
    	Producto kitkat = new Producto("KitKat", (float) 30.0, 2);
    	Producto bocata = new Producto("Bocata", (float) 40.0, 3);


    	stock.put(chocolate, Constantes.STOCK_MINIMO);
    	stock.put(kitkat, Constantes.STOCK_MINIMO - 3);
    	stock.put(bocata, 17);


    	HashMap<Producto,Integer> stockSupuesto = new HashMap<>();
    	stockSupuesto.put(chocolate, Constantes.STOCK_MINIMO);
    	stockSupuesto.put(kitkat, Constantes.STOCK_MINIMO - 3);
    	stockSupuesto.put(bocata, 17);

    	Maquina m = new Maquina(1, stock, new Coordenadas(68.98,27.124,500.85), maquinaDAO);
    	Map<Producto,Integer> stockActual = m.consultarStock();

    	assertEquals(stockActual, stockSupuesto);
    }

    @Test
    @DisplayName("Actualizar Stock")
    void testActualizarStock() {
    	HashMap<Producto,Integer> stock = new HashMap<>();
    	Producto chocolate = new Producto("Chocolate", (float) 25.0, 1);
    	stock.put(chocolate, Constantes.STOCK_MAXIMO);
    	Maquina m = new Maquina(1, stock, new Coordenadas(68.98,27.124,500.85), maquinaDAO);
    	m.venta("Chocolate");

    	assertTrue(m.consultarStock().get(chocolate) == 19);
    }

    @Test
    @DisplayName("Elemento no existente, venta")
    void testNoSuchElement() {
    	HashMap<Producto,Integer> stock = new HashMap<>();
    	Maquina m = new Maquina(1, stock, new Coordenadas(68.98,27.124,500.85), maquinaDAO);
    	assertThrows(NoSuchElementException.class, () -> m.venta("Chocolate"));
    }

    @Test
    @DisplayName("Cantidad insuficiente, venta")
    void testIllegalState() {
    	HashMap<Producto,Integer> stock = new HashMap<>();
    	Producto chocolate = new Producto("Chocolate", (float) 25.0, 1);
    	stock.put(chocolate, 0);
    	Maquina m = new Maquina(1, stock, new Coordenadas(68.98,27.124,500.85), maquinaDAO);
    	assertThrows(IllegalStateException.class, () -> m.venta("Chocolate"));
    }

    @Test
    @DisplayName("Consultar Reposiciones")
    void testConsultarReposiciones() {
    	HashMap<Producto,Integer> stock = new HashMap<>();
    	Producto chocolate = new Producto("Chocolate", (float) 25.0, 1);
    	Producto kitkat = new Producto("KitKat", (float) 30.0, 2);
    	Producto bocata = new Producto("Bocata", (float) 40.0, 3);

    	stock.put(chocolate, Constantes.STOCK_MINIMO);
    	stock.put(kitkat, Constantes.STOCK_MINIMO - 3);
    	stock.put(bocata, 17);

    	/*HashMap<Producto,Integer> reposicionesSupuestas = new HashMap<>();
    	reposicionesSupuestas.put(chocolate, 15);
    	reposicionesSupuestas.put(kitkat, 18);*/
    	
    	Maquina m = new Maquina(1, stock, new Coordenadas(68.98,27.124,500.85), maquinaDAO);
    	Map<Producto,Integer> reposiciones = m.consultarReposiciones();

    	assertFalse(reposiciones.containsKey(bocata));
    	assertEquals(2, reposiciones.size());
    }

	@Test
	@DisplayName("Recarga")
	void testRecarga() {
		HashMap<Producto,Integer> stock = new HashMap<>();
		Producto chocolate = new Producto("Chocolate", (float) 25.0, 1);
		Producto kitkat = new Producto("KitKat", (float) 30.0, 2);

		stock.put(chocolate, Constantes.STOCK_MINIMO);
		stock.put(kitkat, Constantes.STOCK_MINIMO - 3);

		Maquina m = new Maquina(1, stock, new Coordenadas(68.98,27.124,500.85), maquinaDAO);

		m.recarga(m.consultarReposiciones().keySet().stream().toList());

		assertTrue(m.consultarStock().get(chocolate) == Constantes.STOCK_MAXIMO);
		assertTrue(m.consultarStock().get(kitkat) == Constantes.STOCK_MAXIMO);
	}
}













