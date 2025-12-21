package org.cswteams.ms3;

import org.cswteams.ms3.tenant.TenantContext;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.BeforeTransaction;

/**
 * Class base per tutti gli integration test multi-tenant.
 */
public abstract class AbstractMultiTenantIntegrationTest {

    protected static final String TEST_TENANT = "a"; // coherent con TenantProperties

    @BeforeEach
    public void setUpTenant() {
        TenantContext.setCurrentTenant(TEST_TENANT);
    }

    @BeforeTransaction
    public void setUpTenantForTx() {
        TenantContext.setCurrentTenant(TEST_TENANT);
    }

}
