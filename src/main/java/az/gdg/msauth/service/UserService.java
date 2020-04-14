package az.gdg.msauth.service;

import az.gdg.msauth.model.dto.UserDTO;

public interface UserService {

    public void signUp(UserDTO userDTO);

    public String getCustomerIdByEmail(String token, String email);

    public void verifyAccount(String email, String code);

    public void sendResetPasswordLinkToMail(String email);

    public void resetPassword(String email, String password);
}
