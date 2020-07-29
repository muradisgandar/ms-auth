package az.gdg.msauth.controller;

import az.gdg.msauth.model.dto.UserDTO;
import az.gdg.msauth.model.dto.UserDetail;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin(exposedHeaders = "Access-Control-Allow-Origin")
@Api("User Controller")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final AuthenticationService authenticationService;

    public UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @ApiOperation("sign up new user")
    @PostMapping(value = "/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody @Valid UserDTO userDTO) {
        logger.debug("signUp user start : mail {}", userDTO.getMail());
        userService.signUp(userDTO);
        logger.debug("signUp user end : mail {}", userDTO.getMail());
    }

    @ApiOperation("get user info")
    @GetMapping("/info")
    public UserInfo getUserInfo(@RequestHeader("X-Auth-Token") String token) {
        logger.debug("getUserInfo start");
        return authenticationService.validateToken(token);
    }

    @ApiOperation("get user by id for blog service")
    @GetMapping("/{userId}")
    public UserDetail getUserById(@PathVariable("userId") Long userId) {
        logger.debug("getUserById start : userId {}", userId);
        return userService.getUserById(userId);
    }

    @ApiOperation("get users by id for blog service")
    @PostMapping("/get-users")
    public List<UserDetail> getUsersById(@RequestBody List<Long> userIds) {
        logger.debug("getUsersById start : userId {}", userIds);
        return userService.getUsersById(userIds);
    }

    @ApiOperation("verify account when user registers")
    @GetMapping(value = "/verify-account")
    public String verifyAccount(@RequestParam("token") String token) {
        logger.debug("verifyAccount start");
        userService.verifyAccount(token);
        logger.debug("verifyAccount stop");
        return "Your account is verified, now you can log in";
    }

    @ApiOperation("send reset password link to mail")
    @PostMapping(value = "/forgot-password")
    public void sendResetPasswordLinkToMail(@RequestBody String mail) {
        logger.debug("sendResetPasswordLinkToMail start : mail {}", mail);
        userService.sendResetPasswordLinkToMail(mail);
        logger.debug("sendResetPasswordLinkToMail stop : mail {}", mail);
    }

    @ApiOperation("change password")
    @PostMapping(value = "/change-password")
    public void changePassword(@RequestHeader("X-Auth-Token") String token,
                               @RequestBody String password) {
        logger.debug("changePassword start");
        userService.changePassword(token, password);
        logger.debug("changePassword stop");
    }

    @PutMapping("/popularity/{userId}")
    @ApiOperation("add popularity to user for his article which is read")
    public void addPopularity(@PathVariable("userId") Long id) {
        logger.debug("addPopularity start : id {}", id);
        userService.addPopularity(id);
    }

    @GetMapping("/popular-users")
    @ApiOperation("get most popular users")
    public List<UserDetail> getPopularUsers() {
        logger.debug("getPopularUsers start");
        return userService.getPopularUsers();
    }

    @ApiOperation("update remaining quack count when user likes some article")
    @PutMapping("/update-remaining-quack-count")
    public void updateRemainingQuackCount(@RequestHeader("X-Auth-Token") String token) {
        logger.debug("updateRemainingQuackCount start");
        userService.updateRemainingQuackCount(token);
        logger.debug("updateRemainingQuackCount stop");
    }

    @ApiOperation("update remaining hate count when user hates some article")
    @PutMapping("/update-remaining-hate-count")
    public void updateRemainingHateCount(@RequestHeader("X-Auth-Token") String token) {
        logger.debug("updateRemainingHateCount start");
        userService.updateRemainingHateCount(token);
        logger.debug("updateRemainingHateCount stop");
    }

    @ApiOperation("get remaining quack count")
    @GetMapping("/get-remaining-quack-count")
    public Integer getRemainingQuackCount(@RequestHeader("X-Auth-Token") String token) {
        logger.debug("getRemainingQuackCount start");
        return userService.getRemainingQuackCount(token);
    }

    @ApiOperation("get remaining hate count")
    @GetMapping("/get-remaining-hate-count")
    public Integer getRemainingHateCount(@RequestHeader("X-Auth-Token") String token) {
        logger.debug("updateRemainingHateCount start");
        return userService.getRemainingHateCount(token);
    }

    @ApiOperation("update image")
    @PutMapping("/update-image")
    public void updateImage(@RequestHeader("X-Auth-Token") String token,
                            @RequestParam List<MultipartFile> multipartFile) {
        logger.debug("updateImage start");
        userService.updateImage(token, multipartFile);
        logger.debug("updateImage stop success");
    }


}
