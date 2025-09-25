package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.model.*;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    Usuario crearUsuario(Usuario usuario);
    Optional<Usuario> obtenerUsuarioPorId(String id);
    Optional<Usuario> obtenerUsuarioPorCodigo(String codigo);
    Optional<Usuario> obtenerUsuarioPorEmail(String email);
    List<Usuario> obtenerUsuariosPorRol(RolUsuario rol);
    List<Usuario> obtenerTodosLosUsuarios();
    Usuario actualizarUsuario(Usuario usuario);
    void eliminarUsuario(String id);
    boolean existeUsuarioConCodigo(String codigo);
    boolean existeUsuarioConEmail(String email);
}
