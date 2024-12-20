package org.cswteams.ms3.services;

import org.cswteams.ms3.dao.UserDAO;
import org.cswteams.ms3.entity.CustomUserDetails;
import org.cswteams.ms3.entity.User;
import org.cswteams.ms3.enums.SystemActor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =  userDAO.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        return new CustomUserDetails(user);
    }

    public CustomUserDetails loadUserByUsernameAndRole(String username, String role) throws UsernameNotFoundException, RoleNotFoundException {
        User user = userDAO.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        if (!user.getSystemActors().contains(SystemActor.valueOf(role))) {
            throw new RoleNotFoundException("User not found with email: " + username + " and role: " + role);
        }

        return new CustomUserDetails(user, role);
    }
}
