package az.gdg.msauth.controller;

import az.gdg.msauth.model.dto.UserDTO;
import az.gdg.msauth.security.model.dto.UserInfo;
import az.gdg.msauth.security.service.AuthenticationService;
import az.gdg.msauth.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@CrossOrigin(exposedHeaders = "Access-Control-Allow-Origin")
@Api("User Controller")
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @ApiOperation("sign up new user")
    @PostMapping(value = "/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody @Valid UserDTO userDTO) {
        logger.debug("Sign up user start");
        userService.signUp(userDTO);
        logger.debug("Sign up user end");
    }

    @ApiOperation("get user info")
    @GetMapping("/info")
    public UserInfo getCustomerInfo(@RequestHeader("X-Auth-Token") String token) {
        logger.debug("Token validation start");
        return authenticationService.validateToken(token);
    }

    @ApiOperation("get userId by email")
    @GetMapping("/id/by/email/{email}")
    public String getCustomerIdByEmail(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable(name = "email") String email) {
        logger.debug("Get Customer's id by email");
        return userService.getCustomerIdByEmail(token, email);
    }

    @ApiOperation("verify account when user registers")
    @GetMapping(value = "/verify-account")
    public String verifyAccount(@RequestParam("email") String email, @RequestParam("code") String code) {
        logger.debug("VerifyAccount start");
        userService.verifyAccount(email, code);
        logger.debug("VerifyAccount end");
        return "Your account is verified, now you can log in";
    }

    @ApiOperation("send reset password link to mail")
    @PostMapping(value = "/forgot-password")
    public void sendResetPasswordLinkToMail(@RequestParam("email") String email) {
        logger.debug("SendResetPasswordLinkToMail start");
        userService.sendResetPasswordLinkToMail(email);
        logger.debug("SendResetPasswordLinkToMail stop");
    }

    @ApiOperation("reset password")
    @PostMapping(value = "/reset-password")
    public void resetPassword(@RequestParam("token") String token, @RequestParam("password") String password) {
        logger.debug("ResetPassword start");
        userService.resetPassword(token, password);
        logger.debug("ResetPassword stop");
    }

}
