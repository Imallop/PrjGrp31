import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.usc.enso.mvprjgrp31.Constantes;
import es.usc.enso.mvprjgrp31.Coordenadas;
import es.usc.enso.mvprjgrp31.Maquina;
import es.usc.enso.mvprjgrp31.Producto;

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
    
    @Test
    @DisplayName("Consultar Stock")
    void testConsultarStock() {
    	
    }
    
    @Test
    @DisplayName("Actualizar Stock")
    void testActualizarStock() {
    	HashMap<Producto,Integer> stock = new HashMap<>();
    	Producto chocolate = new Producto("Chocolate", (float) 25.0, 1);
    	stock.put(chocolate, Constantes.STOCK_MAXIMO);
    	Maquina m = new Maquina(1, stock, new Coordenadas(68.98,27.124,500.85));
    	m.venta("Chocolate");
    	
    	assertTrue(m.consultarStock().get(chocolate) == 19);
    }
    
    @Test
    @DisplayName("Elemento no existente, venta")
    void testNoSuchElement() {
    	HashMap<Producto,Integer> stock = new HashMap<>();
    	Maquina m = new Maquina(1, stock, new Coordenadas(68.98,27.124,500.85));
    	assertThrows(NoSuchElementException.class, () -> m.venta("Chocolate"));
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
    	
    	Maquina m = new Maquina(1, stock, new Coordenadas(68.98,27.124,500.85));
    	Map<Producto,Integer> reposiciones = m.consultarReposiciones();
    	
    	assertFalse(reposiciones.containsKey(bocata));
    	assertEquals(2, reposiciones.size());
    }
}













