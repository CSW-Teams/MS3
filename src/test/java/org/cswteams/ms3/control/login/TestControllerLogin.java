package org.cswteams.ms3.control.login;

import org.cswteams.ms3.dto.login.LoginRequestDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.transaction.Transactional;
import java.util.stream.Stream;

@SpringBootTest
@Transactional
public class TestControllerLogin {

    @Autowired
    private AuthenticationManager authenticationManager;

    private static Stream<Arguments> autenticaUtenteParams() {
        return Stream.of(
                // loginDTO, exception expected
                Arguments.of(new LoginRequestDTO("**@gmail.com", "passw"), false),
                Arguments.of(new LoginRequestDTO("email", "password"), true)
        );
    }

    @ParameterizedTest
    @MethodSource("autenticaUtenteParams")
    public void testAutenticaUtente(LoginRequestDTO loginDTO, boolean exceptionExpected) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDTO.getEmail(),
                    loginDTO.getPassword()
            ));
            Assertions.assertFalse(exceptionExpected);      // FAIL: non viene comunicata un'eccezione nel caso di utente non esistente
        } catch (Exception e) {
            Assertions.assertTrue(exceptionExpected);
        }
    }

}
