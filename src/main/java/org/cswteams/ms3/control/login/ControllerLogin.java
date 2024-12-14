package org.cswteams.ms3.control.login;


import org.cswteams.ms3.dao.UserDAO;
import org.cswteams.ms3.dto.login.LoggedUserDTO;
import org.cswteams.ms3.dto.login.LoginDTO;
import org.cswteams.ms3.entity.User;
import org.cswteams.ms3.exception.login.InvalidEmailAddressException;
import org.cswteams.ms3.exception.login.InvalidRoleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ControllerLogin implements IControllerLogin {

    @Autowired
    private UserDAO userDAO;

    /**
     * Check if the provided email address is valid, according to a standard regex.
     * @param email address to be validated
     * @return <code>true</code> if <code>email</code> is valid, <code>false</code> elsewhere.
     */
    private boolean checkEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public LoggedUserDTO authenticateUser(@NotNull LoginDTO loginDTO) throws InvalidEmailAddressException, InvalidRoleException {
        boolean isEmailValid = checkEmail(loginDTO.getEmail());

        /* check email address */
        if (!isEmailValid) {
            throw new InvalidEmailAddressException("Invalid Email Format");
        }

        User user = userDAO.findByEmailAndPassword(loginDTO.getEmail(), loginDTO.getPassword());
        LoggedUserDTO dto = null;
        if (user != null){
            if (!user.getSystemActors().contains(loginDTO.getSystemActor())) {
                throw new InvalidRoleException("Invalid Credentials");
            }
            dto = new LoggedUserDTO(user.getId(), user.getName(), user.getLastname(), user.getEmail(), user.getPassword(), user.getSystemActors());
        }
        return dto;
    }
}
