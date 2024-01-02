package org.cswteams.ms3.entity;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class MedicalService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medical_service_id", nullable = false)
    private Long id;


    @NotNull
    private String label;


    @ManyToMany
    @NotNull
    @Getter
    private List<Task> tasks;
    // TODO: Load this information from a configuration file

    /**
     * Class that represent a service offered by the hospital.
     * (E.g. In the hospital, we have that in the morning in the cardiology ward, we offer treatment)
     * @param tasks The list of the taskEnums offered in this service
     * @param label The medical service offered to the patient (oncology, cardiology, ecc...)
     */
    public MedicalService(List<Task> tasks, String label){
        this.tasks = tasks;
        this.label = label;
    }

    /**
     * Class that represent a service offered by the hospital.
     * (E.g. In the hospital, we have that in the morning in the cardiology ward, we offer treatment)
     * This constructor is useful for editing the instance in the persistence layer
     * @param id The id of the service
     * @param tasks The list of the taskEnums offered in this service
     * @param label The medical service offered to the patient (oncology, cardiology, ecc...)
     */
    public MedicalService(Long id, List<Task> tasks, String label) {
        this.id = id ;
        this.tasks = tasks;
        this.label = label;
    }

    protected MedicalService() {
    }
}
