package org.motechproject.tama.clinicvisits.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.builder.ClinicVisitBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
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
        visitReminderService = new VisitReminderService(reminderOutboxCriteria, outboxService);
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
}