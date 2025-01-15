package org.cswteams.ms3.control.user;

import org.cswteams.ms3.dto.user.UpdateUserProfileDTO;
import org.cswteams.ms3.dto.user.UserCreationDTO;
import org.cswteams.ms3.dto.user.UserDTO;
import org.cswteams.ms3.dto.user.UserDetailsDTO;
import org.cswteams.ms3.dto.userprofile.SingleUserProfileDTO;

import java.util.Set;


public interface IUserController {

    Set<UserDTO> getAllUsers();

    void createUser(UserCreationDTO c);

    UserDetailsDTO getSingleUser(long userId);

    Long getTenantUserId(String email);

    /**
     * Function that has the responsibility to retrive information about single user profile
     * to be shown in the user profile view. In particular this method converts doctor entity of the backend
     * in the
     * @param userId The ID of the user/doctor we are searching
     * @return A well formatted DTO or a dummy DTO with id = -1 if id doesn't corresponds to any
     * DB entry or a null instance if there has been any other problem in the backend
     */
    SingleUserProfileDTO getSingleUserProfileInfos(Long userId);

    void deleteUserSystemActor(Long userID, String systemActor);

    void addSystemActor(Long userID, Set<String> systemActors);

    void updateUserProfile(UpdateUserProfileDTO updateUserProfileDTO);
}