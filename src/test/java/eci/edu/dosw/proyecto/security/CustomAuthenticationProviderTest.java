package eci.edu.dosw.proyecto.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para CustomAuthenticationProvider.
 * Utiliza Mockito para simular las dependencias UserDetailsService y PasswordEncoder.
 */
@ExtendWith(MockitoExtension.class)
class CustomAuthenticationProviderTest {

    // --- Dependencias Mockeadas ---
    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    // --- Clase bajo Prueba ---
    // Mockito creará una instancia de CustomAuthenticationProvider e inyectará los mocks anteriores.
    @InjectMocks
    private CustomAuthenticationProvider customAuthenticationProvider;

    // --- Datos de Prueba ---
    private Authentication authentication;
    private final String username = "testuser";
    private final String rawPassword = "password123";
    private final String encodedPassword = "encoded_password_hash";
    private final List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

    /**
     * Método que se ejecuta antes de cada prueba.
     * Sirve para inicializar los objetos en un estado limpio y conocido.
     */
    @BeforeEach
    void setUp() {
        // Simulamos el objeto de autenticación que llega desde el cliente (ej. un formulario de login)
        authentication = new UsernamePasswordAuthenticationToken(username, rawPassword);
    }

    @Test
    @DisplayName("Camino Feliz: Debería autenticar exitosamente un usuario con credenciales válidas")
    void deberiaAutenticarExitosamenteConCredencialesValidas() {
        // Arrange: Configuramos los mocks para simular un escenario exitoso.
        UserDetails mockUser = User.builder()
                .username(username)
                .password(encodedPassword) // La contraseña almacenada siempre está codificada
                .authorities(authorities)
                .build();

        when(userDetailsService.loadUserByUsername(username)).thenReturn(mockUser);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        // Act: Ejecutamos el método que queremos probar.
        Authentication result = customAuthenticationProvider.authenticate(authentication);

        // Assert: Verificamos que el resultado es el esperado.
        assertNotNull(result, "La autenticación no debería ser nula");
        assertTrue(result.isAuthenticated(), "El usuario debería estar autenticado");
        assertEquals(mockUser, result.getPrincipal(), "El principal debe ser el objeto UserDetails");
        assertNull(result.getCredentials(), "Las credenciales no deben persistir en el token tras un login exitoso");
        assertEquals(authorities, result.getAuthorities(), "Las autoridades deben ser las del usuario");

        // Verificamos que los mocks fueron llamados exactamente como esperábamos.
        verify(userDetailsService, times(1)).loadUserByUsername(username);
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }

    @Test
    @DisplayName("Caso de Fallo: Debería lanzar BadCredentialsException si el usuario no existe")
    void deberiaLanzarExcepcionSiUsuarioNoExiste() {
        // Arrange: Configuramos el mock para que devuelva null, simulando que el usuario no fue encontrado.
        when(userDetailsService.loadUserByUsername(username)).thenReturn(null);

        // Act & Assert: Verificamos que se lanza la excepción correcta.
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            customAuthenticationProvider.authenticate(authentication);
        });

        assertEquals("Credenciales inválidas", exception.getMessage());

        // Verificamos que se intentó cargar el usuario, pero NUNCA se llegó a comprobar la contraseña.
        verify(userDetailsService, times(1)).loadUserByUsername(username);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Caso de Fallo: Debería lanzar BadCredentialsException si la contraseña es incorrecta")
    void deberiaLanzarExcepcionSiContrasenaIncorrecta() {
        // Arrange: El usuario existe, pero la contraseña no coincide.
        UserDetails mockUser = User.builder()
                .username(username)
                .password(encodedPassword)
                .authorities(authorities)
                .build();

        when(userDetailsService.loadUserByUsername(username)).thenReturn(mockUser);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false); // Simulamos que la contraseña no coincide

        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            customAuthenticationProvider.authenticate(authentication);
        });

        assertEquals("Credenciales inválidas", exception.getMessage());

        // En este caso, ambos métodos sí fueron llamados.
        verify(userDetailsService, times(1)).loadUserByUsername(username);
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }

    @Test
    @DisplayName("Debería soportar la autenticación de tipo UsernamePasswordAuthenticationToken")
    void deberiaSoportarUsernamePasswordAuthenticationToken() {
        // Act
        boolean supports = customAuthenticationProvider.supports(UsernamePasswordAuthenticationToken.class);

        // Assert
        assertTrue(supports, "Debe soportar el tipo de autenticación para el que fue diseñado");
    }

    @Test
    @DisplayName("No debería soportar otros tipos de autenticación")
    void noDeberiaSoportarOtrosTiposDeAutenticacion() {
        // Act
        boolean supports = customAuthenticationProvider.supports(Authentication.class);

        // Assert
        assertFalse(supports, "No debe soportar tipos de autenticación genéricos o desconocidos");
    }
}