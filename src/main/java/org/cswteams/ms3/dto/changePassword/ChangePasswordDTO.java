package org.cswteams.ms3.dto.changePassword;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.cswteams.ms3.utils.must_be_different.MustBeDifferent;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * DTO used in the "change password" use case
 */
@Data
@MustBeDifferent(first = "oldPassword", second = "newPassword")
public class ChangePasswordDTO {

    @NotNull
    private Long userId;
    @NotNull
    @NotEmpty
    private String oldPassword;
    @NotNull
    @NotEmpty
    private String newPassword;

    public ChangePasswordDTO(
            @JsonProperty("userId") Long userId,
            @JsonProperty("oldPassword") String oldPass,
            @JsonProperty("newPassword") String newPass) {
        this.userId = userId;
        this.oldPassword = oldPass;
        this.newPassword = newPass;
    }



}
