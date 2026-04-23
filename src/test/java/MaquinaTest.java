import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import es.usc.enso.mvprjgrp31.Coordenadas;
import es.usc.enso.mvprjgrp31.Maquina;

public class MaquinaTest {
	@Test
    void testEqualsReflexivo() {
        Maquina m = new Maquina(1, new HashMap<>(), new Coordenadas(68.98,27.124,500.85));
        assertEquals(m, m);
    }

    @Test
    void testEqualsMismoContenido() {
    	Maquina m1 = new Maquina(1, new HashMap<>(), new Coordenadas(68.98,27.124,500.85));
    	Maquina m2 = new Maquina(1, new HashMap<>(), new Coordenadas(68.98,27.124,500.85));

        assertEquals(m1, m2);
    }

    @Test
    void testNotEquals() {
    	Maquina m1 = new Maquina(1, new HashMap<>(), new Coordenadas(68.98,27.124,500.85));
    	Maquina m2 = new Maquina(2, new HashMap<>(), new Coordenadas(68.98,27.124,500.85));

        assertNotEquals(m1, m2);
    }

    @Test
    void testEqualsNull() {
    	Maquina m = new Maquina(1, new HashMap<>(), new Coordenadas(68.98,27.124,500.85));
        assertNotEquals(null, m);
    }
}
