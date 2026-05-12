package es.usc.enso.mvprjgrp31;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.AfterEach;

import static org.junit.Assert.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests para la funcionalidad de gestión de máquinas (anteriormente Memoria)
 * ahora integrada en MaquinaDAO
 */
@ExtendWith(MockitoExtension.class)
public class MaquinaDAOTest {

	private MaquinaDAO maquinaDAO;
	private Producto producto;

    @Mock
    private Maquina maquinaMock;
    @Mock
    private Maquina maquinaA;
    @Mock
    private Maquina maquinaB;
    @Mock
    private Maquina maquinaC;

	@BeforeEach
	void setUp() {
		maquinaDAO = MaquinaDAO.getInstance();
		maquinaDAO.clear();
		producto = new Producto("Refresco", 1.5f, 1);
	}

    @AfterEach
    void tearDown() {
        maquinaDAO = null;
    }

    @Test
	@DisplayName("MaquinaDAO - Add/Get: Misma instancia")
    void addYGetMismaInstancia() throws Exception {
        // Arrange
        when(maquinaMock.getId()).thenReturn(7);

        // Act
        maquinaDAO.addMaquina(maquinaMock);
        Maquina resultado = assertTimeout(Duration.ofMillis(100), () -> maquinaDAO.getMaquina(7));

        // Assert
        assertAll(
                () -> assertSame(maquinaMock, resultado),
                () -> assertEquals(7, resultado.getId()),
                () -> assertNotNull(resultado));
    }

    @ParameterizedTest(name = "id {0} -> pos {1}")
	@DisplayName("MaquinaDAO - GetMaquina: Por id (CSV)")
    @CsvSource({
            "1, 0",
            "2, 1",
            "3, 2"
    })
    void getMaquinaCsv(int id, int index) throws Exception {
        // Arrange
        Maquina[] maquinas = new Maquina[] {
                new Maquina(1, new HashMap<>(), new Coordenadas(0.0, 0.0, 0.0), maquinaDAO),
                new Maquina(2, new HashMap<>(), new Coordenadas(1.0, 1.0, 0.0), maquinaDAO),
                new Maquina(3, new HashMap<>(), new Coordenadas(2.0, 2.0, 0.0), maquinaDAO)
        };
        for (Maquina m : maquinas) {
            maquinaDAO.addMaquina(m);
        }

        // Act
        Maquina encontrada = maquinaDAO.getMaquina(id);

        // Assert
        assertAll(
                () -> assertSame(maquinas[index], encontrada),
                () -> assertTrue(encontrada.getId() == id));
    }

    @Test
	@DisplayName("MaquinaDAO - GetMaquina: No existe (con fail)")
    void getMaquinaNoExisteConFail() {
		// Arrange
        try {
			// Act
            maquinaDAO.getMaquina(99);
            fail("Se esperaba MachineNotFoundException y no se lanzó ninguna excepción");
        } catch (MachineNotFoundException ex) {
			// Assert
            assertTrue(ex.getMessage().contains("Machine not found"));
            assertNull(ex.getCause());
        } catch (Exception ex) {
            fail("Se lanzó una excepción incorrecta: " + ex.getClass().getSimpleName());
        }
    }

	@Test
	@DisplayName("MaquinaDAO - GetMaquinaCercana: Devuelve la mas cercana")
	void cercanaDevuelveMasCerca() throws Exception {
		// Arrange
		Coordenadas origen = new Coordenadas(0.0, 0.0, 0.0);
		Coordenadas coordA = new Coordenadas(0.0001, 0.0, 0.0);
		Coordenadas coordB = new Coordenadas(0.01, 0.0, 0.0);
		Coordenadas coordC = new Coordenadas(1.0, 1.0, 0.0);

		// Configuramos IDs distintos para cada mock
		when(maquinaA.getId()).thenReturn(1);
		when(maquinaB.getId()).thenReturn(2);
		when(maquinaC.getId()).thenReturn(3);

        when(maquinaA.getCoordenadas()).thenReturn(coordA);
        when(maquinaB.getCoordenadas()).thenReturn(coordB);
        when(maquinaC.getCoordenadas()).thenReturn(coordC);

        maquinaDAO.addMaquina(maquinaA);
        maquinaDAO.addMaquina(maquinaB);
        maquinaDAO.addMaquina(maquinaC);

		// Act
		// Usamos -1 como idAExcluir para que evalúe todas las máquinas registradas
		Maquina resultado = maquinaDAO.getMaquinaCercana(origen, -1);

		// Assert
		assertAll(
				() -> assertSame(maquinaA, resultado, "Debería retornar la máquina A por ser la más cercana"),
				() -> assertArrayEquals(
						coordA.getCoordenadas(),
						resultado.getCoordenadas().getCoordenadas(),
						0.000001),
				() -> assertTrue(
						Coordenadas.distancia(origen, coordA) < Coordenadas.distancia(origen, coordB)));
	}

