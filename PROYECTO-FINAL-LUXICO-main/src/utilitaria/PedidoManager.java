package utilitaria;

import negocio.*;
import java.util.ArrayList;

public class PedidoManager {

    private DatosLuxico datos;


    public PedidoManager(DatosLuxico datos) {
        this.datos = datos;
    }

    public Pedido buscarPedido(int id) {
        try {
            if (id <= 0) throw new Exception();

            for (int i = 0; i < datos.pedidos.size(); i++) {
                if (datos.pedidos.get(i).getId() == id) return datos.pedidos.get(i);
            }
            return null;

        } catch (Exception e) {
            return null;
        }
    }

    public int crearPedido(Cliente cliente, String fechaCreacion) {
        try {
            if (cliente == null) throw new Exception();
            if (fechaCreacion == null || fechaCreacion.trim().isEmpty()) throw new Exception();

            int id = datos.contadorPedidos;
            datos.contadorPedidos++;

            Pedido p = new Pedido(id, cliente, fechaCreacion);
            datos.pedidos.add(p);

            return id;

        } catch (Exception e) {
            return -1;
        }
    }


    public int crearPedido(Cliente cliente, String fechaCreacion, Producto producto, int cantidad) {
        try {
            if (cliente == null) return -1;
            if (fechaCreacion == null || fechaCreacion.trim().isEmpty()) return -1;
            if (producto == null) return -1;
            if (cantidad <= 0) return -1;

            int id = crearPedido(cliente, fechaCreacion);
            if (id <= 0) return -1;

            boolean ok = agregarProductoAPedido(id, producto, cantidad);
            if (!ok) {

                Pedido p = buscarPedido(id);
                if (p != null) {
                    for (int i = 0; i < datos.pedidos.size(); i++) {
                        if (datos.pedidos.get(i).getId() == id) { datos.pedidos.remove(i); break; }
                    }
                }
                if (datos.contadorPedidos > 0) datos.contadorPedidos--;
                return -1;
            }

            return id;

        } catch (Exception e) {
            return -1;
        }
    }

    public boolean agregarProductoAPedido(int idPedido, Producto producto, int cantidad) {
        try {
            Pedido ped = buscarPedido(idPedido);
            if (ped == null) return false;

            if (ped.getEstado() != EstadoPedido.PENDIENTE) return false;
            if (producto == null) return false;
            if (cantidad <= 0) return false;

            DetallePedido det = new DetallePedido(producto, cantidad);
            boolean ok = ped.agregarDetalle(det);
            return ok;

        } catch (Exception e) {
            return false;
        }
    }

    public void listarPedidos() {
        try {
            if (datos.pedidos.size() == 0) {
                System.out.println("No hay pedidos.");
                return;
            }

            for (int i = 0; i < datos.pedidos.size(); i++) {
                System.out.println((i + 1) + ") " + datos.pedidos.get(i));
            }

        } catch (Exception e) {
            System.out.println("Error listando pedidos.");
        }
    }

    public void verDetallePedido(int idPedido) {
        try {
            Pedido ped = buscarPedido(idPedido);
            if (ped == null) {
                System.out.println("Pedido no encontrado.");
                return;
            }

            System.out.println(ped);

            if (ped.getDetalles().size() == 0) {
                System.out.println("El pedido no tiene productos.");
                return;
            }

            for (int i = 0; i < ped.getDetalles().size(); i++) {
                System.out.println((i + 1) + ") " + ped.getDetalles().get(i));
            }

        } catch (Exception e) {
            System.out.println("Error mostrando pedido.");
        }
    }

    public boolean confirmarPedido(int idPedido) {
        try {
            Pedido ped = buscarPedido(idPedido);
            if (ped == null) return false;

            if (ped.getEstado() != EstadoPedido.PENDIENTE) return false;
            if (ped.getDetalles().size() == 0) return false;

            for (int i = 0; i < ped.getDetalles().size(); i++) {
                DetallePedido d = ped.getDetalles().get(i);

                Producto prod = d.getProducto();
                if (prod == null) return false;

                if (d.getCantidad() > prod.getStock()) return false;
            }

            for (int i = 0; i < ped.getDetalles().size(); i++) {
                DetallePedido d = ped.getDetalles().get(i);
                d.getProducto().descontarStock(d.getCantidad());
            }

            ped.setEstado(EstadoPedido.CONFIRMADO);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean cambiarEstadoPedido(int idPedido, EstadoPedido nuevoEstado) {
        try {
            Pedido ped = buscarPedido(idPedido);
            if (ped == null) return false;
            if (nuevoEstado == null) return false;

            EstadoPedido actual = ped.getEstado();

            if (actual == EstadoPedido.PENDIENTE) return false;
            if (actual == EstadoPedido.CONFIRMADO && nuevoEstado != EstadoPedido.EN_PREPARACION) return false;
            if (actual == EstadoPedido.EN_PREPARACION && nuevoEstado != EstadoPedido.ENVIADO) return false;
            if (actual == EstadoPedido.ENVIADO) return false;

            ped.setEstado(nuevoEstado);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean prepararPedido(int idPedido) {
        try {
            Pedido p = buscarPedido(idPedido);
            if (p == null) return false;

            if (p.getEstado() != EstadoPedido.CONFIRMADO) return false;

            p.setEstado(EstadoPedido.EN_PREPARACION);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean eliminarPedido(int idPedido) {
        try {
            Pedido p = buscarPedido(idPedido);
            if (p == null) return false;

            for (int i = 0; i < datos.pedidos.size(); i++) {
                if (datos.pedidos.get(i).getId() == idPedido) {
                    datos.pedidos.remove(i);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public ArrayList<Pedido> getPedidos() {
        return new ArrayList<>(datos.pedidos);
    }
}
