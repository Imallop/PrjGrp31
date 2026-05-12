package es.usc.enso.mvprjgrp31;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductoTest {

    @Test
    @DisplayName("Producto - Equals: Reflexivo")
    void testEqualsReflexivo() {
        // Arrange
        Producto p = new Producto("Chocolate", (float) 25.0, 1);
        // Act & Assert
        assertEquals(p, p);
    }

    @Test
    @DisplayName("Producto - Equals: Mismo contenido")
    void testEqualsMismoContenido() {
        // Arrange
        Producto p1 = new Producto("Chocolate", (float) 25.0, 1);
        Producto p2 = new Producto("Chocolate", (float) 25.0, 1);

        // Act & Assert
        assertEquals(p1, p2);
    }

    @Test
    @DisplayName("Producto - Equals: Distinto contenido")
    void testNotEquals() {
        // Arrange
        Producto p1 = new Producto("Chocolate", (float) 25.0, 1);
        Producto p2 = new Producto("KitKat", (float) 30.0, 2);

        // Act & Assert
        assertNotEquals(p1, p2);
    }

    @Test
    @DisplayName("Producto - Equals: Distinta clase")
    void testNotSameClass() {
        // Arrange
        Producto p1 = new Producto("Chocolate", (float) 25.0, 1);
        ArrayList<Producto> p2 = new ArrayList<>();

        // Act & Assert
        assertNotEquals(p1, p2);
    }

    @Test
    @DisplayName("Producto - Equals: Mismo id distinto contenido")
    void testEqualsMismoIdDistintoContenido() {
        // Arrange
        Producto p1 = new Producto("Chocolate", (float) 25.0, 1);
        Producto p2 = new Producto("Agua", (float) 1.0, 1);

        // Act & Assert
        assertEquals(p1, p2);
    }

    @Test
    @DisplayName("Producto - Equals: Null")
    void testEqualsNull() {
        // Arrange
        Producto p = new Producto("Chocolate", (float) 25.0, 1);
        // Act & Assert
        assertNotEquals(null, p);
    }
}