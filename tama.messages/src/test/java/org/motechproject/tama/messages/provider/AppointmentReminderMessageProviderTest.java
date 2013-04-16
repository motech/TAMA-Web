package org.motechproject.tama.messages.provider;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.domain.Appointment;
import org.motechproject.tama.clinicvisits.domain.TAMAReminderConfiguration;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.domain.TAMAMessageType;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.domain.MessageHistory;
import org.motechproject.tama.messages.service.MessageTrackingService;
import org.motechproject.tama.messages.service.PatientOnCall;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientPreferences;
import org.motechproject.testing.utils.BaseUnitTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.now;

public class AppointmentReminderMessageProviderTest extends BaseUnitTest {

    @Mock
    private MessageTrackingService messageTrackingService;
    @Mock
    private PatientOnCall patientOnCall;
    @Mock
    private TAMAIVRContext context;
    @Mock
    private MessageHistory messageHistory;
    @Mock
    private Appointment appointment;
    @Mock
    private Clinic clinic;
    @Mock
    private TAMAReminderConfiguration tamaReminderConfiguration;

    private Patient patient;

    private AppointmentReminderMessageProvider appointmentReminderMessageProvider;


    @Before
    public void setup() {
        initMocks(this);
        appointmentReminderMessageProvider = new AppointmentReminderMessageProvider(messageTrackingService, patientOnCall, tamaReminderConfiguration);
        when(tamaReminderConfiguration.getRemindAppointmentsFrom()).thenReturn(7);
        when(tamaReminderConfiguration.getPushedAppointmentReminderVoiceMessageCount()).thenReturn(2);
        setupPatient();
    }

    private void setupPatient() {
        when(clinic.getPhone()).thenReturn("1234567890");
        patient = PatientBuilder.startRecording().withDefaults().withClinic(clinic).build();
        when(patientOnCall.getUpcomingAppointment(eq(context), any(LocalDate.class))).thenReturn(appointment);
        when(patientOnCall.getPatient(context)).thenReturn(patient);
    }

    private void updateAppointmentReminderPreference(boolean isAppointmentReminderActivated) {
        PatientPreferences patientPreferences = patient.getPatientPreferences();
        patientPreferences.setActivateAppointmentReminders(isAppointmentReminderActivated);
        patient.setPatientPreferences(patientPreferences);
    }

    @Test
    public void shouldNotPlayAppointmentReminderWhenMessageTypeIsNotPushOrAllTAMAMessages() {
        DateTime now = now();
        mockCurrentDate(now);
        updateAppointmentReminderPreference(true);
        when(appointment.isUpcoming()).thenReturn(true);
        when(appointment.getDueDate()).thenReturn(now.toLocalDate());
        when(messageHistory.getCount()).thenReturn(1);
        when(messageTrackingService.get(eq(AppointmentReminderMessageProvider.MESSAGE_TYPE), anyString())).thenReturn(messageHistory);

        assertFalse(appointmentReminderMessageProvider.hasMessage(context, TAMAMessageType.ADHERENCE_TO_ART));
    }

    @Test
    public void shouldHaveAMessageIfMessageIsValidAndIsPlayedLessThanTwoTimesAndAppointmentReminderIsActivated() {
        DateTime now = now();
        mockCurrentDate(now);
        updateAppointmentReminderPreference(true);
        when(appointment.isUpcoming()).thenReturn(true);
        when(appointment.getDueDate()).thenReturn(now.toLocalDate());
        when(messageHistory.getCount()).thenReturn(1);
        when(messageTrackingService.get(eq(AppointmentReminderMessageProvider.MESSAGE_TYPE), anyString())).thenReturn(messageHistory);

        assertTrue(appointmentReminderMessageProvider.hasMessage(context, org.motechproject.tama.common.domain.TAMAMessageType.PUSHED_MESSAGE));
    }

    @Test
    public void shouldNotHaveMessagesIfMessageIsValidAndIsPlayedLessThanTwoTimesButAppointmentReminderPreferenceIsNotActivated() {
        DateTime now = now();
        mockCurrentDate(now);
        updateAppointmentReminderPreference(false);
        when(appointment.isUpcoming()).thenReturn(true);
        when(appointment.getDueDate()).thenReturn(now.toLocalDate());
        when(messageHistory.getCount()).thenReturn(1);
        when(messageTrackingService.get(eq(AppointmentReminderMessageProvider.MESSAGE_TYPE), anyString())).thenReturn(messageHistory);

        assertFalse(appointmentReminderMessageProvider.hasMessage(context, org.motechproject.tama.common.domain.TAMAMessageType.PUSHED_MESSAGE));
    }

    @Test
    public void shouldNotHaveMessageIfMessageIsValidAndIsPlayedGreaterThanTwoTimes() {
        DateTime now = now();
        mockCurrentDate(now);

        when(appointment.isUpcoming()).thenReturn(true);
        when(appointment.getDueDate()).thenReturn(now.toLocalDate());
        when(messageHistory.getCount()).thenReturn(3);
        when(messageTrackingService.get(eq(AppointmentReminderMessageProvider.MESSAGE_TYPE), anyString())).thenReturn(messageHistory);

        assertFalse(appointmentReminderMessageProvider.hasMessage(context, org.motechproject.tama.common.domain.TAMAMessageType.PUSHED_MESSAGE));
    }

    @Test
    public void shouldNotHaveMessageWhenLastPlayedMessagesIsAlsoAppointmentReminder() {
        DateTime now = now();
        mockCurrentDate(now);

        when(appointment.isUpcoming()).thenReturn(true);
        when(appointment.getDueDate()).thenReturn(now.toLocalDate());
        when(context.getTAMAMessageType()).thenReturn(AppointmentReminderMessageProvider.MESSAGE_TYPE);
        when(messageHistory.getCount()).thenReturn(3);
        when(messageTrackingService.get(eq(AppointmentReminderMessageProvider.MESSAGE_TYPE), anyString())).thenReturn(messageHistory);

        assertFalse(appointmentReminderMessageProvider.hasMessage(context, TAMAMessageType.ALL_MESSAGES));
    }

    @Test
    public void shouldHaveMessageIfMessageIsValidAndIsPlayedGreaterThanTwoTimesAndTypeIsNotPushedMessage() {
        DateTime now = now();
        mockCurrentDate(now);

        when(appointment.isUpcoming()).thenReturn(true);
        when(appointment.getDueDate()).thenReturn(now.toLocalDate());
        when(messageHistory.getCount()).thenReturn(3);
        when(messageTrackingService.get(eq(AppointmentReminderMessageProvider.MESSAGE_TYPE), anyString())).thenReturn(messageHistory);

        assertTrue(appointmentReminderMessageProvider.hasMessage(context, TAMAMessageType.ALL_MESSAGES));
    }

    @Test
    public void shouldAddMessageTypeToContext() {
        appointmentReminderMessageProvider.nextMessage(context);
        verify(context).setTAMAMessageType(TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE);
    }

    @Test
    public void shouldAddLastPlayedMessageIdToContext() {
        appointmentReminderMessageProvider.nextMessage(context);
        verify(context).lastPlayedMessageId(anyString());
    }
}
