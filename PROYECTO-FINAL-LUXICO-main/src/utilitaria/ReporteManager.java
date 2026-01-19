package utilitaria;

import negocio.*;

public class ReporteManager {
    private DatosLuxico datos;

    public ReporteManager(DatosLuxico datos) {
        this.datos = datos;
    }

    public void reporteStockCritico() {
        try {
            boolean hay = false;
            System.out.println("=== REPORTE: STOCK CRITICO ===");

            for (int i = 0; i < datos.productos.size(); i++) {
                Producto p = datos.productos.get(i);
                if (p.estaBajoMinimo()) {
                    hay = true;
                    System.out.println("- " + p.getCodigo() + " | " + p.getModelo()
                            + " | Stock: " + p.getStock() + " | Tipo: " + p.getTipo());
                }
            }
            if (!hay) System.out.println("No hay productos en nivel critico.");
        } catch (Exception e) {
            System.out.println("Error reporte stock.");
        }
    }

    public void reportePedidosPorEstado(EstadoPedido estado) {
        try {
            if (estado == null) return;

            boolean hay = false;
            System.out.println("=== REPORTE: PEDIDOS " + estado + " ===");

            for (int i = 0; i < datos.pedidos.size(); i++) {
                Pedido p = datos.pedidos.get(i);
                if (p.getEstado() == estado) {
                    hay = true;
                    System.out.println(p);
                }
            }
            if (!hay) System.out.println("No existen pedidos en ese estado.");
        } catch (Exception e) {
            System.out.println("Error reporte pedidos.");
        }
    }

    public void reporteVentasResumen() {
        try {
            if (datos.pedidos.size() == 0) {
                System.out.println("No hay pedidos registrados.");
                return;
            }

            double totalVendido = 0;
            String[] codigos = new String[200];
            int[] cantidades = new int[200];
            int n = 0;

            for (int i = 0; i < datos.pedidos.size(); i++) {
                Pedido p = datos.pedidos.get(i);

                if (p.getEstado() == EstadoPedido.PENDIENTE) continue;

                totalVendido += p.getTotal();

                for (int j = 0; j < p.getDetalles().size(); j++) {
                    DetallePedido d = p.getDetalles().get(j);

                    int idx = -1;
                    for (int k = 0; k < n; k++) {
                        if (codigos[k].equals(d.getCodigoProducto())) {
                            idx = k;
                            break;
                        }
                    }

                    if (idx == -1) {
                        codigos[n] = d.getCodigoProducto();
                        cantidades[n] = d.getCantidad();
                        n++;
                    } else {
                        cantidades[idx] += d.getCantidad();
                    }
                }
            }

            System.out.println("=== REPORTE: RESUMEN DE VENTAS ===");
            System.out.println("Total vendido (sin pendientes): $" + totalVendido);

            if (n == 0) {
                System.out.println("No hay productos vendidos aun.");
                return;
            }

            int max = cantidades[0];
            int pos = 0;
            for (int i = 1; i < n; i++) {
                if (cantidades[i] > max) {
                    max = cantidades[i];
                    pos = i;
                }
            }

            System.out.println("Producto mas vendido: " + codigos[pos] + " | Cantidad: " + max);
            for (int i = 0; i < n; i++) {
                System.out.println("- " + codigos[i] + " | Cantidad vendida: " + cantidades[i]);
            }

        } catch (Exception e) {
            System.out.println("Error reporte ventas.");
        }
    }
}