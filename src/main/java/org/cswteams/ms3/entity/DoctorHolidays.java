package org.cswteams.ms3.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;

/**
 * Association between <i>Doctors</i> and <i>Holidays</i>.
 */
@Entity
@Data
@EqualsAndHashCode
@Table(name = "doctor_holidays")
public class DoctorHolidays {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "doctor_holidays_id", nullable = false)
    private Long id;

    /**
     * Doctor which the information refers to
     */
    @OneToOne
    @NotNull
    @JoinColumn(name = "doctor_ms3_tenant_user_id", referencedColumnName = "ms3_tenant_user_id")
    private Doctor doctor;

    @Lob
    @NotNull
    @Column(name = "holiday_map")
    private HashMap<Holiday, Boolean> holidayMap;

    /**
     * Default constructor needed by Lombok
     */
    protected DoctorHolidays() {
    }

    /**
     * Create a <i>Doctor</i>-<i>Holiday</i> association with the specified parameters
     * @param doctor <i>Doctor</i> which the association is referred to
     * @param holidayMap
     */
    public DoctorHolidays(Doctor doctor, HashMap<Holiday, Boolean> holidayMap) {
        this.doctor = doctor;
        this.holidayMap = holidayMap;

    }

}
