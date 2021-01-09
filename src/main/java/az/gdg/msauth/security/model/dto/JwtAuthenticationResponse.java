package az.gdg.msauth.security.model.dto;

import lombok.Getter;

@Getter
public class JwtAuthenticationResponse {

    private final String access;
    private final String refresh;

    public JwtAuthenticationResponse(String access, String refresh) {
        this.access = access;
        this.refresh = refresh;
    }
}