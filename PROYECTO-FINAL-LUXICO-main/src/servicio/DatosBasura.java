package servicio;

import utilitaria.DatosLuxico;
import negocio.*;

import java.util.ArrayList;

public class DatosBasura {

    public static DatosLuxico cargar() {

        DatosLuxico d = new DatosLuxico();

        // Por si tu constructor NO inicializa listas
        d.usuarios = new ArrayList<>();
        d.clientes = new ArrayList<>();
        d.productos = new ArrayList<>();
        d.pedidos = new ArrayList<>();
        d.movimientos = new ArrayList<>();

        // =========================
        // USUARIOS
        // =========================
        d.usuarios.add(new Admin("admin", "admin123", "Administrador"));
        d.usuarios.add(new Bodega("bodega", "bodega123", "Usuario Bodega"));
        d.usuarios.add(new Ventas("ventas", "ventas123", "Usuario Ventas"));

        // =========================
        // CLIENTES (12)
        // =========================

        Cliente c1  = new Cliente("1710034065", "Juan Morales", "Quito Centro", "0991112233", "juan@gmail.com");
        Cliente c2  = new Cliente("1721962783", "Mateo Morales", "Carcelen", "0997778899", "mateo@icloud.com");
        Cliente c3  = new Cliente("1712412210", "Valeria Ruiz", "La Carolina", "0988889900", "valeria@gmail.com");
        Cliente c4  = new Cliente("1720055233", "Daniela Mena", "Inaquito", "0991415161", "daniela@gmail.com");
        Cliente c5  = new Cliente("1714567896", "Maria Perez", "Quito Norte", "0982223344", "maria@outlook.com");
        Cliente c6  = new Cliente("1712412202", "Carlos Heredia", "Cumbaya", "0993334455", "carlos@hotmail.com");
        Cliente c7  = new Cliente("1710034057", "Ana Simbaña", "Tumbaco", "0984445566", "ana@gmail.com");
        Cliente c8  = new Cliente("1721962775", "Diego Ramirez", "Valle de Los Chillos", "0995556677", "diego@outlook.com");
        Cliente c9  = new Cliente("1720055225", "Sofia Lopez", "Quito Sur", "0986667788", "sofia@gmail.com");
        Cliente c10 = new Cliente("1719876540", "Paul Cabrera", "Pomasqui", "0991011121", "paul@hotmail.com");
        Cliente c11 = new Cliente("1712412194", "Alejandro Holguin", "Calderon", "0981213141", "ale@outlook.com");
        Cliente c12 = new Cliente("1710034040", "Jorge Almeida", "Centro Historico", "0981617181", "jorge@hotmail.com");

        d.clientes.add(c1);  d.clientes.add(c2);  d.clientes.add(c3);  d.clientes.add(c4);
        d.clientes.add(c5);  d.clientes.add(c6);  d.clientes.add(c7);  d.clientes.add(c8);
        d.clientes.add(c9);  d.clientes.add(c10); d.clientes.add(c11); d.clientes.add(c12);

        // =========================
        // PRODUCTOS (20)
        // =========================
        Producto p1  = new ProductoRegular("P001", "Jordan 1 High",          "42", 120.0, 12, 3);
        Producto p2  = new ProductoRegular("P002", "Nike Dunk Low",          "41", 95.5,  8, 2);
        Producto p3  = new ProductoEdicionLimitada("P003","Yeezy 350 V2",    "43", 250.0, 3, 1);
        Producto p4  = new ProductoRegular("P004", "Air Force 1",            "40", 110.0, 10, 3);
        Producto p5  = new ProductoRegular("P005", "Converse Chuck 70",      "39", 75.0,  15, 4);
        Producto p6  = new ProductoEdicionLimitada("P006","Jordan 4 Retro",  "42", 280.0, 2, 1);
        Producto p7  = new ProductoRegular("P007", "Adidas Forum",           "41", 90.0,  9, 2);
        Producto p8  = new ProductoRegular("P008", "Puma Suede",             "40", 65.0,  14, 4);
        Producto p9  = new ProductoRegular("P009", "New Balance 550",        "42", 130.0, 6, 2);
        Producto p10 = new ProductoEdicionLimitada("P010","Nike SB Dunk",    "41", 220.0, 3, 1);
        Producto p11 = new ProductoRegular("P011", "Reebok Club C",          "40", 70.0,  11, 3);
        Producto p12 = new ProductoRegular("P012", "Vans Old Skool",         "39", 60.0,  16, 5);
        Producto p13 = new ProductoEdicionLimitada("P013","Travis x Jordan", "42", 450.0, 1, 1);
        Producto p14 = new ProductoRegular("P014", "Adidas Superstar",       "41", 85.0,  10, 3);
        Producto p15 = new ProductoRegular("P015", "Nike Blazer Mid",        "40", 92.0,  7, 2);
        Producto p16 = new ProductoRegular("P016", "Asics Gel Lyte",         "42", 105.0, 8, 2);
        Producto p17 = new ProductoEdicionLimitada("P017","Off-White AF1",   "41", 520.0, 1, 1);
        Producto p18 = new ProductoRegular("P018", "Fila Disruptor",         "39", 78.0,  9, 2);
        Producto p19 = new ProductoRegular("P019", "Skechers Go Walk",       "40", 55.0,  18, 6);
        Producto p20 = new ProductoEdicionLimitada("P020","Jordan 11 Retro", "43", 360.0, 2, 1);

        d.productos.add(p1);  d.productos.add(p2);  d.productos.add(p3);  d.productos.add(p4);  d.productos.add(p5);
        d.productos.add(p6);  d.productos.add(p7);  d.productos.add(p8);  d.productos.add(p9);  d.productos.add(p10);
        d.productos.add(p11); d.productos.add(p12); d.productos.add(p13); d.productos.add(p14); d.productos.add(p15);
        d.productos.add(p16); d.productos.add(p17); d.productos.add(p18); d.productos.add(p19); d.productos.add(p20);

        // =========================
        // MOVIMIENTOS (10 variados)
        // Nota: aqui solo se guardan como historial.
        // =========================
        d.movimientos.add(new MovimientoInventario(p1,  TipoMovimiento.ENTRADA,   5, "2026-01-10", "Reposicion proveedor"));
        d.movimientos.add(new MovimientoInventario(p2,  TipoMovimiento.SALIDA,    2, "2026-01-11", "Venta mostrador"));
        d.movimientos.add(new MovimientoInventario(p4,  TipoMovimiento.ENTRADA,   8, "2026-01-12", "Stock inicial sucursal"));
        d.movimientos.add(new MovimientoInventario(p6,  TipoMovimiento.SALIDA,    1, "2026-01-13", "Pedido confirmado"));
        d.movimientos.add(new MovimientoInventario(p3,  TipoMovimiento.DEVOLUCION,1, "2026-01-14", "Devolucion por talla"));
        d.movimientos.add(new MovimientoInventario(p10, TipoMovimiento.ENTRADA,   2, "2026-01-15", "Llegada edicion limitada"));
        d.movimientos.add(new MovimientoInventario(p12, TipoMovimiento.SALIDA,    3, "2026-01-16", "Venta en linea"));
        d.movimientos.add(new MovimientoInventario(p9,  TipoMovimiento.ENTRADA,   4, "2026-01-17", "Reposicion rapida"));
        d.movimientos.add(new MovimientoInventario(p15, TipoMovimiento.SALIDA,    1, "2026-01-18", "Muestra (salida interna)"));
        d.movimientos.add(new MovimientoInventario(p19, TipoMovimiento.ENTRADA,   6, "2026-01-19", "Compra mayorista"));

        // =========================
        // PEDIDOS (10 variados)
        // =========================
        d.contadorPedidos = 1;

        Pedido ped1 = new Pedido(d.contadorPedidos++, c1, "2026-01-20");
        ped1.agregarDetalle(new DetallePedido(p1, 1));
        ped1.agregarDetalle(new DetallePedido(p2, 2));
        ped1.setEstado(EstadoPedido.PENDIENTE);
        d.pedidos.add(ped1);

        Pedido ped2 = new Pedido(d.contadorPedidos++, c2, "2026-01-21");
        ped2.agregarDetalle(new DetallePedido(p4, 1));
        ped2.setEstado(EstadoPedido.CONFIRMADO);
        d.pedidos.add(ped2);

        Pedido ped3 = new Pedido(d.contadorPedidos++, c3, "2026-01-21");
        ped3.agregarDetalle(new DetallePedido(p5, 2));
        ped3.agregarDetalle(new DetallePedido(p12, 1));
        ped3.setEstado(EstadoPedido.EN_PREPARACION);
        d.pedidos.add(ped3);

        Pedido ped4 = new Pedido(d.contadorPedidos++, c4, "2026-01-22");
        ped4.agregarDetalle(new DetallePedido(p3, 1));
        ped4.setEstado(EstadoPedido.ENVIADO);
        d.pedidos.add(ped4);

        Pedido ped5 = new Pedido(d.contadorPedidos++, c5, "2026-01-22");
        ped5.agregarDetalle(new DetallePedido(p8, 1));
        ped5.agregarDetalle(new DetallePedido(p11, 1));
        ped5.setEstado(EstadoPedido.CONFIRMADO);
        d.pedidos.add(ped5);

        Pedido ped6 = new Pedido(d.contadorPedidos++, c6, "2026-01-23");
        ped6.agregarDetalle(new DetallePedido(p9, 1));
        ped6.agregarDetalle(new DetallePedido(p7, 1));
        ped6.setEstado(EstadoPedido.PENDIENTE);
        d.pedidos.add(ped6);

        Pedido ped7 = new Pedido(d.contadorPedidos++, c7, "2026-01-23");
        ped7.agregarDetalle(new DetallePedido(p13, 1));
        ped7.setEstado(EstadoPedido.EN_PREPARACION);
        d.pedidos.add(ped7);

        Pedido ped8 = new Pedido(d.contadorPedidos++, c8, "2026-01-24");
        ped8.agregarDetalle(new DetallePedido(p14, 2));
        ped8.agregarDetalle(new DetallePedido(p15, 1));
        ped8.setEstado(EstadoPedido.ENVIADO);
        d.pedidos.add(ped8);

        Pedido ped9 = new Pedido(d.contadorPedidos++, c9, "2026-01-24");
        ped9.agregarDetalle(new DetallePedido(p19, 3));
        ped9.setEstado(EstadoPedido.CONFIRMADO);
        d.pedidos.add(ped9);

        Pedido ped10 = new Pedido(d.contadorPedidos++, c10, "2026-01-25");
        ped10.agregarDetalle(new DetallePedido(p20, 1));
        ped10.agregarDetalle(new DetallePedido(p10, 1));
        ped10.setEstado(EstadoPedido.PENDIENTE);
        d.pedidos.add(ped10);

        return d;
    }
}
