package org.motechproject.tama.clinicvisits.domain.criteria;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.builder.ClinicVisitBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.TAMAReminderConfiguration;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class ReminderOutboxCriteriaTest {

    @Mock
    OutboxService outboxService;
    @Mock
    TAMAReminderConfiguration TAMAReminderConfiguration;

    ReminderOutboxCriteria reminderOutboxCriteria;

    @Before
    public void setUp() {
        initMocks(this);
        reminderOutboxCriteria = new ReminderOutboxCriteria(outboxService, TAMAReminderConfiguration);
    }

    @Test
    public void criteriaForPushingAppointmentReminderShouldBeFalseWhenPatientHasNotOpted() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(false).build();
        assertFalse(reminderOutboxCriteria.shouldAddPushedOutboxMessageForAppointments(patient, new ClinicVisit()));
    }

    @Test
    public void criteriaForPushingAppointmentReminderShouldBeFalseWhenAppointmentIsConfirmed() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withAppointmentConfirmedDate(DateUtil.now()).build();
        assertFalse(reminderOutboxCriteria.shouldAddPushedOutboxMessageForAppointments(patient, clinicVisit));
    }

    @Test
    public void criteriaForPushingAppointmentReminderShouldBeFalseWhenAppointmentRemindersWerePushedAlready() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        ClinicVisit clinicVisit = mock(ClinicVisit.class);
        DateTime now = DateUtil.now();

        when(clinicVisit.getEffectiveDueDate()).thenReturn(now.plusDays(1).toLocalDate());
        when(clinicVisit.getAppointmentDueDate()).thenReturn(now);
        when(TAMAReminderConfiguration.reminderStartDate(clinicVisit)).thenReturn(now);
        when(outboxService.hasMessages(patient.getId(), TAMAConstants.PUSHED_APPOINTMENT_REMINDER_VOICE_MESSAGE, now)).thenReturn(true);

        assertFalse(reminderOutboxCriteria.shouldAddPushedOutboxMessageForAppointments(patient, clinicVisit));
    }

    @Test
    public void criteriaForPushingAppointmentReminderShouldBeFalseWhenTodayIsAfterDueDate() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        ClinicVisit clinicVisit = mock(ClinicVisit.class);
        DateTime now = DateUtil.now();

        when(clinicVisit.getEffectiveDueDate()).thenReturn(now.minusDays(1).toLocalDate());
        when(clinicVisit.getAppointmentDueDate()).thenReturn(now.minusDays(10));
        when(TAMAReminderConfiguration.reminderStartDate(clinicVisit)).thenReturn(now.minusDays(11));
        when(outboxService.hasMessages(patient.getId(), TAMAConstants.PUSHED_APPOINTMENT_REMINDER_VOICE_MESSAGE, now.minusDays(11))).thenReturn(false);

        assertFalse(reminderOutboxCriteria.shouldAddPushedOutboxMessageForAppointments(patient, clinicVisit));
    }

    @Test
    public void criteriaForPushingAppointmentReminderShouldBeFalseWhenThereAreOldUnreadAppointmentReminders() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        ClinicVisit clinicVisit = mock(ClinicVisit.class);
        DateTime now = DateUtil.now();

        when(clinicVisit.getEffectiveDueDate()).thenReturn(now.plusDays(1).toLocalDate());
        when(clinicVisit.getAppointmentDueDate()).thenReturn(now);
        when(TAMAReminderConfiguration.reminderStartDate(clinicVisit)).thenReturn(now);

        when(outboxService.hasPendingOutboxMessages(patient.getId(), TAMAConstants.PUSHED_APPOINTMENT_REMINDER_VOICE_MESSAGE)).thenReturn(true);
        when(outboxService.hasMessages(patient.getId(), TAMAConstants.PUSHED_APPOINTMENT_REMINDER_VOICE_MESSAGE, now)).thenReturn(false);

        assertFalse(reminderOutboxCriteria.shouldAddPushedOutboxMessageForAppointments(patient, clinicVisit));
    }

    @Test
    public void criteriaForPushingAppointmentReminderShouldBeTrueWhenAllConditionsAreMet() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        ClinicVisit clinicVisit = mock(ClinicVisit.class);
        DateTime now = DateUtil.now();

        when(clinicVisit.getEffectiveDueDate()).thenReturn(now.plusDays(1).toLocalDate());
        when(clinicVisit.getAppointmentDueDate()).thenReturn(now);
        when(TAMAReminderConfiguration.reminderStartDate(clinicVisit)).thenReturn(now);
        when(outboxService.hasMessages(patient.getId(), TAMAConstants.PUSHED_APPOINTMENT_REMINDER_VOICE_MESSAGE, now)).thenReturn(false);

        assertTrue(reminderOutboxCriteria.shouldAddPushedOutboxMessageForAppointments(patient, clinicVisit));
    }

    @Test
    public void shouldReturnFalseIfPatientHasOptedNotToReceiveAppointmentReminder() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(false).build();
        assertFalse(reminderOutboxCriteria.shouldAddOutboxMessageForAppointments(patient, new ClinicVisit()));
    }

    @Test
    public void shouldReturnTrueOnDueDate() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withAppointmentDueDate(DateTime.now()).build();
        assertTrue(reminderOutboxCriteria.shouldAddOutboxMessageForAppointments(patient, clinicVisit));
    }

    @Test
    public void shouldReturnFalseAfterDueDate() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withAppointmentDueDate(DateTime.now().minusDays(1)).build();
        assertFalse(reminderOutboxCriteria.shouldAddOutboxMessageForAppointments(patient, clinicVisit));
    }

    @Test
    public void shouldReturnFalseForAppointmentRemindersIfPatientHasPendingAppointmentOutboxMessage() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        when(outboxService.hasPendingOutboxMessages(patient.getId(),
                TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE)).thenReturn(true);
        assertFalse(reminderOutboxCriteria.shouldAddOutboxMessageForAppointments(patient, new ClinicVisit()));
    }

    @Test
    public void shouldReturnFalseForAppointmentRemindersIfAppointmentIsAlreadyConfirmed() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withAppointmentConfirmedDate(DateUtil.now()).build();
        assertFalse(reminderOutboxCriteria.shouldAddOutboxMessageForAppointments(patient, clinicVisit));
    }

    @Test
    public void shouldReturnFalseForVisitRemindersIfPatientHasPendingVisitOutboxMessage() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        when(outboxService.hasPendingOutboxMessages(patient.getId(),
                TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE)).thenReturn(true);
        assertFalse(reminderOutboxCriteria.shouldAddOutboxMessageForVisits(patient, new ClinicVisit()));
    }

    @Test
    public void shouldReturnFalseForVisitRemindersIfAppointmentIsNotConfirmed() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withAppointmentConfirmedDate(null).build();
        assertFalse(reminderOutboxCriteria.shouldAddOutboxMessageForVisits(patient, clinicVisit));
    }

    @Test
    public void shouldReturnFalseForVisitRemindersIfConfirmedDateIsBeforeToday() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withAppointmentConfirmedDate(DateTime.now().minusHours(2)).build();
        assertFalse(reminderOutboxCriteria.shouldAddOutboxMessageForVisits(patient, clinicVisit));
    }

    @Test
    public void shouldReturnFalseForVisitRemindersIfVisitDateIsSet() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withAppointmentConfirmedDate(DateTime.now().plusDays(2))
                .withVisitDate(DateTime.now().minusHours(2))
                .build();
        assertFalse(reminderOutboxCriteria.shouldAddOutboxMessageForVisits(patient, clinicVisit));
    }

    @Test
    public void shouldReturnTrueForVisitRemindersIfAllVisitOutboxMessageCriteriaSatisfied() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withAppointmentConfirmedDate(DateTime.now().plusHours(2)).build();
        assertTrue(reminderOutboxCriteria.shouldAddOutboxMessageForVisits(patient, clinicVisit));
    }

    @Test
    public void shouldReturnTrueForVisitReminders() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withAppointmentReminderPreference(true).build();
        ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withAppointmentConfirmedDate(DateUtil.now().plusDays(1)).build();
        assertTrue(reminderOutboxCriteria.shouldAddOutboxMessageForVisits(patient, clinicVisit));
    }
}
