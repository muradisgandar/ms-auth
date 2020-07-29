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
        return isFirstNameValid(value.getFirstName(), context) &&
                isLastNameValid(value.getLastName(), context) &&
                isMailValid(value.getMail(), context) &&
                isPasswordValid(value.getPassword(), context);
    }

    private boolean isFirstNameValid(String firstName, ConstraintValidatorContext context) {
        if (firstName == null || firstName.isEmpty() || !firstName.matches("[A-Z][a-z]*")) {
            violationHelper.addViolation(context, "firstName", "FirstName is not valid");
            return false;
        }
        return true;
    }

    private boolean isLastNameValid(String lastName, ConstraintValidatorContext context) {
        if (lastName == null || lastName.isEmpty() || !lastName.matches("[A-Z][a-z]*")) {
            violationHelper.addViolation(context, "lastName", "LastName is not valid");
            return false;
        }
        return true;
    }

    private boolean isMailValid(String mail, ConstraintValidatorContext context) {
        if (mail == null ||
                mail.isEmpty() ||
                !mail.matches("^([a-zA-Z0-9_\\\\.-]+)@([a-zA-Z0-9-]+).([a-z]{2,8})(.[a-z]{2,8})?$")) {
            violationHelper.addViolation(context, "mail", "Mail is not valid");
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password, ConstraintValidatorContext context) {
        if (password == null ||
                password.isEmpty() ||
                !password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[./_`~|{}?:;!(),><*@#$%^&+='])" +
                        "(?=\\S+$).{8,}$")) {
            violationHelper.addViolation(context, "password", "Password is not valid");
            return false;
        }
        return true;
    }


}