	@Test
	@DisplayName("MaquinaDAO - GetMaquinaCercana: Sin máquinas")
	void cercanaSinMaquinas() {
		// Arrange
		Coordenadas origen = new Coordenadas(0.0, 0.0, 0.0);
		// Aseguramos que el DAO está limpio
		maquinaDAO.clear();

		// Act & Assert
		MachineNotFoundException ex = assertThrows(
				MachineNotFoundException.class,
				() -> maquinaDAO.getMaquinaCercana(origen, -1));

		// Verificamos que el mensaje es el correcto (proviene de MachineNotFoundException)
		assertTrue(ex.getMessage().contains("Machine not found near coordinates"));
	}

	@Test
	@DisplayName("MaquinaDAO - GetMaquinas: Copia defensiva")
	void getMaquinasDevuelveCopia() {
		// Arrange
		MaquinaDAO maquinaDAO = MaquinaDAO.getInstance();

		// Act
		ArrayList<Maquina> lista1 = maquinaDAO.getMaquinas();
		ArrayList<Maquina> lista2 = maquinaDAO.getMaquinas();

		// Assert
		assertNotSame(lista1, lista2);
	}

	@Test
	@DisplayName("MaquinaDAO - CalcularProximaReposicion: Tiempo máximo")
	void tiempoMaximoCalcularReposicion() {
		// Arrange
        HashMap<Producto, Integer> stock = new HashMap<>();
        Producto chocolate = new Producto("Chocolate", (float) 25.0, 1);
        Producto kitkat = new Producto("KitKat", (float) 30.0, 2);
        Producto bocata = new Producto("Bocata", (float) 40.0, 3);

        stock.put(chocolate, Constantes.STOCK_MINIMO);
        stock.put(kitkat, Constantes.STOCK_MINIMO - 3);
        stock.put(bocata, 17);

        Maquina maquina = new Maquina(1, stock, new Coordenadas(0.0, 0.0, 0.0), maquinaDAO);
        maquinaDAO.addMaquina(maquina);

        // Act
        maquina.recarga(new ArrayList<>(maquina.consultarReposiciones().keySet()));

        // Assert
        assertTimeoutPreemptively(Duration.ofMillis(10), () -> {
            maquinaDAO.calcularProximaReposicion(1).entrySet();
        });
    }
 
	@Test
	@DisplayName("MaquinaDAO - CalcularReposicionProducto: Un registro")
	void d9_calcularReposicion_unRegistro() {
		// Arrange
		Producto chocolate = new Producto("Chocolate", (float) 25.0, 1);
		Instant ahora = Instant.now();
		maquinaDAO.registrarReposicion(1, chocolate, ahora);
 
		// El bucle itera una vez (TRUE una vez, luego FALSE al acabar)
		// Act
		Instant proxima = maquinaDAO.calcularReposicionProducto(1, chocolate);
 
		// Assert
		assertNotNull(proxima, "Con un registro el resultado no debe ser null");
		assertTrue(proxima.isAfter(ahora),
				"La próxima reposición estimada debe estar después del último registro conocido");
	}
 
	@Test
	@DisplayName("MaquinaDAO - CalcularReposicionProducto: Varios registros")
	void d9_calcularReposicion_variosRegistros() {
		// Arrange
		Producto chocolate = new Producto("Chocolate", (float) 25.0, 1);
		Instant t1 = Instant.parse("2025-01-01T10:00:00Z");
		Instant t2 = Instant.parse("2025-01-08T10:00:00Z"); // +7 días
		Instant t3 = Instant.parse("2025-01-15T10:00:00Z"); // +7 días
		maquinaDAO.registrarReposicion(1, chocolate, t1);
		maquinaDAO.registrarReposicion(1, chocolate, t2);
		maquinaDAO.registrarReposicion(1, chocolate, t3);
 
		// El bucle itera 3 veces; la media entre reposiciones es 7 días
		// Act
		Instant proxima = maquinaDAO.calcularReposicionProducto(1, chocolate);
 
		// Assert
		Instant esperada = Instant.parse("2025-01-22T10:00:00Z"); // t3 + 7 días
		long toleranciaMs = 60_000L; // ±1 minuto por aritmética entera
		assertTrue(
				Math.abs(proxima.toEpochMilli() - esperada.toEpochMilli()) < toleranciaMs,
				"La próxima reposición debe ser aproximadamente t3 + media_entre_reposiciones (7 días)");
	}

