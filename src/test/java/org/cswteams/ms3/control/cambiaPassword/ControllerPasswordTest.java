package org.cswteams.ms3.control.cambiaPassword;

import org.cswteams.ms3.control.passwordChange.PasswordChange;
import org.cswteams.ms3.dao.UserDAO;
import org.cswteams.ms3.dto.changePassword.ChangePasswordDTO;
import org.cswteams.ms3.entity.User;
import org.cswteams.ms3.enums.SystemActor;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;


@SpringBootTest

public class ControllerPasswordTest {

    @Autowired
    private UserDAO utenteDao;

    @Autowired
    private PasswordChange controllerPassword;

    User savedUtente;
    static Long userId;

    @BeforeEach
    public void setup() {
        User utente = new User(
                "Franco",
                "Marinato",
                "FRMTN******",
                LocalDate.of(1994, 3, 14),
                "salvatimrtina97@gmail.com",
                "passw",
                Set.of(SystemActor.DOCTOR));
        savedUtente = utenteDao.save(utente);
        userId = savedUtente.getId();

    }

    static Stream<Object[]> testData() {
        return Stream.of( // passwordDTO, expected exception
                new Object[]{new ChangePasswordDTO(userId, "passw", "newPass"), null},
                new Object[]{new ChangePasswordDTO(userId, "", "newPass"), Exception.class},
                new Object[]{new ChangePasswordDTO(userId, "invalidPass", "newPass"), Exception.class},
                new Object[]{new ChangePasswordDTO(userId, null, "newPass"), Exception.class},
//
                new Object[]{new ChangePasswordDTO(userId, "passw", ""), Exception.class},
                new Object[]{new ChangePasswordDTO(userId, "passw", null), Exception.class}
        );
    }

    @ParameterizedTest
    @MethodSource("testData")
    void changePasswordTest(ChangePasswordDTO passwordDTO, Class<Exception> expectedException) {
        passwordDTO.setUserId(userId);

        if(expectedException != null) {
            Assertions.assertThrows(expectedException, () -> {
                controllerPassword.changePassword(passwordDTO);
            });
        }
        else {
            try {
                controllerPassword.changePassword(passwordDTO);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


    }

    @Test
    public void changePasswordInvalidIdTest() {
        PasswordChange controllerPassword = new PasswordChange();
        ChangePasswordDTO invalidDTO = new ChangePasswordDTO(1L, "oldPassword", "newPassword");

        try {
            controllerPassword.changePassword(invalidDTO);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void changePasswordNullDTOTest() {
        PasswordChange controllerPassword = new PasswordChange();

        try {
            controllerPassword.changePassword(null);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
