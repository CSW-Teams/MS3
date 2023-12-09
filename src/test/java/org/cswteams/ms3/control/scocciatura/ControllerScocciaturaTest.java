package org.cswteams.ms3.control.scocciatura;

import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.entity.doctor.Doctor;
import org.cswteams.ms3.entity.scocciature.*;
import org.cswteams.ms3.enums.AttoreEnum;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ControllerScocciaturaTest {

    private ControllerScocciatura controller;
    private List<Scocciatura> scocciature;

    @BeforeEach
    public void setup() {

        scocciature = new ArrayList<>();

        controller = new ControllerScocciatura(scocciature);
    }

    @Test
    public void testCalcolaUffaComplessivoUtenteAssegnazione() {
        List<Scocciatura> scocciature = new ArrayList<>();
        scocciature.add(new ScocciaturaAssegnazioneUtente(5, DayOfWeek.MONDAY, TipologiaTurno.MATTUTINO));
        scocciature.add(new ScocciaturaDesiderata(10));
        scocciature.add(new ScocciaturaVacanza(7, new Holiday(), TipologiaTurno.NOTTURNO));

        ControllerScocciatura controller = new ControllerScocciatura(scocciature);

        Shift shift = new Shift();
        shift.setTipologiaTurno(TipologiaTurno.MATTUTINO);

        Doctor doctor = new Doctor("Martina","Salvati", "SLVMTN******", LocalDate.of(1997, 3, 14),"salvatimartina97@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO, AttoreEnum.UTENTE );
        UserScheduleState userScheduleState = new UserScheduleState();
        userScheduleState.setDoctor(doctor);

        AssegnazioneTurno assegnazioneTurno = new AssegnazioneTurno(LocalDate.of(2023, 11, 20), shift);
        ContestoScocciatura contestoScocciatura = new ContestoScocciatura(userScheduleState, assegnazioneTurno);

        int expectedUffa = 5;
        int calculatedUffa = controller.calcolaUffaComplessivoUtenteAssegnazione(contestoScocciatura);

        assertEquals(expectedUffa, calculatedUffa);
    }
}
