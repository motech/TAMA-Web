package org.motechproject.tama.dailypillreminder.call;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.tama.dailypillreminder.domain.Dose;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Map;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PillReminderCallTest extends BaseUnitTest {

    private DateTime NOW = DateUtil.now();

    private PillReminderCall pillReminderCall;
    @Mock
    private AllPatients allPatients;
    @Mock
    private IVRService callService;
    @Mock
    private DailyPillReminderService dailyPillReminderService;
    @Mock
    private DailyPillReminderAdherenceService dailyPillReminderAdherenceService;

    private String PATIENT_DOC_ID = "P_1";
    private String DOSAGE_ID = "D_1";
    private int TOTAL_TIMES_TO_SEND = 2;
    private int TIMES_SENT = 0;
    private int RETRY_INTERVAL = 5;

    @Before
    public void setUp() {
        initMocks(this);
        pillReminderCall = Mockito.spy(new PillReminderCall(callService, allPatients, dailyPillReminderService, dailyPillReminderAdherenceService, new Properties()));
        Mockito.doReturn("").when(pillReminderCall).getApplicationUrl();
        mockCurrentDate(NOW);
    }

    @Test
    public void shouldNotMakeACallForANonExistentPatient() {
        when(allPatients.get(PATIENT_DOC_ID)).thenReturn(null);
        pillReminderCall.execute(PATIENT_DOC_ID, NOW.toDate(), TIMES_SENT, TOTAL_TIMES_TO_SEND, RETRY_INTERVAL);
        verify(callService, never()).initiateCall(any(CallRequest.class));
    }

    @Test
    public void shouldNotMakeACallForSuspendedPatient() {
        Patient patient = mock(Patient.class);
        when(patient.allowAdherenceCalls()).thenReturn(false);
        when(allPatients.get(PATIENT_DOC_ID)).thenReturn(patient);

        pillReminderCall.execute(PATIENT_DOC_ID, NOW.toDate(), TIMES_SENT, TOTAL_TIMES_TO_SEND, RETRY_INTERVAL);

        verify(callService, never()).initiateCall(any(CallRequest.class));
    }

    @Test
    public void shouldMakeACallForActivePatient_AndRecordDosageAsNotReported_WhenCalledFirstTime() {
        String PHONE_NUMBER = "1234567890";
        Patient patient = mock(Patient.class);
        PillRegimen pillRegimen = mock(PillRegimen.class);
        Dose dose = mock(Dose.class);
        DateTime now = DateUtil.now();

        when(patient.allowAdherenceCalls()).thenReturn(true);
        when(patient.getMobilePhoneNumber()).thenReturn(PHONE_NUMBER);
        when(allPatients.get(PATIENT_DOC_ID)).thenReturn(patient);

        when(dailyPillReminderService.getPillRegimen(anyString())).thenReturn(pillRegimen);
        when(pillRegimen.getDoseAt(Matchers.<DateTime>any())).thenReturn(dose);
        when(pillRegimen.getId()).thenReturn("pillRegimenId");
        when(dose.getDoseTime()).thenReturn(now);

        pillReminderCall.execute(PATIENT_DOC_ID, NOW.toDate(), TIMES_SENT, TOTAL_TIMES_TO_SEND, RETRY_INTERVAL);

        ArgumentCaptor<CallRequest> callRequestArgumentCaptor = ArgumentCaptor.forClass(CallRequest.class);
        verify(callService).initiateCall(callRequestArgumentCaptor.capture());
        verify(dailyPillReminderAdherenceService).recordDosageAdherenceAsNotCaptured(PATIENT_DOC_ID, "pillRegimenId", dose, DosageStatus.NOT_RECORDED, now);
        Map<String, String> payload = callRequestArgumentCaptor.getValue().getPayload();
        assertEquals(String.valueOf(TOTAL_TIMES_TO_SEND), payload.get(PillReminderCall.TOTAL_TIMES_TO_SEND));
        assertEquals(String.valueOf(TIMES_SENT), payload.get(PillReminderCall.TIMES_SENT));
        assertEquals(String.valueOf(RETRY_INTERVAL), payload.get(PillReminderCall.RETRY_INTERVAL));
    }

    @Test
    public void shouldNotMakeACallWhenCurrentTimeIsConfiguredMinutesMoreThanScheduledTime() {
        String PHONE_NUMBER = "1234567890";
        Patient patient = mock(Patient.class);
        PillRegimen pillRegimen = mock(PillRegimen.class);
        Dose dose = mock(Dose.class);
        DateTime now = DateUtil.now();

        when(patient.allowAdherenceCalls()).thenReturn(true);
        when(patient.getMobilePhoneNumber()).thenReturn(PHONE_NUMBER);
        when(allPatients.get(PATIENT_DOC_ID)).thenReturn(patient);

        when(dailyPillReminderService.getPillRegimen(anyString())).thenReturn(pillRegimen);
        when(pillRegimen.getDoseAt(Matchers.<DateTime>any())).thenReturn(dose);
        when(pillRegimen.getId()).thenReturn("pillRegimenId");
        when(dose.getDoseTime()).thenReturn(now);

        pillReminderCall.execute(PATIENT_DOC_ID, NOW.plusMinutes(16).toDate(), TIMES_SENT, TOTAL_TIMES_TO_SEND, RETRY_INTERVAL);

        ArgumentCaptor<CallRequest> callRequestArgumentCaptor = ArgumentCaptor.forClass(CallRequest.class);
        verify(callService, never()).initiateCall(callRequestArgumentCaptor.capture());
    }

    @Test
    public void shouldMarkDoseAsNotRecordedWhenCurrentTimeIsConfiguredMinutesMoreThanScheduledTime() {
        String PHONE_NUMBER = "1234567890";
        Patient patient = mock(Patient.class);
        PillRegimen pillRegimen = mock(PillRegimen.class);
        Dose dose = mock(Dose.class);
        DateTime now = DateUtil.now();

        when(patient.allowAdherenceCalls()).thenReturn(true);
        when(patient.getMobilePhoneNumber()).thenReturn(PHONE_NUMBER);
        when(allPatients.get(PATIENT_DOC_ID)).thenReturn(patient);

        when(dailyPillReminderService.getPillRegimen(anyString())).thenReturn(pillRegimen);
        when(pillRegimen.getDoseAt(Matchers.<DateTime>any())).thenReturn(dose);
        when(pillRegimen.getId()).thenReturn("pillRegimenId");
        when(dose.getDoseTime()).thenReturn(now);

        pillReminderCall.execute(PATIENT_DOC_ID, NOW.plusMinutes(16).toDate(), TIMES_SENT, TOTAL_TIMES_TO_SEND, RETRY_INTERVAL);

        verify(dailyPillReminderAdherenceService).recordDosageAdherenceAsNotCaptured(PATIENT_DOC_ID, "pillRegimenId", dose, DosageStatus.NOT_RECORDED, now);
    }

    @Test
    public void shouldMakeACallForActivePatient_DoesNotRecordDosageAsNotReported_WhenCalledAfterFirstTime() {
        String PHONE_NUMBER = "1234567890";
        Patient patient = mock(Patient.class);
        PillRegimen pillRegimen = mock(PillRegimen.class);
        Dose dose = mock(Dose.class);
        DateTime now = DateUtil.now();
        TIMES_SENT = 1;

        when(patient.allowAdherenceCalls()).thenReturn(true);
        when(patient.getMobilePhoneNumber()).thenReturn(PHONE_NUMBER);
        when(allPatients.get(PATIENT_DOC_ID)).thenReturn(patient);

        pillReminderCall.execute(PATIENT_DOC_ID, NOW.toDate(), TIMES_SENT, TOTAL_TIMES_TO_SEND, RETRY_INTERVAL);

        ArgumentCaptor<CallRequest> callRequestArgumentCaptor = ArgumentCaptor.forClass(CallRequest.class);
        verify(callService).initiateCall(callRequestArgumentCaptor.capture());
        verifyZeroInteractions(dailyPillReminderAdherenceService);
        verifyZeroInteractions(dailyPillReminderService);
        Map<String, String> payload = callRequestArgumentCaptor.getValue().getPayload();
        assertEquals(String.valueOf(TOTAL_TIMES_TO_SEND), payload.get(PillReminderCall.TOTAL_TIMES_TO_SEND));
        assertEquals(String.valueOf(TIMES_SENT), payload.get(PillReminderCall.TIMES_SENT));
        assertEquals(String.valueOf(RETRY_INTERVAL), payload.get(PillReminderCall.RETRY_INTERVAL));
    }
}