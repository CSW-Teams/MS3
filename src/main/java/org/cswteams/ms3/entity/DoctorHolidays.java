package org.cswteams.ms3.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode
public class DoctorHolidays {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "doctor_holidays_id", nullable = false)
    private Long id;

    /** Doctor which the information refers to */
    @OneToOne
    @NotNull
    private Doctor doctor;

    @Lob
    @NotNull
    private HashMap<Holiday, Boolean> holidayMap;

    protected DoctorHolidays() {}

    public DoctorHolidays(Doctor doctor, HashMap<Holiday, Boolean> holidayMap) {
        this.doctor = doctor;
        this.holidayMap = holidayMap;

    }

}
