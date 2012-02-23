package org.motechproject.tama.clinicvisits.domain.criteria;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mockito.Mock;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


@RunWith(Suite.class)
@Suite.SuiteClasses({ReminderOutboxCriteriaTest.OutboxMessage.class})
public class ReminderOutboxCriteriaTest {

    public static class OutboxMessage {
        @Mock
        OutboxService outboxService;

        ReminderOutboxCriteria reminderOutboxCriteria;

        @Before
        public void setUp() {
            initMocks(this);
            reminderOutboxCriteria = new ReminderOutboxCriteria(outboxService);
        }

        @Test
        public void shouldNotBeCreatedIfPatientHasOptedNotToReceiveAppointmentReminder() {
            Patient patient = PatientBuilder
                    .startRecording()
                    .withDefaults()
                    .withAppointmentReminderPreference(false)
                    .build();

            assertFalse(reminderOutboxCriteria.shouldAddOutboxMessage(patient));
        }

        @Test
        public void shouldNotBeCreatedIfPatientHasPendingOutboxMessage() {
            Patient patient = PatientBuilder
                    .startRecording()
                    .withDefaults()
                    .withAppointmentReminderPreference(true)
                    .build();

            when(outboxService.hasPendingOutboxMessages(patient.getId(),
                    TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE)).thenReturn(true);

            assertFalse(reminderOutboxCriteria.shouldAddOutboxMessage(patient));
        }

        @Test
        public void shouldBeCreated() {
            Patient patient = PatientBuilder
                    .startRecording()
                    .withDefaults()
                    .withAppointmentReminderPreference(true)
                    .build();

            when(outboxService.hasPendingOutboxMessages(patient.getId(),
                    TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE)).thenReturn(false);

            assertTrue(reminderOutboxCriteria.shouldAddOutboxMessage(patient));
        }
    }


}
