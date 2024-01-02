package org.cswteams.ms3.control.cambiaPassword;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.transaction.Transactional;


//@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt. It means that
@Transactional
public class ControllerPasswordTest {
/*
    @Autowired
    private UtenteDao utenteDao;

    @Autowired
    private ControllerPassword controllerPassword;

    Utente savedUtente;
    Long userId;

    @BeforeEach
    public void setup() {
        Utente utente = new Utente("Franco","Marinato", "FRMTN******", LocalDate.of(1994, 3, 14),"salvatimrtina97@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO, AttoreEnum.UTENTE );
        savedUtente = utenteDao.save(utente);
        userId = savedUtente.getId();
    }

    static Stream<Object[]> testData() {
        return Stream.of( // passwordDTO, expected exception
                new Object[]{new PasswordDTO(2L, "passw", "newPass"), false},
                new Object[]{new PasswordDTO(2L, "", "newPass"), true},
                new Object[]{new PasswordDTO(2L, "invalidPass", "newPass"), true},
                new Object[]{new PasswordDTO(2L, null, "newPass"), true},

                new Object[]{new PasswordDTO(2L, "passw", ""), true},
                new Object[]{new PasswordDTO(2L, "passw", null), true}
        );
    }

    @ParameterizedTest
    @MethodSource("testData")
    void changePasswordTest(PasswordDTO passwordDTO, boolean expected) {

        passwordDTO.setId(userId);

        try {
            controllerPassword.cambiaPassword(passwordDTO);
        } catch (Exception e) {
            if(expected) {
                e.printStackTrace();
            } else{
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    void changePasswordInvalidIdTest() {
        ControllerPassword controllerPassword = new ControllerPassword();
        PasswordDTO invalidDTO = new PasswordDTO(1L, "oldPassword", "newPassword");

        try {
            controllerPassword.cambiaPassword(invalidDTO);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void changePasswordNullDTOTest() {
        ControllerPassword controllerPassword = new ControllerPassword();

        try {
            controllerPassword.cambiaPassword(null);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/

}
