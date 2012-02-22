package org.motechproject.tama.clinicvisits.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientAlertType;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Properties;

import static org.mockito.Mockito.*;

public class AppointmentReminderServiceTest extends BaseUnitTest {

    private static final Integer daysBeforeReminderAlert = 2;

    @Mock
    PatientAlertService patientAlertService;
    @Mock
    private OutboxService outboxService;
    @Mock
    private AppointmentService appointmentService;

    private Properties appointmentProperties = new Properties();
    private AppointmentReminderService appointmentReminderService;

    public AppointmentReminderServiceTest() {
        appointmentProperties.put(TAMAConstants.DAYS_BEFORE_DUE_DATE_WHEN_ALERT_SHOULD_BE_RAISED, daysBeforeReminderAlert.toString());
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        appointmentReminderService = new AppointmentReminderService(patientAlertService, appointmentProperties, outboxService);
    }

    @Test
    public void shouldCreateOutboxMessageIfThereAreNoPendingMessages() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patientDocId").build();
        when(outboxService.hasPendingOutboxMessages(patient.getId(), TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE)).thenReturn(false);

        appointmentReminderService.raiseOutboxMessage(patient);
        verify(outboxService).addMessage(patient.getId(), TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE);
    }

    @Test
    public void shouldNotCreateOutboxMessageIfThereArePendingMessages() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patientDocId").build();
        when(outboxService.hasPendingOutboxMessages(patient.getId(), TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE)).thenReturn(true);

        appointmentReminderService.raiseOutboxMessage(patient);
        verify(outboxService, never()).addMessage(patient.getId(), TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE);
    }

    @Test
    public void shouldNotCreateOutboxMessageIfPatientHasNotOptedToReceiveAppointmentReminders() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patientDocId").withAppointmentReminderPreference(false).build();
        when(outboxService.hasPendingOutboxMessages(patient.getId(), TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE)).thenReturn(false);

        appointmentReminderService.raiseOutboxMessage(patient);
        verify(outboxService, never()).addMessage(patient.getId(), TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE);
    }

    @Test
    public void shouldRaiseAppointmentReminderAlertWhenTodayIsMDaysBeforeDueDate() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patientId").build();
        Appointment appointment = new Appointment().dueDate(DateUtil.now().plusDays(daysBeforeReminderAlert));

        appointmentReminderService.raiseReminderAlert(patient, appointment);
        verify(patientAlertService).createAlert(patient.getId(), TAMAConstants.NO_ALERT_PRIORITY, TAMAConstants.APPOINTMENT_REMINDER, "", PatientAlertType.AppointmentReminder, null);
    }

    @Test
    public void shouldRaiseAppointmentReminderAlertWhenTodayIsMDaysBeforeAdjustedDuDate() {
        DateTime now = DateUtil.now();
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patientId").build();
        Appointment appointment = new Appointment().dueDate(now.minusDays(1));
        appointment.addData(ClinicVisit.ADJUSTED_DUE_DATE, now.plusDays(daysBeforeReminderAlert).toLocalDate().toString());

        appointmentReminderService.raiseReminderAlert(patient, appointment);
        verify(patientAlertService).createAlert(patient.getId(), TAMAConstants.NO_ALERT_PRIORITY, TAMAConstants.APPOINTMENT_REMINDER, "", PatientAlertType.AppointmentReminder, null);
    }

    @Test
    public void shouldNotRaiseAppointmentReminderAlertWhenTodayIsNotMDaysBeforeAdjustedDuDate() {
        DateTime now = DateUtil.now();
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patientId").build();
        Appointment appointment = new Appointment().dueDate(now.plusDays(daysBeforeReminderAlert));
        appointment.addData(ClinicVisit.ADJUSTED_DUE_DATE, now.plusDays(daysBeforeReminderAlert + 2).toLocalDate().toString());

        appointmentReminderService.raiseReminderAlert(patient, appointment);
        verifyZeroInteractions(patientAlertService);
    }

    @Test
    public void shouldNotRaiseAppointmentReminderAlertWhenTodayIsBeforeReminderAlertDay() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patientId").build();
        Appointment appointment = new Appointment().dueDate(DateUtil.now().plusDays(daysBeforeReminderAlert + 1));

        when(appointmentService.getAppointment(appointment.id())).thenReturn(appointment);
        appointmentReminderService.raiseReminderAlert(patient, appointment);
        verifyZeroInteractions(patientAlertService);
    }

    @Test
    public void shouldNotRaiseAppointmentReminderAlertWhenTodayIsAfterReminderAlertDay() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patientId").build();
        Appointment appointment = new Appointment().dueDate(DateUtil.now().plusDays(daysBeforeReminderAlert - 1));

        when(appointmentService.getAppointment(appointment.id())).thenReturn(appointment);
        appointmentReminderService.raiseReminderAlert(patient, appointment);
        verifyZeroInteractions(patientAlertService);
    }
}
