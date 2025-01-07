package org.cswteams.ms3.multitenancyapp.control.login;

import lombok.extern.slf4j.Slf4j;
import org.cswteams.ms3.multitenancyapp.dao.SystemUserDAO;
import org.cswteams.ms3.multitenancyapp.dto.login.CustomUserDetails;
import org.cswteams.ms3.multitenancyapp.entity.SystemUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoginController implements UserDetailsService {

    @Autowired
    private SystemUserDAO userDAO;

    /**
     * Check if the provided email address is valid, according to a standard regex.
     *
     * @param email address to be validated
     * @return <code>true</code> if <code>email</code> is valid, <code>false</code> elsewhere.
     */
    private boolean checkEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        return email != null && email.matches(emailRegex);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null || email.trim().isEmpty()) {
            log.error("Email is null or empty");
            throw new UsernameNotFoundException("Invalid email format");
        }

        // Check if the email format is valid
        boolean isEmailValid = checkEmail(email);
        if (!isEmailValid) {
            log.error("Invalid email format: {}", email);
            throw new UsernameNotFoundException("Invalid email format");
        }

        // Retrieve the user from the database
        SystemUser user = userDAO.findByEmail(email);
        if (user == null) {
            log.error("SystemUser not found with email: {}", email);
            throw new UsernameNotFoundException("SystemUser not found with email: " + email);
        }

        return new CustomUserDetails(user.getId(), user.getName(), user.getLastname(), user.getEmail(), user.getPassword(), user.getTenant());
    }
}