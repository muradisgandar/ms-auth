package az.gdg.msauth.security.controller;

import az.gdg.msauth.security.model.dto.JwtAuthenticationRequest;
import az.gdg.msauth.security.model.dto.JwtAuthenticationResponse;
import az.gdg.msauth.security.model.dto.UserInfo;
import az.gdg.msauth.security.service.AuthenticationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
@CrossOrigin(exposedHeaders = "Access-Control-Allow-Origin")
@Api(value = "Authentication Controller")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService service;

    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }

    @ApiOperation("Create token if input credentials are valid")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody JwtAuthenticationRequest request) {
        logger.debug("signIn start : mail {}", request.getMail());
        return service.createAuthenticationToken(request);
    }

    @ApiOperation("if token is valid, returns user information")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/validate")
    public UserInfo validateToken(@RequestHeader("X-Auth-Token") String token) {
        logger.debug("validateToken start");
        return service.validateToken(token);
    }

    @ApiOperation("refresh token")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/refresh")
    public JwtAuthenticationResponse refresh(@RequestHeader("X-Auth-Token") String token) {
        logger.debug("refresh start");
        return service.refreshToken(token);
    }


}