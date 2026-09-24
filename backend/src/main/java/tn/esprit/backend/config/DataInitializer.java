package tn.esprit.backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.backend.entity.Role;
import tn.esprit.backend.entity.RoleName;
import tn.esprit.backend.entity.User;
import tn.esprit.backend.repository.RoleRepository;
import tn.esprit.backend.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        Role roleUser = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_USER).build()));

        Role roleAdmin = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_ADMIN).build()));

        if (Boolean.FALSE.equals(userRepository.existsByUsername("admin"))) {
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(roleUser);
            adminRoles.add(roleAdmin);

            User admin = User.builder()
                    .username("admin")
                    .email("admin@gestionprojets.tn")
                    .password(passwordEncoder.encode("Admin123!"))
                    .roles(adminRoles)
                    .build();

            userRepository.save(admin);
            log.info("Compte Administrateur initialisé avec succès : admin / Admin123!");
        }

        if (Boolean.FALSE.equals(userRepository.existsByUsername("user"))) {
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(roleUser);

            User standardUser = User.builder()
                    .username("user")
                    .email("user@gestionprojets.tn")
                    .password(passwordEncoder.encode("User123!"))
                    .roles(userRoles)
                    .build();

            userRepository.save(standardUser);
            log.info("Compte Utilisateur initialisé avec succès : user / User123!");
        }
    }
}
