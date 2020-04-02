package az.gdg.msauth.security.service.impl;

import az.gdg.msauth.dao.UserRepository;
import az.gdg.msauth.model.entity.UserEntity;
import az.gdg.msauth.exception.WrongDataException;
import az.gdg.msauth.security.model.dto.JwtAuthenticationRequest;
import az.gdg.msauth.security.model.dto.JwtAuthenticationResponse;
import az.gdg.msauth.security.model.dto.UserInfo;
import az.gdg.msauth.security.exception.AuthenticationException;
import az.gdg.msauth.security.service.AuthenticationService;
import az.gdg.msauth.security.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final TokenUtil tokenUtil;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    public AuthenticationServiceImpl(TokenUtil tokenUtil,
                                     UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.tokenUtil = tokenUtil;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    public JwtAuthenticationResponse createAuthenticationToken(JwtAuthenticationRequest request) {
        logger.info("ActionLog.CreateAuthenticationToken.Start");

        authenticate(request.getEmail(), request.getPassword());
        UserEntity userEntity = userRepository.findByEmail(request.getEmail());

        if (userEntity != null && userEntity.getStatus().toString().equals("CONFIRMED")) {
            String userId = userEntity.getId().toString();
            String role = userEntity.getRole().toString();
            String status = userEntity.getStatus().toString();
            String token = tokenUtil.generateToken(request.getEmail(),userId,role,status);

            logger.info("ActionLog.CreateAuthenticationToken.Stop.Success");
            return new JwtAuthenticationResponse(token);
        } else {
            logger.info("ActionLog.CreateAuthenticationToken.Stop.WrongDataException.Thrown");
            throw new WrongDataException("Email is not registered or you are not confirmed by admins");
        }

    }

    public void authenticate(String username, String password) {
        logger.info("ActionLog.Authenticate.Start");
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException e) {
            logger.error("ActionLog.AuthenticationException.Bad Credentials.Thrown");

            throw new AuthenticationException("Bad credentials", e);
        }

        logger.info("ActionLog.Authenticate.Stop.Success");
    }

    public UserInfo validateToken(String token) {
        logger.info("ActionLog.ValidateToken.Start");
        tokenUtil.isTokenValid(token);
        logger.info("ActionLog.ValidateToken.Stop.Success");

        return tokenUtil.getUserInfoFromToken(token);
    }


}
