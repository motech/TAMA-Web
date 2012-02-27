package org.motechproject.tama.clinicvisits.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.tama.clinicvisits.domain.criteria.ReminderOutboxCriteria;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
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
        String visitName = "visitName";

        when(reminderOutboxCriteria.shouldAddOutboxMessageForVisits(patient, appointment)).thenReturn(true);
        visitReminderService.addOutboxMessage(patient, appointment, visitName);

        ArgumentCaptor<HashMap> paramCaptor = ArgumentCaptor.forClass(HashMap.class);
        verify(outboxService).addMessage(eq(patient.getId()), eq(TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE), paramCaptor.capture());
        assertEquals(visitName, paramCaptor.getValue().get(TAMAConstants.MESSAGE_PARAMETER_VISIT_NAME));
    }

    @Test
    public void shouldNotAddOutboxMessageWhenReminderOutboxCriteriaIsFalse() {
        when(reminderOutboxCriteria.shouldAddOutboxMessageForVisits(patient, appointment)).thenReturn(false);
        visitReminderService.addOutboxMessage(patient, appointment, "visitName");
        verifyZeroInteractions(outboxService);
    }
}