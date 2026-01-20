package utilitaria;

import negocio.*;
import java.util.ArrayList;

public class ProductoManager {

    private DatosLuxico datos;

    public ProductoManager(DatosLuxico datos) {
        this.datos = datos;
    }

    public boolean agregarProducto(Producto p) {
        try {
            if (p == null) throw new Exception();

            for (int i = 0; i < datos.productos.size(); i++) {
                if (datos.productos.get(i).getCodigo().equals(p.getCodigo())) return false;
            }

            datos.productos.add(p);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean eliminarProducto(Producto p) {
        if (p == null) return false;

        for (int i = 0; i < datos.productos.size(); i++) {
            if (datos.productos.get(i) == p) {
                datos.productos.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean contieneProducto(Producto p) {
        try {
            if (p == null) return false;

            for (int i = 0; i < datos.productos.size(); i++) {
                if (datos.productos.get(i) == p) return true;
            }
            return false;

        } catch (Exception e) {
            return false;
        }
    }

    public void listarProductos() {
        try {
            if (datos.productos.size() == 0) {
                System.out.println("No hay productos registrados.");
                return;
            }

            for (int i = 0; i < datos.productos.size(); i++) {
                Producto p = datos.productos.get(i);
                System.out.println((i + 1) + ") " + p.getCodigo() + " | " + p.getModelo()
                        + " | Talla: " + p.getTalla()
                        + " | Stock: " + p.getStock()
                        + " | Tipo: " + p.getTipo());
            }

        } catch (Exception e) {
            System.out.println("Error al listar productos.");
        }
    }

    public boolean registrarMovimiento(Producto producto, TipoMovimiento tipo, int cantidad, String fecha, String motivo) {
        try {
            if (producto == null) throw new Exception();
            if (tipo == null) throw new Exception();
            if (cantidad <= 0) throw new Exception();

            boolean ok = false;
            if (tipo == TipoMovimiento.ENTRADA) ok = producto.aumentarStock(cantidad);
            else if (tipo == TipoMovimiento.SALIDA) ok = producto.descontarStock(cantidad);
            else if (tipo == TipoMovimiento.DEVOLUCION) ok = producto.aumentarStock(cantidad);

            if (!ok) return false;

            datos.movimientos.add(new MovimientoInventario(producto, tipo, cantidad, fecha, motivo));
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public void listarMovimientos() {
        try {
            if (datos.movimientos.size() == 0) {
                System.out.println("No hay movimientos registrados.");
                return;
            }

            for (int i = 0; i < datos.movimientos.size(); i++) {
                System.out.println((i + 1) + ") " + datos.movimientos.get(i));
            }

        } catch (Exception e) {
            System.out.println("Error al listar movimientos.");
        }
    }

    public void listarAlertasStockMinimo() {
        try {
            boolean hay = false;

            for (int i = 0; i < datos.productos.size(); i++) {
                Producto p = datos.productos.get(i);
                if (p.estaBajoMinimo()) {
                    hay = true;
                    System.out.println("- " + p.getCodigo() + " | " + p.getModelo() + " | Stock: " + p.getStock());
                }
            }

            if (!hay) System.out.println("No hay productos en nivel critico.");

        } catch (Exception e) {
            System.out.println("Error alertas stock.");
        }
    }

    public ArrayList<Producto> getProductos() {
        return new ArrayList<>(datos.productos);
    }

    public ArrayList<MovimientoInventario> getMovimientos() {
        return new ArrayList<>(datos.movimientos);
    }
}
