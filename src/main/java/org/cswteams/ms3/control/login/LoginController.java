package org.cswteams.ms3.control.login;

import org.cswteams.ms3.dao.SystemUserDAO;
import org.cswteams.ms3.dto.login.CustomUserDetails;
import org.cswteams.ms3.entity.SystemUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginController implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

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
            logger.error("Email is null or empty");
            throw new UsernameNotFoundException("Invalid email format");
        }

        // Check if the email format is valid
        boolean isEmailValid = checkEmail(email);
        if (!isEmailValid) {
            logger.error("Invalid email format: {}", email);
            throw new UsernameNotFoundException("Invalid email format");
        }

        // Retrieve the user from the database
        SystemUser user = userDAO.findByEmail(email);
        if (user == null) {
            logger.error("TenantUser not found with email: {}", email);
            throw new UsernameNotFoundException("TenantUser not found with email: " + email);
        }

        return new CustomUserDetails(user.getId(), user.getName(), user.getLastname(), user.getEmail(), user.getPassword(), user.getSystemActors(), user.getTenant());
    }
}
