package az.gdg.msauth.service.impl;

import az.gdg.msauth.dao.UserRepository;
import az.gdg.msauth.model.dto.UserDTO;
import az.gdg.msauth.model.entity.UserEntity;
import az.gdg.msauth.exception.WrongDataException;
import az.gdg.msauth.security.model.Status;
import az.gdg.msauth.security.model.dto.UserInfo;
import az.gdg.msauth.security.exception.AuthenticationException;
import az.gdg.msauth.security.model.Role;
import az.gdg.msauth.security.service.AuthenticationService;
import az.gdg.msauth.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    public void signUp(UserDTO userDTO) {
        logger.info("ActionLog.Sign up user.Start");
        UserEntity checkedEmail = userRepository.findByEmail(userDTO.getEmail());
        if (checkedEmail != null) {
            logger.error("ActionLog.WrongDataException.Thrown");
            throw new WrongDataException("This email already exists");
        }

        String password = new BCryptPasswordEncoder().encode(userDTO.getPassword());
        UserEntity userEntity = UserEntity
                .builder()
                .name(userDTO.getName())
                .surname(userDTO.getSurname())
                .username(userDTO.getEmail())
                .email(userDTO.getEmail())
                .password(password)
                .role(Role.ROLE_USER)
                .status(Status.REGISTERED)
                .build();

        userRepository.save(userEntity);
        logger.info("ActionLog.Sign up user.Stop.Success");

    }

    public String getCustomerIdByEmail(String token, String email) {
        logger.info("ActionLog.GetCustomerIdByEmail.Start");
        UserInfo userInfo = authenticationService.validateToken(token);
        String userRole = userInfo.getRole();
        if (!userRole.equals("ROLE_ADMIN")) {
            logger.error("ActionLog.AuthenticationException.Thrown");
            throw new AuthenticationException("You do not have rights for access");
        }

        UserEntity foundUser = userRepository.findByEmail(email);
        if (foundUser != null) {
            logger.info("ActionLog.GetCustomerIdByEmail.Stop.Success");
            return foundUser.getId().toString();
        } else {
            logger.error("ActionLog.WrongDataException.Thrown");
            throw new WrongDataException("No such email is found");
        }

    }
}
