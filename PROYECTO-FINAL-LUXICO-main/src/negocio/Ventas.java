package negocio;

public class Ventas extends Usuario  {

    public Ventas(String usuario, String clave, String nombre) {
        super(usuario, clave, nombre);
    }

    @Override
    public RolUsuario getRol() {
        return RolUsuario.VENTAS;
    }
}
