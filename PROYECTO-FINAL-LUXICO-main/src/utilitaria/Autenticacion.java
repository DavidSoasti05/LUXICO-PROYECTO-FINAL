package utilitaria;

import negocio.Usuario;
import java.util.ArrayList;

public class Autenticacion {
    private ArrayList<Usuario> usuarios;

    public Autenticacion(ArrayList<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Usuario login(String user, String pass) {
        try {
            if (user == null || pass == null) return null;

            for (int i = 0; i < usuarios.size(); i++) {
                Usuario u = usuarios.get(i);
                if (u.getUsuario().equals(user) && u.getClave().equals(pass)) {
                    return u;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}