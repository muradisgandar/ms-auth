package az.gdg.msauth.security.service.impl;

import az.gdg.msauth.dao.UserRepository;
import az.gdg.msauth.exception.NotFoundException;
import az.gdg.msauth.exception.WrongDataException;
import az.gdg.msauth.model.entity.UserEntity;
import az.gdg.msauth.security.exception.AuthenticationException;
import az.gdg.msauth.security.model.dto.JwtAuthenticationRequest;
import az.gdg.msauth.security.model.dto.JwtAuthenticationResponse;
import az.gdg.msauth.security.model.dto.UserInfo;
import az.gdg.msauth.security.service.AuthenticationService;
import az.gdg.msauth.security.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    private final TokenUtil tokenUtil;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public AuthenticationServiceImpl(TokenUtil tokenUtil,
                                     UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.tokenUtil = tokenUtil;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    public JwtAuthenticationResponse createAuthenticationToken(JwtAuthenticationRequest request) {
        logger.info("ServiceLog.createAuthenticationToken.start.mail : {}", request.getMail());

        authenticate(request.getMail(), request.getPassword());
        UserEntity userEntity = userRepository.findByMail(request.getMail());

        if (userEntity != null) {

            switch (userEntity.getStatus().toString()) {
                case "CONFIRMED":
                    String userId = userEntity.getId().toString();
                    String role = userEntity.getRole().toString();
                    String status = userEntity.getStatus().toString();
                    String token = tokenUtil.generateToken(request.getMail(), userId, role, status);

                    logger.info("ServiceLog.createAuthenticationToken.stop.success.mail : {}", request.getMail());
                    return new JwtAuthenticationResponse(token);
                case "REGISTERED":
                    throw new AuthenticationException("Your registration is not verified," +
                            " please check your mail for verification link which has been sent");
                case "BLOCKED":
                    throw new AuthenticationException("Your account has been blocked by admins, please contact us");
                default:

            }


        } else {
            throw new NotFoundException("Incorrect login credentials!");
        }

        return null;
    }

    public void authenticate(String username, String password) {
        logger.info("ServiceLog.authenticate.start.username : {}", username);

        if (username != null && password != null) {
            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            } catch (BadCredentialsException e) {
                throw new AuthenticationException("Incorrect login credentials!", e);
            }
        } else {
            throw new WrongDataException("Username or Password is null!");
        }

        logger.info("ServiceLog.authenticate.stop.success.username : {}", username);
    }

    public UserInfo validateToken(String token) {
        logger.info("ServiceLog.validateToken.start");
        tokenUtil.isTokenValid(token);
        logger.info("ServiceLog.validateToken.stop.success");

        return tokenUtil.getUserInfoFromToken(token);
    }


}
