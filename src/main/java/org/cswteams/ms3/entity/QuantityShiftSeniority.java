package org.cswteams.ms3.entity;

import lombok.Data;
import org.cswteams.ms3.enums.Seniority;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class allows you to store how many users of each role are needed in a specific shift.
 * For example, on the night shift in the department there must be 1 resident and 1 specialist among the allocated users. */
@Entity
@Data
public class QuantityShiftSeniority {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * SystemUser role.
     * The integer value represents the number of <i>Users</i> of a specific <i>Seniority</i> to be allocated to a specific <i>Shift</i>.
     */
    @Lob
    private HashMap<Seniority,Integer> seniorityMap;

    @ManyToOne
    private Task task;

    /**
     * Create a <code>QuantitiShiftSeniority</code> object with the specified parameters.
     *
     * @param id        association id
     * @param seniority <i>seniority</i> level required
     * @param task      <i>task</i> to be assigned
     */
    public QuantityShiftSeniority(Long id, Map<Seniority,Integer> seniority,Task task) {
        this.id = id;
        this.seniorityMap =new HashMap<>(seniority);
        this.task = task;
    }

    /**
     * Create a <code>QuantitiShiftSeniority</code> object with the specified parameters.
     * @param seniority <i>seniority</i> level required
     * @param task <i>task</i> to be assigned
     */
    public QuantityShiftSeniority(Map<Seniority,Integer> seniority, Task task) {
        this.seniorityMap =new HashMap<>(seniority);
        this.task = task;
    }

    /**
     * Default constructor needed by Lombok
     */
    public QuantityShiftSeniority(){

    }


}
