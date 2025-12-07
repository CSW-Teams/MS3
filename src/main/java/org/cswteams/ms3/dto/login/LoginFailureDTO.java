package org.cswteams.ms3.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginFailureDTO {
    private String message;
    private boolean captchaRequired;
}
