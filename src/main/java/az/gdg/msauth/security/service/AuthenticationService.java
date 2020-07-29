package az.gdg.msauth.security.service;

import az.gdg.msauth.security.model.dto.JwtAuthenticationRequest;
import az.gdg.msauth.security.model.dto.JwtAuthenticationResponse;
import az.gdg.msauth.security.model.dto.UserInfo;

public interface AuthenticationService {

    JwtAuthenticationResponse createAuthenticationToken(JwtAuthenticationRequest request);

    void authenticate(String username, String password);

    UserInfo validateToken(String token);
}
