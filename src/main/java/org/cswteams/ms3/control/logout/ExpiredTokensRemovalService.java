package org.cswteams.ms3.control.logout;

import org.cswteams.ms3.dao.BlacklistedTokenDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class ExpiredTokensRemovalService {

    private final BlacklistedTokenDAO dao;

    private final Clock clock;

    @Autowired
    public ExpiredTokensRemovalService(BlacklistedTokenDAO dao, Clock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    @Transactional
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void deleteExpiredTokens() {
        LocalDateTime now = LocalDateTime.now(clock);
        dao.deleteByExpiresAtBefore(now);
    }

}
