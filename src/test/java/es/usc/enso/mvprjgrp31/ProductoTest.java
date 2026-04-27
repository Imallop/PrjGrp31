package es.usc.enso.mvprjgrp31;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import es.usc.enso.mvprjgrp31.Producto;

class ProductoTest {

    @Test
    void testEqualsReflexivo() {
        Producto p = new Producto("Chocolate", (float) 25.0, 1);
        assertEquals(p, p);
    }

    @Test
    void testEqualsMismoContenido() {
    	Producto p1 = new Producto("Chocolate", (float) 25.0, 1);
    	Producto p2 = new Producto("Chocolate", (float) 25.0, 1);

        assertEquals(p1, p2);
    }

    @Test
    void testNotEquals() {
        Producto p1 = new Producto("Chocolate", (float)25.0, 1);
        Producto p2 = new Producto("KitKat", (float)30.0, 2);

        assertNotEquals(p1, p2);
    }

    @Test
    void testEqualsNull() {
    	Producto p = new Producto("Chocolate", (float) 25.0, 1);
        assertNotEquals(null, p);
    }
}