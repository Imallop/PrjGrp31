package es.usc.enso.mvprjgrp31;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
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

		when(maquinaA.getCoordenadas()).thenReturn(coordA);
		when(maquinaB.getCoordenadas()).thenReturn(coordB);
		when(maquinaC.getCoordenadas()).thenReturn(coordC);

		maquinaDAO.addMaquina(maquinaA);
		maquinaDAO.addMaquina(maquinaB);
		maquinaDAO.addMaquina(maquinaC);

		// Act
		Maquina resultado = maquinaDAO.getMaquinaCercana(origen);

		// Assert
		assertAll(
				() -> assertSame(maquinaA, resultado),
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

		// Act
		MachineNotFoundException ex = assertThrows(
				MachineNotFoundException.class,
				() -> maquinaDAO.getMaquinaCercana(origen));

		// Assert
		assertTrue(ex.getMessage().contains("Machine not found near coordinates"));
		assertNotNull(ex);
	}

    @Test
    void getMaquinasDevuelveCopia() {
        MaquinaDAO dao = MaquinaDAO.getInstance();

        ArrayList<Maquina> lista1 = dao.getMaquinas();
        ArrayList<Maquina> lista2 = dao.getMaquinas();

        assertNotSame(lista1, lista2);
    }

    @Test
    void tiempoMaximoCalcularReposicion(){
        HashMap<Producto,Integer> stock = new HashMap<>();
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
}
