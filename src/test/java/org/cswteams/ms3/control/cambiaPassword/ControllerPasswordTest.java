package org.cswteams.ms3.control.cambiaPassword;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cswteams.ms3.config.multitenancy.TenantConfig;
import org.cswteams.ms3.control.passwordChange.PasswordChange;
import org.cswteams.ms3.dao.TenantUserDAO;
import org.cswteams.ms3.dto.changePassword.ChangePasswordDTO;
import org.cswteams.ms3.entity.TenantUser;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.tenant.TenantContext;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;


@SpringBootTest

public class ControllerPasswordTest {

    @Autowired
    private TenantUserDAO utenteDao;

    @Autowired
    private PasswordChange controllerPassword;

    TenantUser savedUtente;
    static Long userId;

    @BeforeEach
    public void setup() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TenantConfig tenantConfig = objectMapper.readValue(new File("src/main/resources/tenants_config.json"), TenantConfig.class);
            List<String> tenantSchemas = tenantConfig.getTenants();

            if (!tenantSchemas.isEmpty()) {
                String randomTenant = tenantSchemas.get(new Random().nextInt(tenantSchemas.size()));
                System.out.println("Tenant selezionato per il test: " + randomTenant);
                TenantContext.setCurrentTenant(randomTenant.toLowerCase());
            } else {
                System.out.println("Nessun tenant disponibile, fallback su default.");
            }

            TenantUser utente = new TenantUser(
                    "Franco",
                    "Marinato",
                    "FRMTN******",
                    LocalDate.of(1994, 3, 14),
                    "salvatimrtina97@gmail.com",
                    "passw",
                    Set.of(SystemActor.DOCTOR));

            savedUtente = utenteDao.save(utente);
            userId = savedUtente.getId();

        } catch (IOException e) {
            throw new RuntimeException("Errore nella lettura del file tenants_config.json", e);
        }

    }

    @AfterEach
    public void cleanup() {
        TenantContext.clear(); // Ripristina il contesto del tenant per test successivi
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
