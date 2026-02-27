package org.cswteams.ms3.filters;

import org.junit.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RequestCorrelationFilterTest {

    private static final String REQUEST_ID_KEY = "requestId";
    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Test
    public void filter_withoutHeader_generatesCorrelationId() throws Exception {
        RequestCorrelationFilter filter = new RequestCorrelationFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) throws IOException {
                String requestId = MDC.get(REQUEST_ID_KEY);
                assertNotNull(requestId);
                assertTrue(requestId.length() > 0);
            }
        };

        filter.doFilter(request, response, chain);

        String headerValue = response.getHeader(REQUEST_ID_HEADER);
        assertNotNull(headerValue);
        assertEquals(headerValue, request.getAttribute(REQUEST_ID_KEY));
        assertNull(MDC.get(REQUEST_ID_KEY));
    }

    @Test
    public void filter_withHeader_propagatesCorrelationId() throws Exception {
        RequestCorrelationFilter filter = new RequestCorrelationFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(CORRELATION_ID_HEADER, "corr-123");

        FilterChain chain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) throws IOException {
                assertEquals("corr-123", MDC.get(REQUEST_ID_KEY));
            }
        };

        filter.doFilter(request, response, chain);

        assertEquals("corr-123", response.getHeader(REQUEST_ID_HEADER));
        assertEquals("corr-123", request.getAttribute(REQUEST_ID_KEY));
        assertNull(MDC.get(REQUEST_ID_KEY));
    }
}
