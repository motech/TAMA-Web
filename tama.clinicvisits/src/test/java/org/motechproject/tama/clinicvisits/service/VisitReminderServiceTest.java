package org.motechproject.tama.clinicvisits.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.tama.clinicvisits.domain.criteria.ReminderOutboxCriteria;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class VisitReminderServiceTest {

    @Mock
    private ReminderOutboxCriteria reminderOutboxCriteria;
    @Mock
    private OutboxService outboxService;

    VisitReminderService visitReminderService;
    private Patient patient;
    private Appointment appointment;

    public VisitReminderServiceTest() {
        patient = PatientBuilder.startRecording().withDefaults().withId("patientDocumentId").build();
        appointment = new Appointment();
    }

    @Before
    public void setup() {
        initMocks(this);
        visitReminderService = new VisitReminderService(reminderOutboxCriteria, outboxService);
    }

    @Test
    public void shouldAddOutboxMessageWhenReminderOutboxCriteriaIsTrue() {
        when(reminderOutboxCriteria.shouldAddOutboxMessageForVisits(patient, appointment)).thenReturn(true);
        visitReminderService.addOutboxMessage(patient, appointment);
        verify(outboxService).addMessage(patient.getId(), TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE);
    }

    @Test
    public void shouldNotAddOutboxMessageWhenReminderOutboxCriteriaIsFalse() {
        when(reminderOutboxCriteria.shouldAddOutboxMessageForVisits(patient, appointment)).thenReturn(false);
        visitReminderService.addOutboxMessage(patient, appointment);
        verifyZeroInteractions(outboxService);
    }

}