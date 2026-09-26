package SistemaTransferencia.mateus999.auth;

public record AuthResponse(String accessToken, String tokenType, long expiresIn) {
}
