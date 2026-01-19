package utilitaria;

import java.util.ArrayList;
import negocio.*;

public class DatosLuxico {
    public ArrayList<Usuario> usuarios;
    public ArrayList<Producto> productos;
    public ArrayList<Cliente> clientes;
    public ArrayList<Pedido> pedidos;
    public ArrayList<MovimientoInventario> movimientos;
    public int contadorPedidos;

    public DatosLuxico() {
        usuarios = new ArrayList<>();
        productos = new ArrayList<>();
        clientes = new ArrayList<>();
        pedidos = new ArrayList<>();
        movimientos = new ArrayList<>();
        contadorPedidos = 1;
    }
}