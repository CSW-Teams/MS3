package org.cswteams.ms3.audit.selection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AuditSelectionAspectTest.TestConfig.class)
public class AuditSelectionAspectTest {

    @Configuration
    @EnableAspectJAutoProxy
    static class TestConfig {
        @Bean
        public AuditRecorder auditRecorder() {
            return mock(AuditRecorder.class);
        }

        @Bean
        public AuditSelectionAspect auditSelectionAspect(AuditRecorder recorder) {
            return new AuditSelectionAspect(recorder);
        }

        @Bean
        public SampleSelectionService sampleSelectionService() {
            return new SampleSelectionService();
        }
    }

    static class SampleSelectionService {
        @AuditSelection("sample-selection")
        public AuditedSelectionResult select() {
            SelectionAuditEvent event1 = new SelectionAuditEvent(null, "O1", "C1", 0.8, true, null);
            SelectionAuditEvent event2 = new SelectionAuditEvent(null, "O2", "C2", 0.5, false, null);
            return new AuditedSelectionResult("C1", List.of(event1, event2));
        }
    }

    @javax.annotation.Resource
    private SampleSelectionService sampleSelectionService;

    @javax.annotation.Resource
    private AuditRecorder auditRecorder;

    @Test
    public void annotatedMethod_shouldRecordAuditEvents() {
        sampleSelectionService.select();
        verify(auditRecorder, times(2)).record(org.mockito.ArgumentMatchers.any(SelectionAuditEvent.class));
    }
}
