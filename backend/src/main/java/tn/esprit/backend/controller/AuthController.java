package tn.esprit.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.backend.dto.AuthRequestDTO;
import tn.esprit.backend.dto.AuthResponseDTO;
import tn.esprit.backend.dto.RegisterRequestDTO;
import tn.esprit.backend.entity.Role;
import tn.esprit.backend.entity.RoleName;
import tn.esprit.backend.entity.User;
import tn.esprit.backend.repository.RoleRepository;
import tn.esprit.backend.repository.UserRepository;
import tn.esprit.backend.security.JwtUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping({"/api/auth", "/auth"})
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Gestion de l'authentification et de l'enregistrement des utilisateurs")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur et émission du jeton JWT")
    public ResponseEntity<AuthResponseDTO> authenticateUser(@Valid @RequestBody AuthRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String jwt = jwtUtils.generateToken(loginRequest.username(), roles);

        return ResponseEntity.ok(new AuthResponseDTO(jwt, loginRequest.username(), roles));
    }

    @PostMapping("/register")
    @Operation(summary = "Inscription d'un nouvel utilisateur avec le rôle standard ROLE_USER")
    public ResponseEntity<AuthResponseDTO> registerUser(@Valid @RequestBody RegisterRequestDTO signUpRequest) {
        if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.username()))) {
            throw new IllegalArgumentException("Le nom d'utilisateur est déjà utilisé !");
        }

        if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.email()))) {
            throw new IllegalArgumentException("L'adresse email est déjà enregistrée !");
        }

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_USER).build()));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .username(signUpRequest.username())
                .email(signUpRequest.email())
                .password(passwordEncoder.encode(signUpRequest.password()))
                .roles(roles)
                .build();

        userRepository.save(user);

        List<String> roleNames = List.of(RoleName.ROLE_USER.name());
        String jwt = jwtUtils.generateToken(user.getUsername(), roleNames);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponseDTO(jwt, user.getUsername(), roleNames));
    }
}