	@Test
	@DisplayName("MaquinaDAO - SugerirDesplazamiento: Máquina objetivo no existe")
	void testMaquinaNoExiste() {
		// Act & Assert
		assertThrows(MachineNotFoundException.class, () ->
			maquinaDAO.sugerirDesplazamientoStock(999, producto));
	}

	@Test
	@DisplayName("MaquinaDAO - SugerirDesplazamiento: Stock suficiente")
	void testStockSuficiente() throws MachineNotFoundException {
		// Arrange
		Map<Producto, Integer> stock = new HashMap<>();
		stock.put(producto, Constantes.STOCK_MINIMO + 1); // 6
		Maquina m = new Maquina(1, stock, new Coordenadas(0, 0, 0), maquinaDAO);
		maquinaDAO.addMaquina(m);

		// Act & Assert
		assertEquals("STOCK_SUFICIENTE", maquinaDAO.sugerirDesplazamientoStock(1, producto));
	}

    @Nested
	@DisplayName("MaquinaDAO - Escenarios de máquina cercana y transferencia")
    class EscenariosTransferencia {

        @BeforeEach
        void prepararMaquinaObjetivoEnMinimos() {
            Map<Producto, Integer> stockBajo = new HashMap<>();
            stockBajo.put(producto, Constantes.STOCK_MINIMO); // 5
            Maquina obj = new Maquina(1, stockBajo, new Coordenadas(0, 0, 0), maquinaDAO);
            maquinaDAO.addMaquina(obj);
        }

        @Test
		@DisplayName("MaquinaDAO - SugerirDesplazamiento: Sin otras máquinas")
        void testSinOtrasMaquinas() throws MachineNotFoundException {
			// Act & Assert
            assertEquals("SIN_PROVEEDOR_CERCANO", maquinaDAO.sugerirDesplazamientoStock(1, producto));
        }

        @Test
		@DisplayName("MaquinaDAO - SugerirDesplazamiento: Cercana sin excedente")
        void testCercanaSinExcedente() throws MachineNotFoundException {
			// Arrange
            Map<Producto, Integer> stockNormal = new HashMap<>();
            stockNormal.put(producto, Constantes.STOCK_MAXIMO); // 20

            // Situada a 100m
            Maquina cercana = new Maquina(2, stockNormal, new Coordenadas(0.0009, 0, 0), maquinaDAO);
            maquinaDAO.addMaquina(cercana);

			// Act & Assert
            assertEquals("SIN_PROVEEDOR_CERCANO", maquinaDAO.sugerirDesplazamientoStock(1, producto));
        }

        @Test
		@DisplayName("MaquinaDAO - SugerirDesplazamiento: Distancia excesiva")
		void testDistanciaLejana() throws MachineNotFoundException {
			// Arrange
			maquinaDAO.clear();

			Map<Producto, Integer> stockMucho = new HashMap<>();
			stockMucho.put(producto, 21); // Excede STOCK_MAXIMO (20)

			maquinaDAO.addMaquina(new Maquina(2, stockMucho, new Coordenadas(0, 0, 0), maquinaDAO));

			Map<Producto, Integer> stockBajo = new HashMap<>();
			stockBajo.put(producto, 5); // STOCK_MINIMO
			maquinaDAO.addMaquina(new Maquina(1, stockBajo, new Coordenadas(0.1, 0, 0), maquinaDAO));

			// Act & Assert
			assertEquals("DISTANCIA_EXCESIVA", maquinaDAO.sugerirDesplazamientoStock(1, producto));
		}

