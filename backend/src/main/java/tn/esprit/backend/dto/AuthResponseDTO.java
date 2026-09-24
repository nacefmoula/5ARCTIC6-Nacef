package tn.esprit.backend.dto;

import java.util.List;

public record AuthResponseDTO(
    String token,
    String type,
    String username,
    List<String> roles
) {
    public AuthResponseDTO(String token, String username, List<String> roles) {
        this(token, "Bearer", username, roles);
    }
}
