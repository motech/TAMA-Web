package org.motechproject.tama.messages.provider;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.domain.MessageHistory;
import org.motechproject.tama.messages.service.MessageTrackingService;
import org.motechproject.tama.messages.service.PatientOnCall;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.now;

public class VisitReminderMessageProviderTest extends BaseUnitTest {

    @Mock
    private ClinicVisits clinicVisits;
    @Mock
    private MessageTrackingService messageTrackingService;
    @Mock
    private PatientOnCall patientOnCall;
    @Mock
    private MessageHistory messageHistory;
    @Mock
    private TAMAIVRContext context;
    @Mock
    private ClinicVisit clinicVisit;

    private Patient patient;

    private VisitReminderMessageProvider visitReminderMessageProvider;

    @Before
    public void setup() {
        initMocks(this);
        setupPatient();
        setupClinicVisits();
        visitReminderMessageProvider = new VisitReminderMessageProvider(patientOnCall, messageTrackingService);
    }

    private void setupClinicVisits() {
        when(patientOnCall.getClinicVisits(context)).thenReturn(clinicVisits);
        when(clinicVisit.isUpcoming()).thenReturn(true);
        when(clinicVisits.upcomingVisit(any(DateTime.class))).thenReturn(clinicVisit);
    }

    private void setupPatient() {
        patient = PatientBuilder.startRecording().withDefaults().build();
        when(patientOnCall.getPatient(context)).thenReturn(patient);
    }

    @Test
    public void shouldHaveMessageWhenMessageIsValidAndIsPlayedLessThanTwoTimes() {
        DateTime now = now();
        mockCurrentDate(now);

        when(clinicVisit.isUpcoming()).thenReturn(true);
        when(clinicVisit.getConfirmedAppointmentDate()).thenReturn(now);

        when(messageHistory.getCount()).thenReturn(1);
        when(messageTrackingService.get(eq(VisitReminderMessageProvider.MESSAGE_TYPE), anyString())).thenReturn(messageHistory);

        assertTrue(visitReminderMessageProvider.hasMessage(context));
    }

    @Test
    public void shouldNotHaveMessageWhenMessageIsPlayedEqualToTwoTimes() {
        DateTime now = now();
        mockCurrentDate(now);

        when(clinicVisit.isUpcoming()).thenReturn(true);
        when(clinicVisit.getConfirmedAppointmentDate()).thenReturn(now);

        when(messageHistory.getCount()).thenReturn(2);
        when(messageTrackingService.get(eq(VisitReminderMessageProvider.MESSAGE_TYPE), anyString())).thenReturn(messageHistory);

        assertFalse(visitReminderMessageProvider.hasMessage(context));
    }

    @Test
    public void shouldAddMessageTypeToContext() {
        when(clinicVisit.getConfirmedAppointmentDate()).thenReturn(DateUtil.now());
        visitReminderMessageProvider.nextMessage(context);
        verify(context).setTAMAMessageType(VisitReminderMessageProvider.MESSAGE_TYPE);
    }

    @Test
    public void shouldAddMessageIdToContext() {
        when(clinicVisit.getConfirmedAppointmentDate()).thenReturn(DateUtil.now());
        visitReminderMessageProvider.nextMessage(context);
        verify(context).lastPlayedMessageId(anyString());
    }
}
