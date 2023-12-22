package org.cswteams.ms3.control.login;


import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.UserDAO;
import org.cswteams.ms3.dto.DoctorDTO;
import org.cswteams.ms3.dto.login.LoggedUserDTO;
import org.cswteams.ms3.dto.login.LoginDTO;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.User;
import org.cswteams.ms3.exception.login.InvalidEmailAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ControllerLogin implements IControllerLogin {

    @Autowired
    private UserDAO userDAO;

    private boolean checkEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public LoggedUserDTO authenticateUser(@NotNull LoginDTO loginDTO) throws InvalidEmailAddressException {
        boolean isEmailValid = checkEmail(loginDTO.getEmail());

        /* check email address */
        if (!isEmailValid) {
            throw new InvalidEmailAddressException("Il formato dell'indirizzo email non è valido");
        }

        User user = userDAO.findByEmailAndPassword(loginDTO.getEmail(), loginDTO.getPassword());
        LoggedUserDTO dto = null;
        if (user != null){
            //TODO: Change LoginDTO logic, user may be more than one Systemactor in the same moment
            dto = new LoggedUserDTO(user.getId(), user.getName(), user.getLastname(), user.getEmail(), user.getPassword(), user.getRoles().get(0));
        }
        return dto;
    }
}
