package org.motechproject.tama.clinicvisits.domain.criteria;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class ReminderOutboxCriteriaTest {

    @Mock
    OutboxService outboxService;

    ReminderOutboxCriteria reminderOutboxCriteria;

    @Before
    public void setUp() {
        initMocks(this);

        reminderOutboxCriteria = new ReminderOutboxCriteria(outboxService);
    }

    @Test
    public void shouldReturnFalseIfPatientHasOptedNotToReceiveAppointmentReminder() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(false).build();
        Appointment appointment = new Appointment();
        assertFalse(reminderOutboxCriteria.shouldAddOutboxMessageForAppointments(patient, appointment));
    }

    @Test
    public void shouldReturnFalseIfPatientHasPendingAppointmentOutboxMessage() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        Appointment appointment = new Appointment();
        when(outboxService.hasPendingOutboxMessages(patient.getId(),
                TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE)).thenReturn(true);
        assertFalse(reminderOutboxCriteria.shouldAddOutboxMessageForAppointments(patient, appointment));
    }


    @Test
    public void shouldReturnFalseIfPatientHasPendingVisitOutboxMessage() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        Appointment appointment = new Appointment();
        when(outboxService.hasPendingOutboxMessages(patient.getId(),
                TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE)).thenReturn(true);
        assertFalse(reminderOutboxCriteria.shouldAddOutboxMessageForVisits(patient, appointment));
    }

    @Test
    public void shouldReturnFalseIfAppointmentIsAlreadyConfirmed() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        Appointment appointment = new Appointment().confirmedDate(DateUtil.now());
        assertFalse(reminderOutboxCriteria.shouldAddOutboxMessageForAppointments(patient, appointment));
    }

    @Test
    public void shouldReturnFalseIfAppointmentIsNotConfirmed() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        Appointment appointment = new Appointment();
        assertFalse(reminderOutboxCriteria.shouldAddOutboxMessageForVisits(patient, appointment));
    }

    @Test
    public void shouldReturnFalseIfConfirmedDateIsBeforeToday() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        Appointment appointment = new Appointment().confirmedDate(DateTime.now().minusHours(2));
        assertFalse(reminderOutboxCriteria.shouldAddOutboxMessageForVisits(patient, appointment));
    }

    @Test
    public void shouldReturnTrueIfAllVisitOutboxMessageCriteriaSatisfied() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        Appointment appointmentWithFutureConfirmDate = new Appointment().confirmedDate(DateTime.now().plusHours(2));
        assertTrue(reminderOutboxCriteria.shouldAddOutboxMessageForVisits(patient, appointmentWithFutureConfirmDate));
    }

    @Test
    public void shouldReturnTrue() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        Appointment appointment = new Appointment();
        when(outboxService.hasPendingOutboxMessages(patient.getId(),
                TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE)).thenReturn(false);
        assertTrue(reminderOutboxCriteria.shouldAddOutboxMessageForAppointments(patient, appointment));
    }


}
