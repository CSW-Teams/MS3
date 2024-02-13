package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Request;
import org.cswteams.ms3.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftChangeRequestDAO extends JpaRepository<Request,Long> {
    List<Request> findBySenderIdAndTurnIdAndStatus(Long senderId, Long shiftId, RequestStatus status);

    /**
     * Return all requests made by the sender
     *
     * @param senderId sender id
     * @return list of all requests made by <code>senderId</code>
     */
    List<Request> findBySenderId(Long senderId);

    /**
     * Return all requests made to the server having a specific status (pending)
     *
     * @param senderId sender id
     * @param status status required
     * @return list of all requests made by <code>senderId</code> with status matching <code>status</code>
     */
    List<Request> findByReceiverIdAndStatus(Long senderId, RequestStatus status);

}
