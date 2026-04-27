package es.usc.enso.mvprjgrp31;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MemoriaTest {
	private Memoria memoria;

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
		memoria = new Memoria();
	}

	@AfterEach
	void tearDown() {
		memoria = null;
	}

	@Test
	@DisplayName("add y get dan la misma")
	void addYGetMismaInstancia() throws Exception {
		// Arragne
		when(maquinaMock.getId()).thenReturn(7);

		// Act
		memoria.addMaquina(maquinaMock);
		Maquina resultado = assertTimeout(Duration.ofMillis(100), () -> memoria.getMaquina(7));

		// Assert
		assertAll(
				() -> assertSame(maquinaMock, resultado),
				() -> assertEquals(7, resultado.getId()),
				() -> assertNotNull(resultado)
		);
	}

	@ParameterizedTest(name = "id {0} -> pos {1}")
	@DisplayName("getMaquina por id (csv)")
	@CsvSource({
			"1, 0",
			"2, 1",
			"3, 2"
	})
	void getMaquinaCsv(int id, int index) throws Exception {
		// Arragne
		Maquina[] maquinas = new Maquina[] {
				new Maquina(1, new HashMap<>(), new Coordenadas(0.0, 0.0, 0.0)),
				new Maquina(2, new HashMap<>(), new Coordenadas(1.0, 1.0, 0.0)),
				new Maquina(3, new HashMap<>(), new Coordenadas(2.0, 2.0, 0.0))
		};
		for (Maquina m : maquinas) {
			memoria.addMaquina(m);
		}

		// Act
		Maquina encontrada = memoria.getMaquina(id);

		// Assert
		assertAll(
				() -> assertSame(maquinas[index], encontrada),
				() -> assertTrue(encontrada.getId() == id)
		);
	}

	@Test
	@DisplayName("getMaquina falla si no encuentra")
	void getMaquinaNoExiste() {
		// Arragne
		// Act
		MachineNotFoundException ex = assertThrows(
				MachineNotFoundException.class,
				() -> memoria.getMaquina(99)
		);

		// Assert
		assertTrue(ex.getMessage().contains("Machine not found"));
		assertNull(ex.getCause());
	}

	@Test
	@DisplayName("getMaquinaCercana devuelve máquina más cercana")
	void cercanaDevuelveMasCerca() throws Exception {
		// Arragne
		Coordenadas origen = new Coordenadas(0.0, 0.0, 0.0);
		Coordenadas coordA = new Coordenadas(0.0001, 0.0, 0.0);
		Coordenadas coordB = new Coordenadas(0.01, 0.0, 0.0);
		Coordenadas coordC = new Coordenadas(1.0, 1.0, 0.0);

		when(maquinaA.getCoordenadas()).thenReturn(coordA);
		when(maquinaB.getCoordenadas()).thenReturn(coordB);
		when(maquinaC.getCoordenadas()).thenReturn(coordC);

		memoria.addMaquina(maquinaA);
		memoria.addMaquina(maquinaB);
		memoria.addMaquina(maquinaC);

		// Act
		Maquina resultado = memoria.getMaquinaCercana(origen);

		// Assert
		assertAll(
				() -> assertSame(maquinaA, resultado),
				() -> assertArrayEquals(
						coordA.getCoordenadas(),
						resultado.getCoordenadas().getCoordenadas(),
						0.000001
				),
				() -> assertTrue(
						Coordenadas.distancia(origen, coordA)
								< Coordenadas.distancia(origen, coordB)
				)
		);
	}

	@Test
	@DisplayName("cercana sin maquinas")
	void cercanaSinMaquinas() {
		// Arragne
		Coordenadas origen = new Coordenadas(0.0, 0.0, 0.0);

		// Act
		MachineNotFoundException ex = assertThrows(
				MachineNotFoundException.class,
				() -> memoria.getMaquinaCercana(origen)
		);

		// Assert
		assertTrue(ex.getMessage().contains("Machine not found near coordinates"));
		assertNotNull(ex);
	}
}
