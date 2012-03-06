package org.motechproject.tama.clinicvisits.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.builder.ClinicVisitBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.criteria.AppointmentConfirmationMissedAlertCriteria;
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
    private AppointmentConfirmationMissedAlertCriteria appointmentConfirmationMissedAlertCriteria;
    @Mock
    private OutboxService outboxService;
    @Mock
    PatientAlertService patientAlertService;

    private Patient patient;
    private ClinicVisit clinicVisit;

    private AppointmentReminderService appointmentReminderService;

    public AppointmentReminderServiceTest() {
        patient = PatientBuilder.startRecording().withDefaults().withId("patientDocumentId").build();
        clinicVisit = new ClinicVisit();
    }

    @Before
    public void setup() {
        initMocks(this);
        appointmentReminderService = new AppointmentReminderService(reminderOutboxCriteria, reminderAlertCriteria, patientAlertService, outboxService, appointmentConfirmationMissedAlertCriteria);
    }

    @Test
    public void shouldAddOutboxMessageWhenReminderOutboxCriteriaIsTrue() {
        when(reminderOutboxCriteria.shouldAddOutboxMessageForAppointments(patient, clinicVisit)).thenReturn(true);
        appointmentReminderService.addOutboxMessage(patient, clinicVisit);
        verify(outboxService).addMessage(patient.getId(), TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE);
    }

    @Test
    public void shouldNotAddOutboxMessageWhenReminderOutboxCriteriaIsFalse() {
        when(reminderOutboxCriteria.shouldAddOutboxMessageForAppointments(patient, clinicVisit)).thenReturn(false);
        appointmentReminderService.addOutboxMessage(patient, clinicVisit);
        verifyZeroInteractions(outboxService);
    }

    @Test
    public void shouldRaiseAlertIfReminderAlertCriteriaIsTrue() {
        DateTime now = DateUtil.now();
        clinicVisit = new ClinicVisitBuilder().withAppointmentDueDate(now).build();
        when(reminderAlertCriteria.shouldRaiseAlert(clinicVisit)).thenReturn(true);
        when(appointmentConfirmationMissedAlertCriteria.shouldRaiseAlert(clinicVisit)).thenReturn(false);

        appointmentReminderService.raiseAlert(patient, clinicVisit);

        ArgumentCaptor<HashMap> alertDataArgumentCaptor = ArgumentCaptor.forClass(HashMap.class);
        verify(patientAlertService).createAlert(eq(patient.getId()), eq(TAMAConstants.NO_ALERT_PRIORITY),
                eq(TAMAConstants.APPOINTMENT_REMINDER), eq(""), eq(PatientAlertType.AppointmentReminder), alertDataArgumentCaptor.capture());
        assertNotNull(alertDataArgumentCaptor.getValue().containsKey(PatientAlert.APPOINTMENT_DATE));
        assertEquals(now.toLocalDate().toString(), alertDataArgumentCaptor.getValue().get(PatientAlert.APPOINTMENT_DATE));
    }

    @Test
    public void shouldNotRaiseAlertIfReminderAlertCriteriaIsFalse() {
        when(reminderAlertCriteria.shouldRaiseAlert(clinicVisit)).thenReturn(false);
        when(appointmentConfirmationMissedAlertCriteria.shouldRaiseAlert(clinicVisit)).thenReturn(false);
        appointmentReminderService.raiseAlert(patient, clinicVisit);
        verifyZeroInteractions(patientAlertService);
    }

    @Test
    public void shouldRaiseAlertIfAppointmentConfirmationMissedAlertCriteriaIsTrue() {
        DateTime now = DateUtil.now();
        clinicVisit = new ClinicVisitBuilder().withAppointmentDueDate(now).build();
        when(appointmentConfirmationMissedAlertCriteria.shouldRaiseAlert(clinicVisit)).thenReturn(true);
        appointmentReminderService.raiseAlert(patient, clinicVisit);

        ArgumentCaptor<HashMap> alertDataArgumentCaptor = ArgumentCaptor.forClass(HashMap.class);
        verify(patientAlertService).createAlert(eq(patient.getId()), eq(TAMAConstants.NO_ALERT_PRIORITY),
                eq(TAMAConstants.APPOINTMENT_LOST_REMINDER), eq(""), eq(PatientAlertType.AppointmentConfirmationMissed), alertDataArgumentCaptor.capture());
        assertNotNull(alertDataArgumentCaptor.getValue().containsKey(PatientAlert.APPOINTMENT_DATE));
        assertEquals(now.toLocalDate().toString(), alertDataArgumentCaptor.getValue().get(PatientAlert.APPOINTMENT_DATE));
    }

    @Test
    public void shouldNotRaiseAlertIfAppointmentConfirmationMissedAlertCriteriaIsFalse() {
        when(appointmentConfirmationMissedAlertCriteria.shouldRaiseAlert(clinicVisit)).thenReturn(false);
        appointmentReminderService.raiseAlert(patient, clinicVisit);
        verifyZeroInteractions(patientAlertService);
    }
}
