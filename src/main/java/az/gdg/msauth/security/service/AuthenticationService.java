package az.gdg.msauth.security.service;

import az.gdg.msauth.security.model.dto.JwtAuthenticationRequest;
import az.gdg.msauth.security.model.dto.JwtAuthenticationResponse;
import az.gdg.msauth.security.model.dto.UserInfo;

public interface AuthenticationService {

    public JwtAuthenticationResponse createAuthenticationToken(JwtAuthenticationRequest request);

    public void authenticate(String username, String password);

    public UserInfo validateToken(String token);
}
