package org.motechproject.tama.outbox.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TimeMeridiem;
import org.motechproject.tama.patient.domain.TimeOfDay;
import org.motechproject.tama.patient.service.PatientService;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class OutboxServiceTest {

    @Mock
    protected VoiceOutboxService voiceOutboxService;
    @Mock
    protected PatientService patientService;
    @Mock
    protected OutboxSchedulerService outboxSchedulerService;
    private OutboxService outboxService;

    @Before
    public void setUp() {
        initMocks(this);
        outboxService = new OutboxService(voiceOutboxService, outboxSchedulerService, patientService);
    }

    @Test
    public void patientIsNotEnrolled_ToOutbox_WhenHeHasNotAgreedToBeCalledAtBestCallTime() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        outboxService.enroll(patient);

        verify(patientService).registerOutbox(outboxService);
        verify(outboxSchedulerService, never()).scheduleOutboxJobs(patient);
    }

    @Test
    public void patientEnrolls_ToOutbox_WhenAgreedToBeCalledAtBestCallTime() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withBestCallTime(new TimeOfDay(5, 30, TimeMeridiem.AM)).build();
        outboxService.enroll(patient);

        verify(patientService).registerOutbox(outboxService);
        verify(outboxSchedulerService).scheduleOutboxJobs(patient);
    }

    @Test
    public void patientReEnrolls_ToOutbox_WhenHeHasAlwaysAgreedToBeCalledAtBestCallTime_AndHisBestCallTimeChanges() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withBestCallTime(new TimeOfDay(5, 30, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withBestCallTime(new TimeOfDay(10, 30, TimeMeridiem.AM)).build();
        outboxService.reEnroll(dbPatient, patient);

        verify(patientService).registerOutbox(outboxService);
        verify(outboxSchedulerService).unscheduleOutboxJobs(dbPatient);
        verify(outboxSchedulerService).scheduleOutboxJobs(patient);
    }

    @Test
    public void patientReEnrolls_ToOutbox_WhenHeHasAlwaysAgreedToBeCalledAtBestCallTime_AndHisDayOfWeeklyCallChanges() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(5, 30, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Monday, new TimeOfDay(5, 30, TimeMeridiem.AM)).build();
        outboxService.reEnroll(dbPatient, patient);

        verify(patientService).registerOutbox(outboxService);
        verify(outboxSchedulerService).unscheduleOutboxJobs(dbPatient);
        verify(outboxSchedulerService).scheduleOutboxJobs(patient);
    }

    @Test
    public void patientReEnrolls_ToOutbox_WhenHeFirstAgreesToBeCalledAtBestCallTime() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withBestCallTime(new TimeOfDay(10, 30, TimeMeridiem.AM)).build();
        outboxService.reEnroll(dbPatient, patient);

        verify(patientService).registerOutbox(outboxService);
        verify(outboxSchedulerService, never()).unscheduleOutboxJobs(dbPatient);
        verify(outboxSchedulerService).scheduleOutboxJobs(patient);
    }

    @Test
    public void patientDisEnrolls_FromOutbox_WhenNoLongerAgreesToBeCalledAtBestCallTime() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withBestCallTime(new TimeOfDay(5, 30, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        outboxService.reEnroll(dbPatient, patient);

        verify(patientService).registerOutbox(outboxService);
        verify(outboxSchedulerService).unscheduleOutboxJobs(dbPatient);
        verify(outboxSchedulerService, never()).scheduleOutboxJobs(patient);
    }

    @Test
    public void shouldCreateVoiceMessage() {
        final String patientId = "patientId";
        outboxService.addMessage(patientId);
        verify(voiceOutboxService).addMessage(Matchers.<OutboundVoiceMessage>any());
    }
}
