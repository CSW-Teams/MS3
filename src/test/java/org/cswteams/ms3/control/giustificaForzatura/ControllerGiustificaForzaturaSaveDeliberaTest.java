package org.cswteams.ms3.control.giustificaForzatura;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ControllerGiustificaForzaturaSaveDeliberaTest extends ControllerGiustificaForzaturaTest{
    @Autowired
    ControllerGiustificaForzatura controllerGiustificaForzatura;

    @BeforeAll
    static void setUp() {
        /* pass */
    }

    @AfterAll
    static void tearDown() {
        /* pass */
    }

    @Test
    @Override
    void saveDelibera() {
        super.saveDelibera();
    }

}
