package org.motechproject.tama.clinicvisits.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.builder.ClinicVisitBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.criteria.ReminderOutboxCriteria;
import org.motechproject.tama.clinicvisits.domain.criteria.VisitMissedAlertCriteria;
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

public class VisitReminderServiceTest {

    @Mock
    private ReminderOutboxCriteria reminderOutboxCriteria;
    @Mock
    private VisitMissedAlertCriteria visitMissedAlertCriteria;
    @Mock
    private OutboxService outboxService;
    @Mock
    private PatientAlertService patientAlertService;

    VisitReminderService visitReminderService;
    private Patient patient;
    private ClinicVisit clinicVisit;
    private String visitName;

    public VisitReminderServiceTest() {
        visitName = "visitName";
        patient = PatientBuilder.startRecording().withDefaults().withId("patientDocumentId").build();
        clinicVisit = new ClinicVisitBuilder().withId(visitName).build();
    }

    @Before
    public void setup() {
        initMocks(this);
        visitReminderService = new VisitReminderService(reminderOutboxCriteria, outboxService, visitMissedAlertCriteria, patientAlertService);
    }

    @Test
    public void shouldAddOutboxMessageWhenReminderOutboxCriteriaIsTrue() {
        when(reminderOutboxCriteria.shouldAddOutboxMessageForVisits(patient, clinicVisit)).thenReturn(true);
        visitReminderService.addOutboxMessage(patient, clinicVisit);

        ArgumentCaptor<HashMap> paramCaptor = ArgumentCaptor.forClass(HashMap.class);
        verify(outboxService).addMessage(eq(patient.getId()), eq(TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE), paramCaptor.capture());
        assertEquals(visitName, paramCaptor.getValue().get(TAMAConstants.MESSAGE_PARAMETER_VISIT_NAME));
    }

    @Test
    public void shouldNotAddOutboxMessageWhenReminderOutboxCriteriaIsFalse() {
        when(reminderOutboxCriteria.shouldAddOutboxMessageForVisits(patient, clinicVisit)).thenReturn(false);
        visitReminderService.addOutboxMessage(patient, clinicVisit);
        verifyZeroInteractions(outboxService);
    }

    @Test
    public void shouldRaiseAlertIfAppointmentConfirmationMissedAlertCriteriaIsTrue() {
        DateTime now = DateUtil.now();
        clinicVisit = new ClinicVisitBuilder().withAppointmentConfirmedDate(now).build();
        when(visitMissedAlertCriteria.shouldRaiseAlert(clinicVisit)).thenReturn(true);

        visitReminderService.raiseAlert(patient, clinicVisit);

        ArgumentCaptor<HashMap> alertDataArgumentCaptor = ArgumentCaptor.forClass(HashMap.class);
        verify(patientAlertService).createAlert(eq(patient.getId()), eq(TAMAConstants.NO_ALERT_PRIORITY),
                eq(TAMAConstants.APPOINTMENT_MISSED_REMINDER), eq(""), eq(PatientAlertType.VisitMissed), alertDataArgumentCaptor.capture());
        assertNotNull(alertDataArgumentCaptor.getValue().containsKey(PatientAlert.APPOINTMENT_DATE));
        assertEquals(now.toString(), alertDataArgumentCaptor.getValue().get(PatientAlert.CONFIRMED_APPOINTMENT_DATE));
    }

    @Test
    public void shouldNotRaiseAlertIfAppointmentConfirmationMissedAlertCriteriaIsFalse() {
        when(visitMissedAlertCriteria.shouldRaiseAlert(clinicVisit)).thenReturn(false);
        visitReminderService.raiseAlert(patient, clinicVisit);
        verifyZeroInteractions(patientAlertService);
    }
}