        @Test
		@DisplayName("MaquinaDAO - SugerirDesplazamiento: Altitud normal")
        void testAltitudAmbaBaja() throws MachineNotFoundException {
			// Arrange
            maquinaDAO.clear();

            Map<Producto, Integer> stockBajo = new HashMap<>();
            stockBajo.put(producto, Constantes.STOCK_MINIMO);
            Maquina obj = new Maquina(1, stockBajo, new Coordenadas(0, 0, 1500), maquinaDAO);
            maquinaDAO.addMaquina(obj);

            Map<Producto, Integer> stockMucho = new HashMap<>();
            stockMucho.put(producto, Constantes.STOCK_MAXIMO + 1);
            Maquina cercana = new Maquina(2, stockMucho, new Coordenadas(0.00001, 0.00001, 2000), maquinaDAO);
            maquinaDAO.addMaquina(cercana);

			    // Act & Assert
            assertEquals("TRANSFERENCIA_VIABLE",
                    maquinaDAO.sugerirDesplazamientoStock(1, producto));
        }

		@Test
		@DisplayName("MaquinaDAO - SugerirDesplazamiento: Altitud extrema")
		void testAltitudAmbaMayorQue2500() throws MachineNotFoundException {
			// Arrange
			maquinaDAO.clear();
			Map<Producto, Integer> stockBajo = new HashMap<>();
			stockBajo.put(producto, Constantes.STOCK_MINIMO);
			Maquina obj = new Maquina(1, stockBajo, new Coordenadas(0, 0, 3000), maquinaDAO);
			maquinaDAO.addMaquina(obj);

			Map<Producto, Integer> stockMucho = new HashMap<>();
			stockMucho.put(producto, Constantes.STOCK_MAXIMO + 1);
			Maquina cercana = new Maquina(2, stockMucho, new Coordenadas(0.00001, 0.00001, 2600), maquinaDAO);
			maquinaDAO.addMaquina(cercana);

			// Act & Assert
			assertEquals("REQUIERE_TRANSPORTE_ESPECIAL_ALTITUD",
					maquinaDAO.sugerirDesplazamientoStock(1, producto));
		}

		@Test
		@DisplayName("MaquinaDAO - SugerirDesplazamiento: Objetivo alto")
		void testAltitudObjetivoAlto() throws MachineNotFoundException {
			// Arrange
			maquinaDAO.clear();
			Map<Producto, Integer> stockBajo = new HashMap<>();
			stockBajo.put(producto, Constantes.STOCK_MINIMO);
			// obj > 2500 m
			Maquina obj = new Maquina(1, stockBajo, new Coordenadas(0, 0, 3000), maquinaDAO);
			maquinaDAO.addMaquina(obj);

			Map<Producto, Integer> stockMucho = new HashMap<>();
			stockMucho.put(producto, Constantes.STOCK_MAXIMO + 1);
			// cercana ≤ 2500 m, diferencia de altitud = 600 m < 1000 m (umbral de distancia)
			Maquina cercana = new Maquina(2, stockMucho, new Coordenadas(0.00001, 0.00001, 2400), maquinaDAO);
			maquinaDAO.addMaquina(cercana);

			// Act & Assert
			assertEquals("TRANSFERENCIA_VIABLE",
					maquinaDAO.sugerirDesplazamientoStock(1, producto));
		}

		@Test
		@DisplayName("MaquinaDAO - SugerirDesplazamiento: Cercana alta")
		void testAltitudCercanaAlta() throws MachineNotFoundException {
			// Arrange
			maquinaDAO.clear();
			Map<Producto, Integer> stockBajo = new HashMap<>();
			stockBajo.put(producto, Constantes.STOCK_MINIMO);
			// obj ≤ 2500 m, diferencia de altitud = 400 m < 1000 m (umbral de distancia)
			Maquina obj = new Maquina(1, stockBajo, new Coordenadas(0, 0, 2200), maquinaDAO);
			maquinaDAO.addMaquina(obj);

			Map<Producto, Integer> stockMucho = new HashMap<>();
			stockMucho.put(producto, Constantes.STOCK_MAXIMO + 1);
			// cercana > 2500 m
			Maquina cercana = new Maquina(2, stockMucho, new Coordenadas(0.00001, 0.00001, 2600), maquinaDAO);
			maquinaDAO.addMaquina(cercana);

			// Act & Assert
			assertEquals("TRANSFERENCIA_VIABLE",
					maquinaDAO.sugerirDesplazamientoStock(1, producto));
		}
	}

}