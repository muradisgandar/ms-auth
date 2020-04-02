package az.gdg.msauth.validation.user;

import az.gdg.msauth.model.dto.UserDTO;
import az.gdg.msauth.util.CheckViolationHelper;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class UserValidator implements
        ConstraintValidator<UserConstraint, UserDTO> {


    private final CheckViolationHelper violationHelper;

    public UserValidator(CheckViolationHelper violationHelper) {
        this.violationHelper = violationHelper;
    }

    @Override
    public boolean isValid(UserDTO value, ConstraintValidatorContext context) {
        return isNameValid(value.getName(),context) &&
                isSurnameValid(value.getSurname(),context) &&
                isEmailValid(value.getEmail(),context) &&
                isPasswordValid(value.getPassword(),context);
    }

    private boolean isNameValid(String name, ConstraintValidatorContext context) {
        if (name == null || name.isEmpty() || !name.matches("[A-Z][a-z]*")) {
            violationHelper.addViolation(context,"name","Name is not valid");
            return false;
        }
        return true;
    }

    private boolean isSurnameValid(String surname, ConstraintValidatorContext context) {
        if (surname == null || surname.isEmpty() || !surname.matches("[A-Z][a-z]*")) {
            violationHelper.addViolation(context,"surname","Surname is not valid");
            return false;
        }
        return true;
    }

    private boolean isEmailValid(String email, ConstraintValidatorContext context) {
        if (email == null ||
                email.isEmpty() ||
                !email.matches("^([a-zA-Z0-9_\\\\.-]+)@([a-zA-Z0-9-]+).([a-z]{2,8})(.[a-z]{2,8})?$")) {
            violationHelper.addViolation(context,"email","Email is not valid");
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password, ConstraintValidatorContext context) {
        if (password == null ||
                password.isEmpty() ||
                !password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[.@#$%^&+=])(?=\\S+$).{8,}$")) {
            violationHelper.addViolation(context,"password","Password is not valid");
            return false;
        }
        return true;
    }


}
