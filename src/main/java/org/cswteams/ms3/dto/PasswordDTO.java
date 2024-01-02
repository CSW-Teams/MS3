package org.cswteams.ms3.dto;

import lombok.Data;

@Data
public class PasswordDTO {

    private Long id;
    private String oldPassword;
    private String newPassword;

    public PasswordDTO() { }

    public PasswordDTO(Long id, String oldPass, String newPass) {
        this.id = id;
        this.oldPassword = oldPass;
        this.newPassword = newPass;
    }



}
