package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Request;
import org.cswteams.ms3.enums.RequestENUM;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestTurnChangeDao extends JpaRepository<Request,Long> {
    List<Request> findBySenderIdAndTurnIdAndStatus(Long senderId, Long turnId, RequestENUM status);

    // Return all requests made by the sender
    List<Request> findBySenderId(Long senderId);

    // Return all requests made to the server having a specific status (pending)
    List<Request> findByReceiverIdAndStatus(Long senderId, RequestENUM status);
}
