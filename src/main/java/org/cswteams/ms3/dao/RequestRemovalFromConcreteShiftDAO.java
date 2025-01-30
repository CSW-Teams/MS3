package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.RequestRemovalFromConcreteShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRemovalFromConcreteShiftDAO extends JpaRepository<RequestRemovalFromConcreteShift, Long> {

    /**
     * Returns a list of all requests for removal from assigned shift <i>pending</i>,
     * i.e. not examined, i.e. with <code>examined</code> field set to <code>false</code>.
     *
     * @return list of requests to remove from assigned shift <i>pending</i>
     */
    @Query("SELECT  r " +
            "FROM   RequestRemovalFromConcreteShift r " +
            "WHERE  r.isReviewed = false")
    List<RequestRemovalFromConcreteShift> findAllPending();

    /**
     * Returns a list of all removal requests from assigned shift
     * for a specific <code>ConcreteShift</code> and a specific <code>TenantUser</code>.
     * The cardinality of this list should always be 0 or 1 (if the request exists, it is unique).
     *
     * @param shift assignmentId shift assignment id
     * @param UserId user id
     * @return list, possibly empty, of removal requests from the assigned shift for the specification
     * <code>Shift Assignment</code> and for the specific <code>TenantUser</code>
     */
    @Query("SELECT  r " +
            "FROM   RequestRemovalFromConcreteShift r " +
            "WHERE  r.concreteShift.id = ?1 " +
            "       AND " +
            "       r.requestingDoctor.id = ?2")
    List<RequestRemovalFromConcreteShift> findAllByAssegnazioneTurnoIdAndUtenteId(Long shift, Long UserId);

    /**
     * Returns a list of all removal requests from assigned shift
     * for a specific <code>TenantUser</code>.
     *
     * @param UserId user id
     * @return list, possibly empty, of removal requests from assigned shift
     * for the specific <code>TenantUser</code>.
     */
    @Query("SELECT  r " +
            "FROM   RequestRemovalFromConcreteShift r " +
            "WHERE  r.requestingDoctor.id = ?1")
    List<RequestRemovalFromConcreteShift> findAllByUser(Long UserId);
}
