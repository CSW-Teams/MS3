package org.cswteams.ms3.entity;

import lombok.Getter;
import org.cswteams.ms3.enums.TaskEnum;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "task_id", nullable = false)
    @Getter
    private Long id;

    @Enumerated
    @Getter
    private TaskEnum taskType;

    @ManyToMany
    private List<Doctor> doctors;

    protected Task(){

    }

    /**
     * Class that has the responsibility to map between a list of doctors which
     * take the responsibility of a task in a shift
     * (E.g. We want to know which doctor is in the operating room for a certain shift)
     * @param taskType Type of all the possible tasks to be assigned to
     */
    public Task(TaskEnum taskType){
        this.taskType = taskType;
        this.doctors = new ArrayList<>();
    }


    public void addDoctor(Doctor doctor) {
        if(this.doctors!= null){
            this.doctors.add(doctor);
        }

    }
}
