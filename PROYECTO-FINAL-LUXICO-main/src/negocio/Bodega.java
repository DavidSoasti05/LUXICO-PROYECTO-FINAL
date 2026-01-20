package negocio;

public class Bodega extends Usuario  {
    
    public Bodega(String usuario, String clave, String nombre) {
        super(usuario, clave, nombre);
    }

    @Override
    public RolUsuario getRol() {
        return RolUsuario.BODEGA;
    }
}
