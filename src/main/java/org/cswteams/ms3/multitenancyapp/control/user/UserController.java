package org.cswteams.ms3.multitenancyapp.control.user;

import org.cswteams.ms3.multitenancyapp.dao.TenantUserDAO;
import org.cswteams.ms3.multitenancyapp.dto.user.UserDTO;
import org.cswteams.ms3.multitenancyapp.entity.TenantUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserController implements IUserController {

    @Autowired
    private TenantUserDAO tenantUserDAO;

    @Override
    public Set<UserDTO> getAllUsers() {
        List<TenantUser> users = tenantUserDAO.findAll();
        Set<UserDTO> doctorsSet = new HashSet<>();

        for (TenantUser u: users){
            UserDTO dto = new UserDTO(u.getId(), u.getName(), u.getLastname(), u.getBirthday(), u.getEmail());
            doctorsSet.add(dto);
        }

        return doctorsSet;
    }

}
