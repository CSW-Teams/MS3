package org.cswteams.ms3.DBperTenant.control.user;

import org.cswteams.ms3.DBperTenant.dto.user.UserDTO;

import java.util.Set;


public interface IUserController {

    Set<UserDTO> getAllUsers();

}