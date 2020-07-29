package az.gdg.msauth.service;

import az.gdg.msauth.model.dto.UserDTO;
import az.gdg.msauth.model.dto.UserDetail;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    void signUp(UserDTO userDTO);

    void verifyAccount(String token);

    void sendResetPasswordLinkToMail(String email);

    void changePassword(String token, String password);

    UserDetail getUserById(Long id);

    List<UserDetail> getUsersById(List<Long> userIds);

    void addPopularity(Long userId);

    List<UserDetail> getPopularUsers();

    void updateRemainingQuackCount(String token);

    void updateRemainingHateCount(String token);

    Integer getRemainingQuackCount(String token);

    Integer getRemainingHateCount(String token);

    void refreshRemainingQuackAndHateCount();

    void updateImage(String token, List<MultipartFile> multipartFile);


}
