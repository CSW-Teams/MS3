package org.cswteams.ms3.multitenancyapp.control.user;

import org.cswteams.ms3.multitenancyapp.dto.user.UserDTO;

import java.util.Set;


public interface IUserController {

    Set<UserDTO> getAllUsers();

}