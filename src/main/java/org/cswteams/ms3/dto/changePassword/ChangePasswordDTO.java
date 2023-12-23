package org.cswteams.ms3.dto.changePassword;

import lombok.Data;

/**
 * DTO used in the "change password" use case
 */
@Data
public class ChangePasswordDTO {

    private Long userId;
    private String oldPassword;
    private String newPassword;

    public ChangePasswordDTO() { }

    public ChangePasswordDTO(Long userId, String oldPass, String newPass) {
        this.userId = userId;
        this.oldPassword = oldPass;
        this.newPassword = newPass;
    }



}
