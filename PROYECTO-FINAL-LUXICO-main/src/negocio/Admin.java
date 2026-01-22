package negocio;

public class Admin extends Usuario  {

    public Admin(String usuario, String clave, String nombre) {
        super(usuario, clave, nombre);
    }

    @Override
    public RolUsuario getRol() {
        return RolUsuario.ADMIN;
    }
}
