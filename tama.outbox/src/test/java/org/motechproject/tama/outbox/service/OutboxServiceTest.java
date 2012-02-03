package org.motechproject.tama.outbox.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.ivr.call.IVRCall;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.patient.domain.TimeMeridiem;
import org.motechproject.tama.patient.domain.TimeOfDay;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.service.PatientService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.server.pillreminder.EventKeys.EXTERNAL_ID_KEY;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

public class OutboxServiceTest {

    @Mock
    protected VoiceOutboxService voiceOutboxService;
    @Mock
    protected PatientService patientService;
    @Mock
    private IVRCall ivrCall;
    @Mock
    private AllPatients allPatients;
    @Mock
    protected OutboxSchedulerService outboxSchedulerService;
    private OutboxService outboxService;
    private Patient patient;
    private MotechEvent motechEvent;

    @Before
    public void setUp() {
        initMocks(this);
        outboxService = new OutboxService(allPatients, ivrCall, voiceOutboxService, outboxSchedulerService, patientService);
        setUpEventWithExternalId();
        setUpPatientAsActive();
        setUpOutboxWithAtLeastOneUnreadMessage();
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
    public void patientReEnrolls_ToOutbox_WhenHeHasAlwaysAgreedToBeCalledAtBestCallTime() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withBestCallTime(new TimeOfDay(5, 30, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withBestCallTime(new TimeOfDay(5, 30, TimeMeridiem.AM)).build();
        outboxService.reEnroll(dbPatient, patient);

        verify(patientService).registerOutbox(outboxService);
        verify(outboxSchedulerService).unscheduleOutboxJobs(dbPatient);
        verify(outboxSchedulerService).scheduleOutboxJobs(patient);
    }

    @Test
    public void patientOnWeeklyPillReminderEnrolls_ToOutbox() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Monday, new TimeOfDay(5, 30, TimeMeridiem.AM)).build();
        outboxService.enroll(patient);

        verify(patientService).registerOutbox(outboxService);
        verify(outboxSchedulerService, never()).scheduleOutboxJobs(patient);
    }

    @Test
    public void patientOnWeeklyPillReminderDisEnrolls_ToOutbox() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Monday, new TimeOfDay(5, 30, TimeMeridiem.AM)).build();
        outboxService.disEnroll(patient);

        verify(patientService).registerOutbox(outboxService);
        verify(outboxSchedulerService, never()).unscheduleOutboxJobs(patient);
    }

    @Test
    public void shouldCreateVoiceMessage() {
        final String patientId = "patientId";
        outboxService.addMessage(patientId);
        verify(voiceOutboxService).addMessage(Matchers.<OutboundVoiceMessage>any());
    }

    @Test
    public void shouldMakeACall() {
        patient.setStatus(Status.Active);
        outboxService.call(motechEvent);
        verify(ivrCall).makeCall(same(patient), Matchers.<Map<String, String>>any());
        verify(outboxSchedulerService).scheduleRepeatingJobForOutBoxCall(patient);
    }

    @Test
    public void shouldMakeACallEvenWhenPatientIsSuspended() {
        patient.setStatus(Status.Suspended);
        Mockito.when(allPatients.get(EXTERNAL_ID_KEY)).thenReturn(patient);

        outboxService.call(motechEvent);
        verify(ivrCall).makeCall(same(patient), Matchers.<Map<String, String>>any());
        verify(outboxSchedulerService).scheduleRepeatingJobForOutBoxCall(patient);
    }

    @Test
    public void shouldNotMakeACallEvenWhenPatientIsInactive() {
        patient.setStatus(Status.Loss_To_Follow_Up);
        Mockito.when(allPatients.get(EXTERNAL_ID_KEY)).thenReturn(patient);

        outboxService.call(motechEvent);
        verifyZeroInteractions(ivrCall);
        verifyZeroInteractions(outboxSchedulerService);
    }

    public void setUpEventWithExternalId() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EXTERNAL_ID_KEY, "patientId");
        motechEvent = new MotechEvent(TAMAConstants.OUTBOX_CALL_SCHEDULER_SUBJECT, parameters);
    }

    private void setUpPatientAsActive() {
        patient = PatientBuilder.startRecording().withDefaults().withMobileNumber("0000000000").withPatientId("111").build();
        patient.activate();
        when(allPatients.get(anyString())).thenReturn(patient);
    }

    private void setUpOutboxWithAtLeastOneUnreadMessage() {
        when(voiceOutboxService.getNumberPendingMessages("patientId")).thenReturn(1);
    }
}
