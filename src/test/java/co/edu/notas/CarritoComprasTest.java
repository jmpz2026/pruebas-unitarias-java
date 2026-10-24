package co.edu.notas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * ENTREGA DEL TALLER
 * Nombre completo: Junior Miguel Pinzon Santa
 * 1. Bug encontrado (método y línea de CarritoCompras.java):
 *    agregarProducto, línea 24: la condición "cantidad < 0" permitía agregar
 *    productos con cantidad 0. Se corrigió a "cantidad <= 0".
 * 2. Prueba que lo detectó (nombre del método):
 *    agregarProducto_cantidadCero_lanzaExcepcion
 * 3. Sugerencia de la IA que rechacé o corregí, y por qué:
 *    La propuesta inicial solo cubría cantidad negativa; la amplié con el caso
 *    cantidad 0 porque R1 exige cantidad mayor a 0 y el código la aceptaba.
 *    Verifiqué a mano cada valor esperado con las reglas R1–R6.
 */
class CarritoComprasTest {

    private CarritoCompras carrito;

    @BeforeEach
    void setUp() {
        carrito = new CarritoCompras();
    }

    // ==================== R1: validación de productos ====================

    @Test
    @DisplayName("Rechaza un producto con nombre nulo")
    void agregarProducto_nombreNulo_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto(null, 10_000, 1));

        assertEquals("El nombre es obligatorio", ex.getMessage());
    }

    @Test
    @DisplayName("Rechaza un producto con nombre vacío")
    void agregarProducto_nombreVacio_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("", 10_000, 1));

        assertEquals("El nombre es obligatorio", ex.getMessage());
    }

    @Test
    @DisplayName("Rechaza un producto con nombre en blanco")
    void agregarProducto_nombreEnBlanco_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("   ", 10_000, 1));

        assertEquals("El nombre es obligatorio", ex.getMessage());
    }

    @Test
    @DisplayName("Rechaza un producto con precio cero")
    void agregarProducto_precioCero_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Cuaderno", 0, 1));

        assertEquals("El precio debe ser positivo", ex.getMessage());
    }

    @Test
    @DisplayName("Rechaza un producto con precio negativo")
    void agregarProducto_precioNegativo_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Cuaderno", -1, 1));

        assertEquals("El precio debe ser positivo", ex.getMessage());
    }

    @Test
    @DisplayName("Rechaza un producto con cantidad cero")
    void agregarProducto_cantidadCero_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Cuaderno", 8_000, 0));

        assertEquals("La cantidad debe ser mayor a cero", ex.getMessage());
    }

    @Test
    @DisplayName("Rechaza un producto con cantidad negativa")
    void agregarProducto_cantidadNegativa_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Cuaderno", 8_000, -1));

        assertEquals("La cantidad debe ser mayor a cero", ex.getMessage());
    }

    @Test
    @DisplayName("Agrega un producto con datos válidos")
    void agregarProducto_datosValidos_agregaItem() {
        carrito.agregarProducto("Cuaderno", 8_000, 3);

        assertEquals(1, carrito.cantidadDeItems());
        assertEquals(24_000, carrito.calcularSubtotal(), 0.001);
    }

    // ==================== R2: subtotal y carrito vacío ====================

    @Test
    @DisplayName("Un carrito vacío tiene subtotal cero")
    void calcularSubtotal_carritoVacio_retornaCero() {
        assertEquals(0, carrito.calcularSubtotal(), 0.001);
    }

    @Test
    @DisplayName("Un carrito vacío tiene total cero")
    void calcularTotal_carritoVacio_retornaCero() {
        assertEquals(0, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Subtotal = precio × cantidad de cada producto")
    void calcularSubtotal_variosProductos_retornaSumaCorrecta() {
        // Arrange
        carrito.agregarProducto("Cuaderno", 8_000, 3);
        carrito.agregarProducto("Esfero", 2_000, 5);

        // Act
        double subtotal = carrito.calcularSubtotal();

        // Assert
        assertEquals(34_000, subtotal, 0.001);
    }

    // ==================== R3: cupón SENA10 ====================

    @Test
    @DisplayName("SENA10 aplica un 10 % de descuento sobre el subtotal")
    void calcularDescuento_cuponSena10_retornaDiezPorCiento() {
        carrito.agregarProducto("Cuaderno", 8_000, 3);
        carrito.agregarProducto("Esfero", 2_000, 5);
        carrito.aplicarCupon("SENA10");

        assertEquals(3_400, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("SENA10 calcula el total con descuento e IVA")
    void calcularTotal_cuponSena10_retornaTotalConDescuento() {
        carrito.agregarProducto("Cuaderno", 8_000, 3);
        carrito.agregarProducto("Esfero", 2_000, 5);
        carrito.aplicarCupon("SENA10");

        assertEquals(48_414, carrito.calcularTotal(), 0.001);
    }

    // ==================== R4: cupón BIENVENIDO ====================

    @Test
    @DisplayName("BIENVENIDO descuenta $5.000 justo en el subtotal de $50.000")
    void calcularDescuento_bienvenidoSubtotalExacto50Mil_retornaCincoMil() {
        carrito.agregarProducto("Producto", 50_000, 1);
        carrito.aplicarCupon("BIENVENIDO");

        assertEquals(5_000, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("BIENVENIDO no descuenta con subtotal de $49.999")
    void calcularDescuento_bienvenidoSubtotal49999_retornaCero() {
        carrito.agregarProducto("Producto", 49_999, 1);
        carrito.aplicarCupon("BIENVENIDO");

        assertEquals(0, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("BIENVENIDO en $50.000 genera un total de $65.550")
    void calcularTotal_bienvenidoSubtotalExacto50Mil_retorna65550() {
        carrito.agregarProducto("Producto", 50_000, 1);
        carrito.aplicarCupon("BIENVENIDO");

        assertEquals(65_550, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("BIENVENIDO en $49.999 no descuenta y el total es $71.499")
    void calcularTotal_bienvenidoSubtotal49999_retorna71499() {
        carrito.agregarProducto("Producto", 49_999, 1);
        carrito.aplicarCupon("BIENVENIDO");

        assertEquals(71_499, carrito.calcularTotal(), 0.001);
    }

    // ==================== R5: cupones inválidos ====================

    @Test
    @DisplayName("Rechaza un cupón que no existe")
    void aplicarCupon_cuponInvalido_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carrito.aplicarCupon("OTRO"));

        assertEquals("Cupón inválido: OTRO", ex.getMessage());
    }

    @Test
    @DisplayName("Rechaza un cupón nulo")
    void aplicarCupon_cuponNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> carrito.aplicarCupon(null));
    }

    // ==================== R6: IVA, envío y total ====================

    @Test
    @DisplayName("Un libro de $100.000 paga envío y cuesta $131.000")
    void calcularTotal_sinCuponEnvioPagado_retorna131000() {
        carrito.agregarProducto("Libro", 100_000, 1);

        assertEquals(131_000, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Un monitor de $200.000 supera el mínimo y no paga envío")
    void calcularTotal_sinCuponEnvioGratis_retorna238000() {
        carrito.agregarProducto("Monitor", 200_000, 1);

        assertEquals(238_000, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Un monitor de $500.000 con SENA10 cuesta $535.500 con envío gratis")
    void calcularTotal_sena10EnvioGratis_retorna535500() {
        carrito.agregarProducto("Monitor", 500_000, 1);
        carrito.aplicarCupon("SENA10");

        assertEquals(535_500, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Un libro de $100.000 con SENA10 cuesta $119.100 con envío")
    void calcularTotal_sena10EnvioPagado_retorna119100() {
        carrito.agregarProducto("Libro", 100_000, 1);
        carrito.aplicarCupon("SENA10");

        assertEquals(119_100, carrito.calcularTotal(), 0.001);
    }
}
