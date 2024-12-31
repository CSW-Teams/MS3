package org.cswteams.ms3.control.login;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.stream.Stream;

@SpringBootTest
@Transactional
public class TestControllerLogin {
/*
    private enum InstanceValidity {
        VALID,
        INVALID
    }

    @Autowired
    private ControllerLogin controllerLogin;

    private static Stream<Arguments> autenticaUtenteParams() {
        return Stream.of(
                //           loginDTO, exception expected
                Arguments.of(InstanceValidity.VALID, false),
                Arguments.of(InstanceValidity.INVALID, true),
                Arguments.of(null, true)
        );
    }

    @ParameterizedTest
    @MethodSource("autenticaUtenteParams")
    public void testAutenticaUtente(InstanceValidity validity, boolean exceptionExpected) {
        LoginRequestDTO loginDTO;
        if (validity == InstanceValidity.VALID) {
            loginDTO = new LoginRequestDTO("**@gmail.com", "passw");
        } else if (validity == InstanceValidity.INVALID) {
            loginDTO = new LoginRequestDTO("email", "password");
        } else {
            loginDTO = null;
        }

        try {
            controllerLogin.autenticaUtente(loginDTO);
            Assertions.assertFalse(exceptionExpected);      // FAIL: non viene comunicata un'eccezione nel caso di utente non esistente
        } catch (Exception e) {
            Assertions.assertTrue(exceptionExpected);
        }
    }*/

}
