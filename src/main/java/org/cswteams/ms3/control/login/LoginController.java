package org.cswteams.ms3.control.login;

import org.cswteams.ms3.dao.UserDAO;
import org.cswteams.ms3.dto.login.CustomUserDetails;
import org.cswteams.ms3.entity.User;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.exception.login.InvalidEmailAddressException;
import org.cswteams.ms3.exception.login.InvalidRoleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LoginController implements UserDetailsService, ILoginController {
    @Autowired
    private UserDAO userDAO;

    /**
     * Check if the provided email address is valid, according to a standard regex.
     *
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userDAO.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return new CustomUserDetails(user.getId(), user.getName(), user.getLastname(), user.getEmail(), user.getPassword(), null);
//        throw new UsernameNotFoundException("The loadUserByUsername method is not supported in this application");
    }

    public CustomUserDetails loadUserByUsernameAndRole(String username, String role) throws UsernameNotFoundException, RoleNotFoundException {
        User user = userDAO.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        if (!user.getSystemActors().contains(SystemActor.valueOf(role))) {
            throw new RoleNotFoundException("User not found with email: " + username + " and role: " + role);
        }

        return new CustomUserDetails(user.getId(), user.getName(), user.getLastname(), user.getEmail(), user.getPassword(), SystemActor.valueOf(role));
    }

    @Override
    public CustomUserDetails authenticateUser(String email, String password, SystemActor systemActor) throws InvalidEmailAddressException, InvalidRoleException {
        boolean isEmailValid = checkEmail(email);

        /* check email address */
        if (!isEmailValid) {
            throw new InvalidEmailAddressException("Invalid Email Format");
        }

        User user = userDAO.findByEmailAndPassword(email, password);
        CustomUserDetails dto = null;
        if (user != null) {
            if (!user.getSystemActors().contains(systemActor)) {
                throw new InvalidRoleException("Invalid Credentials");
            }
            dto = new CustomUserDetails(user.getId(), user.getName(), user.getLastname(), user.getEmail(), user.getPassword(), systemActor);
        }
        return dto;
    }
}
