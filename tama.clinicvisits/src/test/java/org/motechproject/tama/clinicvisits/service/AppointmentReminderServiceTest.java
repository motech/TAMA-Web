package org.motechproject.tama.clinicvisits.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.tama.clinicvisits.domain.criteria.ReminderAlertCriteria;
import org.motechproject.tama.clinicvisits.domain.criteria.ReminderOutboxCriteria;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientAlert;
import org.motechproject.tama.patient.domain.PatientAlertType;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.util.DateUtil;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AppointmentReminderServiceTest {

    @Mock
    private ReminderOutboxCriteria reminderOutboxCriteria;
    @Mock
    private ReminderAlertCriteria reminderAlertCriteria;
    @Mock
    private OutboxService outboxService;
    @Mock
    PatientAlertService patientAlertService;

    private Patient patient;
    private Appointment appointment;

    private AppointmentReminderService appointmentReminderService;

    public AppointmentReminderServiceTest() {
        patient = PatientBuilder.startRecording().withDefaults().withId("patientDocumentId").build();
        appointment = new Appointment();
    }

    @Before
    public void setup() {
        initMocks(this);
        appointmentReminderService = new AppointmentReminderService(reminderOutboxCriteria, reminderAlertCriteria, patientAlertService, outboxService);
    }


    @Test
    public void shouldAddOutboxMessageWhenReminderOutboxCriteriaIsTrue() {
        when(reminderOutboxCriteria.shouldAddOutboxMessageForAppointments(patient, appointment)).thenReturn(true);
        appointmentReminderService.addOutboxMessage(patient, appointment);
        verify(outboxService).addMessage(patient.getId(), TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE);
    }

    @Test
    public void shouldNotAddOutboxMessageWhenReminderOutboxCriteriaIsFalse() {
        when(reminderOutboxCriteria.shouldAddOutboxMessageForAppointments(patient, appointment)).thenReturn(false);
        appointmentReminderService.addOutboxMessage(patient, appointment);
        verifyZeroInteractions(outboxService);
    }

    @Test
    public void shouldRaiseAlertIfReminderAlertCriteriaIsTrue() {
        DateTime now = DateUtil.now();
        appointment.dueDate(now);
        when(reminderAlertCriteria.shouldRaiseAlert(appointment)).thenReturn(true);

        appointmentReminderService.raiseAlert(patient, appointment);

        ArgumentCaptor<HashMap> alertDataArgumentCaptor = ArgumentCaptor.forClass(HashMap.class);
        verify(patientAlertService).createAlert(eq(patient.getId()), eq(TAMAConstants.NO_ALERT_PRIORITY),
                eq(TAMAConstants.APPOINTMENT_REMINDER), eq(""), eq(PatientAlertType.AppointmentReminder), alertDataArgumentCaptor.capture());
        assertNotNull(alertDataArgumentCaptor.getValue().containsKey(PatientAlert.APPOINTMENT_DUE_DATE));
        assertEquals(now.toLocalDate().toString(), alertDataArgumentCaptor.getValue().get(PatientAlert.APPOINTMENT_DUE_DATE));
    }

    @Test
    public void shouldNotRaiseAlertIfReminderAlertCriteriaIsFalse() {
        when(reminderAlertCriteria.shouldRaiseAlert(appointment)).thenReturn(false);
        appointmentReminderService.raiseAlert(patient, appointment);
        verifyZeroInteractions(patientAlertService);
    }
}
