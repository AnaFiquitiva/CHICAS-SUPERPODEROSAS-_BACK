package eci.edu.dosw.proyecto.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.service.interfaces.UsuarioService;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public Usuario crearUsuario(Usuario usuario) {
        if (!usuario.isValid()) {
            throw new IllegalArgumentException("Usuario no válido: " + usuario.getValidationErrors());
        }

        if (existeUsuarioConCodigo(usuario.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un usuario con el código: " + usuario.getCodigo());
        }

        if (existeUsuarioConEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + usuario.getEmail());
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> obtenerUsuarioPorId(String id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> obtenerUsuarioPorCodigo(String codigo) {
        return usuarioRepository.findByCodigo(codigo);
    }

    @Override
    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public List<Usuario> obtenerUsuariosPorRol(RolUsuario rol) {
        return usuarioRepository.findByRol(rol);
    }

    @Override
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario actualizarUsuario(Usuario usuario) {
        if (!usuario.isValid()) {
            throw new IllegalArgumentException("Usuario no válido: " + usuario.getValidationErrors());
        }

        // Verificar que el usuario exista
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(usuario.getId());
        if (usuarioExistente.isEmpty()) {
            throw new IllegalArgumentException("No se puede actualizar. Usuario no encontrado con id: " + usuario.getId());
        }

        // Verificar que el código no esté duplicado (excluyendo el usuario actual)
        Optional<Usuario> usuarioConMismoCodigo = usuarioRepository.findByCodigo(usuario.getCodigo());
        if (usuarioConMismoCodigo.isPresent() && !usuarioConMismoCodigo.get().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Ya existe otro usuario con el código: " + usuario.getCodigo());
        }

        // Verificar que el email no esté duplicado (excluyendo el usuario actual)
        Optional<Usuario> usuarioConMismoEmail = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioConMismoEmail.isPresent() && !usuarioConMismoEmail.get().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Ya existe otro usuario con el email: " + usuario.getEmail());
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public void eliminarUsuario(String id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public boolean existeUsuarioConCodigo(String codigo) {
        return usuarioRepository.existsByCodigo(codigo);
    }

    @Override
    public boolean existeUsuarioConEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}