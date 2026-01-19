package servicio;

import utilitaria.DatosLuxico;
import negocio.*;

import java.util.ArrayList;

public class Storage {
    private GestorArchivos gestor;

    private final String RUTA_USUARIOS = "usuarios.dat";
    private final String RUTA_PRODUCTOS = "productos.dat";
    private final String RUTA_CLIENTES = "clientes.dat";
    private final String RUTA_PEDIDOS = "pedidos.dat";
    private final String RUTA_MOVIMIENTOS = "movimientos.dat";
    private final String RUTA_CONTADOR = "contador.dat";

    public Storage() {
        gestor = new GestorArchivos();
    }

    public DatosLuxico cargarTodo() {
        DatosLuxico d = new DatosLuxico();

        try {
            Object objU = gestor.cargarObjeto(RUTA_USUARIOS);
            if (objU != null) d.usuarios = (ArrayList<Usuario>) objU;

            Object objP = gestor.cargarObjeto(RUTA_PRODUCTOS);
            if (objP != null) d.productos = (ArrayList<Producto>) objP;

            Object objC = gestor.cargarObjeto(RUTA_CLIENTES);
            if (objC != null) d.clientes = (ArrayList<Cliente>) objC;

            Object objPe = gestor.cargarObjeto(RUTA_PEDIDOS);
            if (objPe != null) d.pedidos = (ArrayList<Pedido>) objPe;

            Object objM = gestor.cargarObjeto(RUTA_MOVIMIENTOS);
            if (objM != null) d.movimientos = (ArrayList<MovimientoInventario>) objM;

            Object objCont = gestor.cargarObjeto(RUTA_CONTADOR);
            if (objCont != null) d.contadorPedidos = (Integer) objCont;

            if (d.usuarios == null) d.usuarios = new ArrayList<>();
            if (d.usuarios.size() == 0) {
                d.usuarios.add(new Admin("admin", "admin123", "Administrador"));
                d.usuarios.add(new Bodega("bodega", "bodega123", "Usuario Bodega"));
                d.usuarios.add(new Ventas("ventas", "ventas123", "Usuario Ventas"));
                guardarTodo(d);
            }

        } catch (Exception e) {
            System.out.println("Error cargando datos. Se inicia vacio.");
        }

        if (d.usuarios == null) d.usuarios = new ArrayList<>();
        if (d.productos == null) d.productos = new ArrayList<>();
        if (d.clientes == null) d.clientes = new ArrayList<>();
        if (d.pedidos == null) d.pedidos = new ArrayList<>();
        if (d.movimientos == null) d.movimientos = new ArrayList<>();
        if (d.contadorPedidos <= 0) d.contadorPedidos = 1;

        return d;
    }

    public void guardarTodo(DatosLuxico d) {
        try {
            gestor.guardarObjeto(RUTA_USUARIOS, d.usuarios);
            gestor.guardarObjeto(RUTA_PRODUCTOS, d.productos);
            gestor.guardarObjeto(RUTA_CLIENTES, d.clientes);
            gestor.guardarObjeto(RUTA_PEDIDOS, d.pedidos);
            gestor.guardarObjeto(RUTA_MOVIMIENTOS, d.movimientos);
            gestor.guardarObjeto(RUTA_CONTADOR, d.contadorPedidos);
        } catch (Exception e) {
            System.out.println("Error guardando datos.");
        }
    }

    public void resetSistema(DatosLuxico d) {
        try {
            gestor.eliminarArchivo(RUTA_USUARIOS);
            gestor.eliminarArchivo(RUTA_PRODUCTOS);
            gestor.eliminarArchivo(RUTA_CLIENTES);
            gestor.eliminarArchivo(RUTA_PEDIDOS);
            gestor.eliminarArchivo(RUTA_MOVIMIENTOS);
            gestor.eliminarArchivo(RUTA_CONTADOR);

            d.usuarios = new ArrayList<>();
            d.productos = new ArrayList<>();
            d.clientes = new ArrayList<>();
            d.pedidos = new ArrayList<>();
            d.movimientos = new ArrayList<>();
            d.contadorPedidos = 1;

            d.usuarios.add(new Admin("admin", "admin123", "Administrador"));
            d.usuarios.add(new Bodega("bodega", "bodega123", "Usuario Bodega"));
            d.usuarios.add(new Ventas("ventas", "ventas123", "Usuario Ventas"));

            guardarTodo(d);
        } catch (Exception e) {
            System.out.println("Error reiniciando sistema.");
        }
    }
}