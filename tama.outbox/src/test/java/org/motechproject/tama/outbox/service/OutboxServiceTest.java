package org.motechproject.tama.outbox.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.service.PatientService;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class OutboxServiceTest {

    @Mock
    protected PatientService patientService;
    @Mock
    protected OutboxSchedulerService outboxSchedulerService;
    private OutboxService outboxService;

    @Before
    public void setUp() {
        initMocks(this);
        outboxService = new OutboxService(outboxSchedulerService, patientService);
    }

    @Test
    public void patientEnrolls_ToOutbox() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        outboxService.enroll(patient);

        verify(patientService).registerOutbox(outboxService);
        verify(outboxSchedulerService).scheduleOutboxJobs(patient);
    }

    @Test
    public void patientReEnrolls_ToOutbox() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        outboxService.reEnroll(patient);

        verify(patientService).registerOutbox(outboxService);
        verify(outboxSchedulerService).rescheduleOutboxJobs(patient);
    }
}
