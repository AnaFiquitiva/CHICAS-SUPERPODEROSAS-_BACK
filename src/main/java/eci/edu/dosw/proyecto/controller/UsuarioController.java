package eci.edu.dosw.proyecto.controller;
import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.exception.UserServiceException;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.service.interfaces.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-service")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/users")
    public List<Usuario> users() {
        return usuarioService.obtenerTodosLosUsuarios();
    }

    @GetMapping("/users/{userId}")
    public Usuario user(@PathVariable String userId) throws UserServiceException {
        return usuarioService.obtenerUsuarioPorId(userId)
                .orElseThrow(() -> new UserServiceException("Usuario no encontrado"));
    }

    @PostMapping("/register")
    public Usuario user(@RequestBody UsuarioDTO user) {
        return usuarioService.registrarUsuario(user);
    }

    @DeleteMapping("/users/{userId}")
    public List<Usuario> deleteUser(@PathVariable String userId) throws UserServiceException {
        usuarioService.eliminarUsuario(userId);
        return usuarioService.obtenerTodosLosUsuarios();
    }

    @PostMapping("/login")
    public AuthenticationResponseDTO authenticate(@RequestBody UserAuthenticationDTO authenticationDTO) {
        return usuarioService.autenticar(authenticationDTO);
    }
}