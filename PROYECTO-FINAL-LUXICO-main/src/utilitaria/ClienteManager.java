package utilitaria;

import java.util.ArrayList;

import negocio.Cliente;
import servicio.Storage;

public class ClienteManager {

    private DatosLuxico datos;
    private Storage persistencia;

    public ClienteManager(DatosLuxico datos, Storage persistencia) {
        this.datos = datos;
        this.persistencia = persistencia;
    }

    public Cliente buscarCliente(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) return null;
        cedula = cedula.trim();

        for (int i = 0; i < datos.clientes.size(); i++) {
            Cliente c = datos.clientes.get(i);
            if (c.getCedula().equals(cedula)) return c;
        }
        return null;
    }

    public boolean registrarCliente(Cliente c) {
        if (c == null) return false;
        try {
            if (!c.esValido()) return false;
        } catch (Exception e) {
        }

        if (buscarCliente(c.getCedula()) != null) return false;

        datos.clientes.add(c);
        persistencia.guardarTodo(datos);
        return true;
    }


    public void listarClientes() {
        if (datos.clientes.size() == 0) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        for (int i = 0; i < datos.clientes.size(); i++) {
            System.out.println((i + 1) + ") " + datos.clientes.get(i));
        }
    }

    public void verDetalleCliente(String cedula) {
        Cliente c = buscarCliente(cedula);
        if (c == null) {
            System.out.println("Cliente no encontrado.");
            return;
        }

        System.out.println("Cedula: " + c.getCedula());
        System.out.println("Nombre: " + c.getNombre());
        System.out.println("Direccion: " + c.getDireccion());
        System.out.println("Telefono: " + c.getTelefono());
        System.out.println("Correo: " + c.getCorreo());
    }

    public boolean editarCliente(String cedula, String nuevoNombre, String nuevaDireccion,
                                 String nuevoTelefono, String nuevoCorreo) {

        Cliente c = buscarCliente(cedula);
        if (c == null) return false;

        boolean ok = true;

        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            try { ok = c.setNombre(nuevoNombre) && ok; }
            catch (Exception e) { c.setNombre(nuevoNombre); }
        }

        if (nuevaDireccion != null && !nuevaDireccion.trim().isEmpty()) {
            try { ok = c.setDireccion(nuevaDireccion) && ok; }
            catch (Exception e) { c.setDireccion(nuevaDireccion); }
        }

        if (nuevoTelefono != null && !nuevoTelefono.trim().isEmpty()) {
            try { ok = c.setTelefono(nuevoTelefono) && ok; }
            catch (Exception e) { c.setTelefono(nuevoTelefono); }
        }

        if (nuevoCorreo != null && !nuevoCorreo.trim().isEmpty()) {
            try { ok = c.setCorreo(nuevoCorreo) && ok; }
            catch (Exception e) { c.setCorreo(nuevoCorreo); }
        }

        persistencia.guardarTodo(datos);
        return ok;
    }

    public boolean eliminarCliente(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) return false;
        cedula = cedula.trim();

        for (int i = 0; i < datos.clientes.size(); i++) {
            if (datos.clientes.get(i).getCedula().equals(cedula)) {
                datos.clientes.remove(i);
                persistencia.guardarTodo(datos);
                return true;
            }
        }
        return false;
    }

    public ArrayList<Cliente> getClientes() {
        return new ArrayList<>(datos.clientes);
    }
}