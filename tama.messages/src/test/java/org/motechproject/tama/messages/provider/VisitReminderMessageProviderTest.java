package org.motechproject.tama.messages.provider;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.clinicvisits.domain.TAMAReminderConfiguration;
import org.motechproject.tama.common.domain.TAMAMessageType;
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
    @Mock
    private TAMAReminderConfiguration tamaReminderConfiguration;

    private Patient patient;

    private VisitReminderMessageProvider visitReminderMessageProvider;


    @Before
    public void setup() {
        initMocks(this);
        setupPatient();
        setupClinicVisits();
        when(tamaReminderConfiguration.getVisitReminderFrom()).thenReturn(3);
        when(tamaReminderConfiguration.getPushedVisitReminderVoiceMessageCount()).thenReturn(2);

        visitReminderMessageProvider = new VisitReminderMessageProvider(patientOnCall, messageTrackingService, tamaReminderConfiguration);
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
    public void shouldNotHaveMessageWhenTypeIsNotPushedOrAllMessages() {
        DateTime now = now();
        mockCurrentDate(now);

        when(clinicVisit.isUpcoming()).thenReturn(true);
        when(clinicVisit.getConfirmedAppointmentDate()).thenReturn(now);

        when(messageHistory.getCount()).thenReturn(1);
        when(messageTrackingService.get(eq(VisitReminderMessageProvider.MESSAGE_TYPE), anyString())).thenReturn(messageHistory);

        assertFalse(visitReminderMessageProvider.hasMessage(context, TAMAMessageType.ADHERENCE_TO_ART));
    }

    @Test
    public void shouldHaveMessageWhenMessageIsValidAndIsPlayedLessThanTwoTimes() {
        DateTime now = now();
        mockCurrentDate(now);

        when(clinicVisit.isUpcoming()).thenReturn(true);
        when(clinicVisit.getConfirmedAppointmentDate()).thenReturn(now);

        when(messageHistory.getCount()).thenReturn(1);
        when(messageTrackingService.get(eq(VisitReminderMessageProvider.MESSAGE_TYPE), anyString())).thenReturn(messageHistory);

        assertTrue(visitReminderMessageProvider.hasMessage(context, org.motechproject.tama.common.domain.TAMAMessageType.PUSHED_MESSAGE));
    }

    @Test
    public void shouldNotHaveMessageWhenMessageIsPlayedEqualToTwoTimes() {
        DateTime now = now();
        mockCurrentDate(now);

        when(clinicVisit.isUpcoming()).thenReturn(true);
        when(clinicVisit.getConfirmedAppointmentDate()).thenReturn(now);

        when(messageHistory.getCount()).thenReturn(2);
        when(messageTrackingService.get(eq(VisitReminderMessageProvider.MESSAGE_TYPE), anyString())).thenReturn(messageHistory);

        assertFalse(visitReminderMessageProvider.hasMessage(context, org.motechproject.tama.common.domain.TAMAMessageType.PUSHED_MESSAGE));
    }

    @Test
    public void shouldNotHaveMessageWhenLastPlayedMessageIsAlsoVisitReminder() {
        DateTime now = now();
        mockCurrentDate(now);

        when(clinicVisit.isUpcoming()).thenReturn(true);
        when(clinicVisit.getConfirmedAppointmentDate()).thenReturn(now);

        when(messageHistory.getCount()).thenReturn(2);
        when(context.getTAMAMessageType()).thenReturn(VisitReminderMessageProvider.MESSAGE_TYPE);
        when(messageTrackingService.get(eq(VisitReminderMessageProvider.MESSAGE_TYPE), anyString())).thenReturn(messageHistory);

        assertFalse(visitReminderMessageProvider.hasMessage(context, TAMAMessageType.ALL_MESSAGES));
    }

    @Test
    public void shouldHaveMessageWhenMessageIsPlayedEqualToTwoTimesAndTheMessageIsNotBeingPushed() {
        DateTime now = now();
        mockCurrentDate(now);

        when(clinicVisit.isUpcoming()).thenReturn(true);
        when(clinicVisit.getConfirmedAppointmentDate()).thenReturn(now);

        when(messageHistory.getCount()).thenReturn(2);
        when(messageTrackingService.get(eq(VisitReminderMessageProvider.MESSAGE_TYPE), anyString())).thenReturn(messageHistory);

        assertTrue(visitReminderMessageProvider.hasMessage(context, TAMAMessageType.ALL_MESSAGES));
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
