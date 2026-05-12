package es.usc.enso.mvprjgrp31;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
	@DisplayName("Maquina - Equals: Reflexivo")
	void testEqualsReflexivo() {
		// Arrange
		Maquina m = new Maquina(1, new HashMap<>(), new Coordenadas(68.98, 27.124, 500.85), maquinaDAO);
		// Act & Assert
		assertEquals(m, m);
	}

	@Test
	@DisplayName("Maquina - Equals: Mismo contenido")
	void testEqualsMismoContenido() {
		// Arrange
		Maquina m1 = new Maquina(1, new HashMap<>(), new Coordenadas(68.98, 27.124, 500.85), maquinaDAO);
		Maquina m2 = new Maquina(1, new HashMap<>(), new Coordenadas(68.98, 27.124, 500.85), maquinaDAO);

		// Act & Assert
		assertEquals(m1, m2);
	}

	@Test
	@DisplayName("Maquina - Equals: Distinto id")
	void testNotEquals() {
		// Arrange
		Maquina m1 = new Maquina(1, new HashMap<>(), new Coordenadas(68.98, 27.124, 500.85), maquinaDAO);
		Maquina m2 = new Maquina(2, new HashMap<>(), new Coordenadas(68.98, 27.124, 500.85), maquinaDAO);

		// Act & Assert
		assertNotEquals(m1, m2);
	}

	@Test
	@DisplayName("Maquina - Equals: Distinta clase")
	void testNotSameClass() {
		// Arrange
		Maquina m1 = new Maquina(1, new HashMap<>(), new Coordenadas(68.98, 27.124, 500.85), maquinaDAO);
		ArrayList<Maquina> m2 = new ArrayList<>();

		// Act & Assert
		assertNotEquals(m1, m2);
	}

	@Test
	@DisplayName("Maquina - Equals: Null")
	void testEqualsNull() {
		// Arrange
		Maquina m = new Maquina(1, new HashMap<>(), new Coordenadas(68.98, 27.124, 500.85), maquinaDAO);
		// Act & Assert
		assertNotEquals(null, m);
	}

	@Test
	@DisplayName("Maquina - Consultar stock")
	void testConsultarStock() {
		// Arrange
		HashMap<Producto, Integer> stock = new HashMap<>();
		Producto chocolate = new Producto("Chocolate", (float) 25.0, 1);
		Producto kitkat = new Producto("KitKat", (float) 30.0, 2);
		Producto bocata = new Producto("Bocata", (float) 40.0, 3);

		stock.put(chocolate, Constantes.STOCK_MINIMO);
		stock.put(kitkat, Constantes.STOCK_MINIMO - 3);
		stock.put(bocata, 17);

		HashMap<Producto, Integer> stockSupuesto = new HashMap<>();
		stockSupuesto.put(chocolate, Constantes.STOCK_MINIMO);
		stockSupuesto.put(kitkat, Constantes.STOCK_MINIMO - 3);
		stockSupuesto.put(bocata, 17);

		Maquina m = new Maquina(1, stock, new Coordenadas(68.98, 27.124, 500.85), maquinaDAO);
		// Act
		Map<Producto, Integer> stockActual = m.consultarStock();

		// Assert
		assertEquals(stockActual, stockSupuesto);
	}

	@Test
	@DisplayName("Maquina - Consultar stock: Referencia compartida")
	void testConsultarStockReferenciaCompartida() {
		// Arrange
		HashMap<Producto, Integer> stock = new HashMap<>();
		Producto chocolate = new Producto("Chocolate", (float) 25.0, 1);
		Producto agua = new Producto("Agua", (float) 1.0, 2);
		stock.put(chocolate, Constantes.STOCK_MINIMO);
		Maquina m = new Maquina(1, stock, new Coordenadas(0.0, 0.0, 0.0), maquinaDAO);

		// Act
		Map<Producto, Integer> stockActual = m.consultarStock();
		stockActual.put(agua, Constantes.STOCK_MAXIMO);

		// Assert
		assertTrue(m.consultarStock().containsKey(agua));
	}

	@Test
	@DisplayName("Maquina - Actualizar stock")
	void testActualizarStock() {
		// Arrange
		HashMap<Producto, Integer> stock = new HashMap<>();
		Producto chocolate = new Producto("Chocolate", (float) 25.0, 1);
		stock.put(chocolate, Constantes.STOCK_MAXIMO);
		Maquina m = new Maquina(1, stock, new Coordenadas(68.98, 27.124, 500.85), maquinaDAO);
		// Act
		m.venta("Chocolate");

		// Assert
		assertTrue(m.consultarStock().get(chocolate) == 19);
	}

	@Test
	@DisplayName("Maquina - Venta: Producto inexistente")
	void testNoSuchElement() {
		// Arrange
		HashMap<Producto, Integer> stock = new HashMap<>();
		Maquina m = new Maquina(1, stock, new Coordenadas(68.98, 27.124, 500.85), maquinaDAO);
		// Act & Assert
		assertThrows(NoSuchElementException.class, () -> m.venta("Chocolate"));
	}

	@Test
	@DisplayName("Maquina - Venta: Sin coincidencia al recorrer el HashMap")
    void hashMapSinCoincidencia() {
		// Arrange
    	HashMap<Producto,Integer> stock = new HashMap<>();
    	Producto p = new Producto("Pepsi", (float) 6.99, 1);
		stock.put(p, Constantes.STOCK_MAXIMO);
    	Maquina m = new Maquina(1, stock, new Coordenadas(68.98,27.124,500.85), maquinaDAO);
		// Act
    	m.venta("Pepsi");
		// Assert
    	assertEquals(Constantes.STOCK_MAXIMO-1,m.consultarStock().get(p));
    }
	
	@Test
	@DisplayName("Maquina - Venta: Coincidencia al recorrer el HashMap")
	void hashMapConCoincidencia() {
		// Arrange
		HashMap<Producto, Integer> stock = new HashMap<>();
		stock.put(new Producto("Chocolate", (float) 6.99, 1), Constantes.STOCK_MAXIMO);
    	Maquina m = new Maquina(1, stock, new Coordenadas(68.98,27.124,500.85), maquinaDAO);
		// Act & Assert
    	assertThrows(NoSuchElementException.class, () -> m.venta("Pepsi"));
    }

	@Test
	@DisplayName("Maquina - Venta: Stock insuficiente")
	void testIllegalState() {
		// Arrange
		HashMap<Producto, Integer> stock = new HashMap<>();
		Producto chocolate = new Producto("Chocolate", (float) 25.0, 1);
		stock.put(chocolate, 0);
		Maquina m = new Maquina(1, stock, new Coordenadas(68.98, 27.124, 500.85), maquinaDAO);
		// Act & Assert
		assertThrows(IllegalStateException.class, () -> m.venta("Chocolate"));
	}

	@Test
	@DisplayName("Maquina - Consultar reposiciones")
	void testConsultarReposiciones() {
		// Arrange
		HashMap<Producto, Integer> stock = new HashMap<>();
		Producto chocolate = new Producto("Chocolate", (float) 25.0, 1);
		Producto kitkat = new Producto("KitKat", (float) 30.0, 2);
		Producto bocata = new Producto("Bocata", (float) 40.0, 3);

		stock.put(chocolate, Constantes.STOCK_MINIMO);
		stock.put(kitkat, Constantes.STOCK_MINIMO - 3);
		stock.put(bocata, 17);

		/*
		 * HashMap<Producto,Integer> reposicionesSupuestas = new HashMap<>();
		 * reposicionesSupuestas.put(chocolate, 15);
		 * reposicionesSupuestas.put(kitkat, 18);
		 */

		Maquina m = new Maquina(1, stock, new Coordenadas(68.98, 27.124, 500.85), maquinaDAO);
		// Act
		Map<Producto, Integer> reposiciones = m.consultarReposiciones();

		// Assert
		assertFalse(reposiciones.containsKey(bocata));
		assertEquals(2, reposiciones.size());
	}

	@Test
	@DisplayName("Maquina - Consultar reposiciones: Stock vacio")
	void testConsultarReposicionesStockVacio() {
		// Arrange
		HashMap<Producto, Integer> stock = new HashMap<>();
		Maquina m = new Maquina(1, stock, new Coordenadas(0.0, 0.0, 0.0), maquinaDAO);

		// Act
		Map<Producto, Integer> reposiciones = m.consultarReposiciones();

		// Assert
		assertTrue(reposiciones.isEmpty());
	}

	@Test
	@DisplayName("Maquina - Consultar reposiciones: Stock cero")
	void testConsultarReposicionesStockCero() {
		// Arrange
		HashMap<Producto, Integer> stock = new HashMap<>();
		Producto agua = new Producto("Agua", (float) 1.0, 10);
		stock.put(agua, 0);
		Maquina m = new Maquina(1, stock, new Coordenadas(0.0, 0.0, 0.0), maquinaDAO);

		// Act
		Map<Producto, Integer> reposiciones = m.consultarReposiciones();

		// Assert
		assertEquals(1, reposiciones.size());
		assertEquals(Constantes.STOCK_MAXIMO, reposiciones.get(agua));
	}

	@Test
	@DisplayName("Maquina - Consultar reposiciones: Stock null")
	void testConsultarReposicionesStockNull() {
		// Arrange
		Maquina m = new Maquina(1, null, new Coordenadas(0.0, 0.0, 0.0), maquinaDAO);

		// Act
		Map<Producto, Integer> reposiciones = m.consultarReposiciones();

		// Assert
		assertTrue(reposiciones.isEmpty());
	}

	@Test
	@DisplayName("Maquina - Consultar reposiciones: Sin reposicion")
	void testConsultarReposicionesSinReposicion() {
		// Arrange
		HashMap<Producto, Integer> stock = new HashMap<>();
		Producto agua = new Producto("Agua", (float) 1.0, 10);
		Producto cafe = new Producto("Cafe", (float) 1.5, 11);
		stock.put(agua, Constantes.STOCK_MAXIMO);
		stock.put(cafe, Constantes.STOCK_MINIMO + 1);
		Maquina m = new Maquina(1, stock, new Coordenadas(0.0, 0.0, 0.0), maquinaDAO);

		// Act
		Map<Producto, Integer> reposiciones = m.consultarReposiciones();

		// Assert
		assertTrue(reposiciones.isEmpty());
	}

	@Test
	@DisplayName("Maquina - Recarga")
	void testRecarga() {
		// Arrange
		HashMap<Producto, Integer> stock = new HashMap<>();
		Producto chocolate = new Producto("Chocolate", (float) 25.0, 1);
		Producto kitkat = new Producto("KitKat", (float) 30.0, 2);

		stock.put(chocolate, Constantes.STOCK_MINIMO);
		stock.put(kitkat, Constantes.STOCK_MINIMO - 3);

		Maquina m = new Maquina(1, stock, new Coordenadas(68.98, 27.124, 500.85), maquinaDAO);

		// Act
		m.recarga(m.consultarReposiciones().keySet().stream().toList());

		// Assert
		assertTrue(m.consultarStock().get(chocolate) == Constantes.STOCK_MAXIMO);
		assertTrue(m.consultarStock().get(kitkat) == Constantes.STOCK_MAXIMO);
	}

	// NUEVOS TESTS — Cobertura de decisión D4
	// D4: recargar.contains(producto)  [filtro del stream en recarga()]
	// -----------------------------------------------------------------------
 
	@Test
	@DisplayName("Maquina - Recarga: Producto necesita reposición y está en lista")
	void d4_recarga_productoNecesitaReposicionYEstaEnLista() {
		// Ambas ramas ejercidas: chocolate SÍ está en recargar (TRUE),
		// la lista tiene un único elemento por lo que el filtro solo evalúa TRUE.
		// Arrange
		Producto chocolate = new Producto("Chocolate", (float) 25.0, 1);
		HashMap<Producto, Integer> stock = new HashMap<>();
		stock.put(chocolate, Constantes.STOCK_MINIMO); // necesita reposición
 
		Maquina m = new Maquina(1, stock, new Coordenadas(68.98, 27.124, 500.85), maquinaDAO);
		// Act
		m.recarga(List.of(chocolate));
 
		// Assert
		assertEquals(Constantes.STOCK_MAXIMO, m.consultarStock().get(chocolate),
				"El producto debe alcanzar STOCK_MAXIMO tras la recarga");
	}
 
	@Test
	@DisplayName("Maquina - Recarga: Producto no necesita reposición")
	void d4_recarga_productoNoNecesitaReposicionNoDebeRecargarse() {
		// El filtro del stream evalúa FALSE para este producto:
		// está en 'recargar' pero su stock >= STOCK_MAXIMO, así que
		// 'productosRecargar' estará vacío y no debería tocarse.
		// Arrange
		Producto chocolate = new Producto("Chocolate", (float) 25.0, 1);
		HashMap<Producto, Integer> stock = new HashMap<>();
		stock.put(chocolate, Constantes.STOCK_MAXIMO); // NO necesita reposición
 
		Maquina m = new Maquina(1, stock, new Coordenadas(68.98, 27.124, 500.85), maquinaDAO);
		int stockAntes = m.consultarStock().get(chocolate);
		// Act
		m.recarga(List.of(chocolate));
 
		// Assert
		assertEquals(stockAntes, m.consultarStock().get(chocolate),
				"Un producto con stock suficiente no debe ser recargado");
	}

}
