package tn.esprit.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.backend.dto.AuthRequestDTO;
import tn.esprit.backend.dto.RegisterRequestDTO;
import tn.esprit.backend.entity.Role;
import tn.esprit.backend.entity.RoleName;
import tn.esprit.backend.entity.User;
import tn.esprit.backend.repository.RoleRepository;
import tn.esprit.backend.repository.UserRepository;
import tn.esprit.backend.security.AuthAccessDeniedHandler;
import tn.esprit.backend.security.AuthEntryPointJwt;
import tn.esprit.backend.security.JwtAuthenticationFilter;
import tn.esprit.backend.security.JwtUtils;
import tn.esprit.backend.security.UserDetailsServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RoleRepository roleRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private AuthEntryPointJwt authEntryPointJwt;

    @MockitoBean
    private AuthAccessDeniedHandler authAccessDeniedHandler;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("POST /api/auth/login should return 200 OK and token on successful authentication")
    void testLoginSuccess() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("admin", "Admin123!");
        Authentication authentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER")))
                .when(authentication).getAuthorities();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateToken(eq("admin"), anyList()))
                .thenReturn("mocked.jwt.token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked.jwt.token"))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("POST /api/auth/login should return 401 Unauthorized on invalid credentials")
    void testLoginBadCredentials() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("admin", "WrongPass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Identifiants invalides"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Non Authentifié"));
    }

    @Test
    @DisplayName("POST /api/auth/register should return 201 Created when data is valid")
    void testRegisterSuccess() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("newuser", "newuser@test.tn", "Password123!");
        Role userRole = Role.builder().id(1L).name(RoleName.ROLE_USER).build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@test.tn")).thenReturn(false);
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtils.generateToken(eq("newuser"), anyList())).thenReturn("new.jwt.token");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("new.jwt.token"))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }

    @Test
    @DisplayName("POST /api/auth/register should return 400 Bad Request when username already exists")
    void testRegisterDuplicateUsername() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("admin", "unique@test.tn", "Password123!");

        when(userRepository.existsByUsername("admin")).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Requête Invalide"));
    }

    @Test
    @DisplayName("POST /api/auth/register should return 400 Bad Request when email already exists")
    void testRegisterDuplicateEmail() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("uniqueUser", "admin@gestionprojets.tn", "Password123!");

        when(userRepository.existsByUsername("uniqueUser")).thenReturn(false);
        when(userRepository.existsByEmail("admin@gestionprojets.tn")).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Requête Invalide"));
    }

    @Test
    @DisplayName("POST /api/auth/register should return 400 Bad Request with invalidParams on invalid payload")
    void testRegisterValidationFailure() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("", "not-an-email", "123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erreur de Validation"))
                .andExpect(jsonPath("$.invalidParams.username").exists())
                .andExpect(jsonPath("$.invalidParams.email").exists())
                .andExpect(jsonPath("$.invalidParams.password").exists());
    }
}
