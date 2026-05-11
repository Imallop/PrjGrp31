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
    @DisplayName("add y get dan la misma")
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
    @DisplayName("getMaquina por id (csv)")
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
    @DisplayName("getMaquina falla si no encuentra (con fail)")
    void getMaquinaNoExisteConFail() {
        try {
            maquinaDAO.getMaquina(99);
            fail("Se esperaba MachineNotFoundException y no se lanzó ninguna excepción");
        } catch (MachineNotFoundException ex) {
            assertTrue(ex.getMessage().contains("Machine not found"));
            assertNull(ex.getCause());
        } catch (Exception ex) {
            fail("Se lanzó una excepción incorrecta: " + ex.getClass().getSimpleName());
        }
    }

	@Test
	@DisplayName("getMaquinaCercana devuelve máquina más cercana")
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
	@DisplayName("cercana sin maquinas")
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
    void getMaquinasDevuelveCopia() {
        MaquinaDAO maquinaDAO = MaquinaDAO.getInstance();

        ArrayList<Maquina> lista1 = maquinaDAO.getMaquinas();
        ArrayList<Maquina> lista2 = maquinaDAO.getMaquinas();

        assertNotSame(lista1, lista2);
    }

    @Test
    void tiempoMaximoCalcularReposicion() {
        HashMap<Producto, Integer> stock = new HashMap<>();
        Producto chocolate = new Producto("Chocolate", (float) 25.0, 1);
        Producto kitkat = new Producto("KitKat", (float) 30.0, 2);
        Producto bocata = new Producto("Bocata", (float) 40.0, 3);

        stock.put(chocolate, Constantes.STOCK_MINIMO);
        stock.put(kitkat, Constantes.STOCK_MINIMO - 3);
        stock.put(bocata, 17);

        Maquina maquina = new Maquina(1, stock, new Coordenadas(0.0, 0.0, 0.0), maquinaDAO);
        maquinaDAO.addMaquina(maquina);

        maquina.recarga(new ArrayList<>(maquina.consultarReposiciones().keySet()));

        assertTimeoutPreemptively(Duration.ofMillis(10), () -> {
            maquinaDAO.calcularProximaReposicion(1).entrySet();
        });
    }
 
	@Test
	void d9_calcularReposicion_unRegistro() {
		Producto chocolate = new Producto("Chocolate", (float) 25.0, 1);
		Instant ahora = Instant.now();
		maquinaDAO.registrarReposicion(1, chocolate, ahora);
 
		// El bucle itera una vez (TRUE una vez, luego FALSE al acabar)
		Instant proxima = maquinaDAO.calcularReposicionProducto(1, chocolate);
 
		assertNotNull(proxima, "Con un registro el resultado no debe ser null");
		assertTrue(proxima.isAfter(ahora),
				"La próxima reposición estimada debe estar después del último registro conocido");
	}
 
	@Test
	@DisplayName("D9-MANY: lista con varios registros → bucle itera N veces y calcula la media correctamente")
	void d9_calcularReposicion_variosRegistros() {
		Producto chocolate = new Producto("Chocolate", (float) 25.0, 1);
		Instant t1 = Instant.parse("2025-01-01T10:00:00Z");
		Instant t2 = Instant.parse("2025-01-08T10:00:00Z"); // +7 días
		Instant t3 = Instant.parse("2025-01-15T10:00:00Z"); // +7 días
		maquinaDAO.registrarReposicion(1, chocolate, t1);
		maquinaDAO.registrarReposicion(1, chocolate, t2);
		maquinaDAO.registrarReposicion(1, chocolate, t3);
 
		// El bucle itera 3 veces; la media entre reposiciones es 7 días
		Instant proxima = maquinaDAO.calcularReposicionProducto(1, chocolate);
 
		Instant esperada = Instant.parse("2025-01-22T10:00:00Z"); // t3 + 7 días
		long toleranciaMs = 60_000L; // ±1 minuto por aritmética entera
		assertTrue(
				Math.abs(proxima.toEpochMilli() - esperada.toEpochMilli()) < toleranciaMs,
				"La próxima reposición debe ser aproximadamente t3 + media_entre_reposiciones (7 días)");
	}

	@Test
	@DisplayName("Debe lanzar Exception si la máquina objetivo no existe")
	void testMaquinaNoExiste() {
		assertThrows(MachineNotFoundException.class, () ->
			maquinaDAO.sugerirDesplazamientoStock(999, producto));
	}

	@Test
	@DisplayName("Retorna STOCK_SUFICIENTE si el stock > STOCK_MINIMO")
	void testStockSuficiente() throws MachineNotFoundException {
		Map<Producto, Integer> stock = new HashMap<>();
		stock.put(producto, Constantes.STOCK_MINIMO + 1); // 6
		Maquina m = new Maquina(1, stock, new Coordenadas(0, 0, 0), maquinaDAO);
		maquinaDAO.addMaquina(m);

		assertEquals("STOCK_SUFICIENTE", maquinaDAO.sugerirDesplazamientoStock(1, producto));
	}

    @Nested
    @DisplayName("Escenarios de Máquina Cercana y Transferencia")
    class EscenariosTransferencia {

        @BeforeEach
        void prepararMaquinaObjetivoEnMinimos() {
            Map<Producto, Integer> stockBajo = new HashMap<>();
            stockBajo.put(producto, Constantes.STOCK_MINIMO); // 5
            Maquina obj = new Maquina(1, stockBajo, new Coordenadas(0, 0, 0), maquinaDAO);
            maquinaDAO.addMaquina(obj);
        }

        @Test
        @DisplayName("Retorna SIN_PROVEEDOR_CERCANO si solo existe la máquina objetivo")
        void testSinOtrasMaquinas() throws MachineNotFoundException {
            assertEquals("SIN_PROVEEDOR_CERCANO", maquinaDAO.sugerirDesplazamientoStock(1, producto));
        }

        @Test
        @DisplayName("Retorna SIN_PROVEEDOR_CERCANO si la cercana no tiene excedente")
        void testCercanaSinExcedente() throws MachineNotFoundException {
            Map<Producto, Integer> stockNormal = new HashMap<>();
            stockNormal.put(producto, Constantes.STOCK_MAXIMO); // 20

            // Situada a 100m
            Maquina cercana = new Maquina(2, stockNormal, new Coordenadas(0.0009, 0, 0), maquinaDAO);
            maquinaDAO.addMaquina(cercana);

            assertEquals("SIN_PROVEEDOR_CERCANO", maquinaDAO.sugerirDesplazamientoStock(1, producto));
        }

        @Test
		@DisplayName("Retorna DISTANCIA_EXCESIVA si hay excedente pero está lejos (>1000m)")
		void testDistanciaLejana() throws MachineNotFoundException {
			maquinaDAO.clear();

			Map<Producto, Integer> stockMucho = new HashMap<>();
			stockMucho.put(producto, 21); // Excede STOCK_MAXIMO (20)

			Maquina lejana = new Maquina(2, stockMucho, new Coordenadas(0.1, 0, 0), maquinaDAO);
			maquinaDAO.addMaquina(lejana);

			Map<Producto, Integer> stockBajo = new HashMap<>();
			stockBajo.put(producto, 5); // STOCK_MINIMO
			Maquina obj = new Maquina(1, stockBajo, new Coordenadas(0, 0, 0), maquinaDAO);
			maquinaDAO.addMaquina(obj);

			maquinaDAO.clear();
			maquinaDAO.addMaquina(new Maquina(2, stockMucho, new Coordenadas(0, 0, 0), maquinaDAO));
			maquinaDAO.addMaquina(new Maquina(1, stockBajo, new Coordenadas(0.1, 0, 0), maquinaDAO));

			assertEquals("DISTANCIA_EXCESIVA", maquinaDAO.sugerirDesplazamientoStock(1, producto));
		}

        @Test
		@DisplayName("Retorna TRANSFERENCIA_VIABLE si hay excedente y está cerca (<1000m)")
		void testTransferenciaOk() throws MachineNotFoundException {
			// 1. Limpiamos para asegurar que no hay interferencias
			maquinaDAO.clear();

			// 2. Máquina Objetivo (ID: 1) en (0,0,0)
			Map<Producto, Integer> stockBajo = new HashMap<>();
			stockBajo.put(producto, Constantes.STOCK_MINIMO); // 5
			Maquina obj = new Maquina(1, stockBajo, new Coordenadas(0, 0, 0), maquinaDAO);
			maquinaDAO.addMaquina(obj);

			// 3. Máquina con Excedente (ID: 2) MUY cerca
			Map<Producto, Integer> stockMucho = new HashMap<>();
			stockMucho.put(producto, Constantes.STOCK_MAXIMO + 1); // 21
			Maquina cercana = new Maquina(2, stockMucho, new Coordenadas(0.00001, 0.00001, 0), maquinaDAO);
			maquinaDAO.addMaquina(cercana);

			assertEquals("TRANSFERENCIA_VIABLE", maquinaDAO.sugerirDesplazamientoStock(1, producto));
		}
	}

}