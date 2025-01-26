package org.cswteams.ms3.control.scocciatura;

import org.cswteams.ms3.control.medicalService.IMedicalServiceController;
import org.cswteams.ms3.dao.HolidayDAO;
import org.cswteams.ms3.dto.shift.ShiftDTOIn;
import org.cswteams.ms3.dto.shift.ShiftDTOOut;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.entity.scocciature.*;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.enums.TaskEnum;
import org.cswteams.ms3.enums.TimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ControllerScocciaturaTest {

    private ControllerScocciatura controller;
    private List<Scocciatura> scocciature;
    @Autowired
    private IMedicalServiceController medicalServiceController;

    @Autowired
    private HolidayDAO holidayDAO;

    @BeforeEach
    public void setup() {

        scocciature = new ArrayList<>();

        controller = new ControllerScocciatura(scocciature);
    }

    @Test
    public void testCalcolaUffaComplessivoUtenteAssegnazione() {
        List<Scocciatura> scocciature = new ArrayList<>();
        scocciature.add(new ScocciaturaAssegnazioneUtente(5, DayOfWeek.MONDAY, TimeSlot.NIGHT));
        scocciature.add(new ScocciaturaDesiderata(10));
        scocciature.add(new ScocciaturaVacanza(7, new Holiday(), TimeSlot.NIGHT));

        ControllerScocciatura controller = new ControllerScocciatura(scocciature);

        Task clinic = new Task(TaskEnum.CLINIC);
        MedicalService ambulatorioCardiologia = medicalServiceController.createService(Collections.singletonList(clinic), "CARDIOLOGIA");

        List<QuantityShiftSeniority> quantityShiftSeniorityList1 = new ArrayList<>();
        for(Task t:ambulatorioCardiologia.getTasks()) {
            Map<Seniority,Integer> mapSeniorityQuantity=new HashMap<>();
            mapSeniorityQuantity.put(Seniority.SPECIALIST_SENIOR,1);
            mapSeniorityQuantity.put(Seniority.STRUCTURED,1);
            mapSeniorityQuantity.put(Seniority.SPECIALIST_JUNIOR,1);
            QuantityShiftSeniority quantityShiftSeniority  = new QuantityShiftSeniority(mapSeniorityQuantity,t);
            quantityShiftSeniorityList1.add(quantityShiftSeniority);
        }
        Set<DayOfWeek> allDaysOfWeek = new HashSet<>();
        allDaysOfWeek.add(DayOfWeek.MONDAY);
        allDaysOfWeek.add(DayOfWeek.TUESDAY);
        allDaysOfWeek.add(DayOfWeek.WEDNESDAY);
        allDaysOfWeek.add(DayOfWeek.THURSDAY);
        allDaysOfWeek.add(DayOfWeek.FRIDAY);
        allDaysOfWeek.add(DayOfWeek.SATURDAY);
        allDaysOfWeek.add(DayOfWeek.SUNDAY);
        Shift shift = new Shift(LocalTime.of(1, 0),
                Duration.ofHours(6),
                ambulatorioCardiologia,
                TimeSlot.NIGHT,
                quantityShiftSeniorityList1,
                allDaysOfWeek,
                Collections.emptyList());

        ConcreteShift concreteShift = new ConcreteShift(LocalDate.now().plusDays(1).toEpochDay(), shift);

        Doctor d = new Doctor("Martina","Salvati", "SLVMTN******", LocalDate.of(1997, 3, 14),"salvatimartina97@gmail.com", "passw", Seniority.STRUCTURED, Set.of(SystemActor.DOCTOR));
        UserScheduleState userScheduleState = new UserScheduleState();
        userScheduleState.setUtente(d);

        List<Holiday> holidays = holidayDAO.findAll();  //retrieve of holiday entities (and not DTOs)

        //we are assuming that, at the moment of instantiation of DoctorHolidays, the corresponding doctor has worked in no concrete shift in the past.
        HashMap<Holiday, Boolean> holidayMap = new HashMap<>();
        for(Holiday holiday: holidays) {
            if(!holiday.getName().equals("Domenica"))   //we do not care about Sundays as holidays
                holidayMap.put(holiday, false);

        }

        DoctorUffaPriority dup = new DoctorUffaPriority(d);

        ContestoScocciatura contestoScocciatura = new ContestoScocciatura(dup, concreteShift);

        int expectedUffa = 5;
        int calculatedUffa = controller.calcolaUffaComplessivoUtenteAssegnazione(contestoScocciatura);

        assertEquals(expectedUffa, calculatedUffa);
    }
}
