package org.motechproject.tama.clinicvisits.domain.criteria;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.testing.utils.BaseUnitTest;

import java.util.Properties;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ReminderAlertCriteriaTest.WhenAppointmentIsConfirmed.class,
        ReminderAlertCriteriaTest.WhenAppointmentIsNotConfirmed.class})
public class ReminderAlertCriteriaTest {

    public static class WhenAppointmentIsNotConfirmed extends BaseUnitTest {

        Appointment appointment;
        Properties appointmentsConfiguration;
        Integer days = 2;
        DateTime now = DateTime.now();

        private ReminderAlertCriteria raiseAppointmentConfirmationCriteria;


        public WhenAppointmentIsNotConfirmed() {
            appointment = new Appointment().dueDate(now).confirmedDate(null);
            appointmentsConfiguration = new Properties();
            appointmentsConfiguration.setProperty(TAMAConstants.DAYS_BEFORE_DUE_DATE_WHEN_ALERT_SHOULD_BE_RAISED, days.toString());

            raiseAppointmentConfirmationCriteria = new ReminderAlertCriteria(appointmentsConfiguration);
        }

        @Test
        public void shouldReturnTrueIfTodayIsConfiguredDaysBeforeDueDate() {
            mockCurrentDate(now.toLocalDate().minusDays(days));
            assertTrue(raiseAppointmentConfirmationCriteria.shouldRaiseAlert(appointment));
        }

        @Test
        public void shouldReturnFalseIfTodayIsLessThanConfiguredDaysBeforeDueDate() {
            mockCurrentDate(now.toLocalDate().minusDays(days).plusDays(1));
            assertFalse(raiseAppointmentConfirmationCriteria.shouldRaiseAlert(appointment));
        }

        @Test
        public void shouldReturnFalseIfTodayIsGreaterThanConfiguredDaysBeforeDueDate() {
            mockCurrentDate(now.toLocalDate().minusDays(days).minusDays(1));
            assertFalse(raiseAppointmentConfirmationCriteria.shouldRaiseAlert(appointment));
        }
    }

    public static class WhenAppointmentIsConfirmed extends BaseUnitTest {

        Appointment appointment;
        Properties appointmentsConfiguration;
        Integer days = 2;
        DateTime now = DateTime.now();

        private ReminderAlertCriteria raiseAppointmentConfirmationCriteria;

        public WhenAppointmentIsConfirmed() {
            appointment = new Appointment().dueDate(now.minusDays(2)).confirmedDate(now);

            appointmentsConfiguration = new Properties();
            appointmentsConfiguration.setProperty(TAMAConstants.DAYS_BEFORE_DUE_DATE_WHEN_ALERT_SHOULD_BE_RAISED, days.toString());

            raiseAppointmentConfirmationCriteria = new ReminderAlertCriteria(appointmentsConfiguration);
        }

        @Test
        public void shouldNotReturnTrueIfTodayIsConfiguredDaysBeforeConfirmedDate() {
            mockCurrentDate(now.toLocalDate().minusDays(days));
            assertFalse(raiseAppointmentConfirmationCriteria.shouldRaiseAlert(appointment));
        }

        @Test
        public void shouldReturnFalseIfTodayIsLessThanConfiguredDaysBeforeConfirmedDate() {
            mockCurrentDate(now.toLocalDate().minusDays(days).plusDays(1));
            assertFalse(raiseAppointmentConfirmationCriteria.shouldRaiseAlert(appointment));
        }

        @Test
        public void shouldReturnFalseIfTodayIsGreaterThanConfiguredDaysBeforeConfirmedDate() {
            mockCurrentDate(now.toLocalDate().minusDays(days).minusDays(1));
            assertFalse(raiseAppointmentConfirmationCriteria.shouldRaiseAlert(appointment));
        }
    }

    public static class WhenAppointmentIsAdjusted extends BaseUnitTest {

        Appointment appointment;
        Properties appointmentsConfiguration;
        Integer days = 2;
        DateTime now = DateTime.now();

        private ReminderAlertCriteria raiseAppointmentConfirmationCriteria;

        public WhenAppointmentIsAdjusted() {
            appointment = new Appointment().dueDate(now.minusDays(2));
            appointment.addData(ClinicVisit.ADJUSTED_DUE_DATE, now.toLocalDate().toString());

            appointmentsConfiguration = new Properties();
            appointmentsConfiguration.setProperty(TAMAConstants.DAYS_BEFORE_DUE_DATE_WHEN_ALERT_SHOULD_BE_RAISED, days.toString());

            raiseAppointmentConfirmationCriteria = new ReminderAlertCriteria(appointmentsConfiguration);
        }

        @Test
        public void shouldReturnTrueIfTodayIsConfiguredDaysBeforeConfirmedDate() {
            mockCurrentDate(now.toLocalDate().minusDays(days));
            assertTrue(raiseAppointmentConfirmationCriteria.shouldRaiseAlert(appointment));
        }

        @Test
        public void shouldReturnFalseIfTodayIsLessThanConfiguredDaysBeforeConfirmedDate() {
            mockCurrentDate(now.toLocalDate().minusDays(days).plusDays(1));
            assertFalse(raiseAppointmentConfirmationCriteria.shouldRaiseAlert(appointment));
        }

        @Test
        public void shouldReturnFalseIfTodayIsGreaterThanConfiguredDaysBeforeConfirmedDate() {
            mockCurrentDate(now.toLocalDate().minusDays(days).minusDays(1));
            assertFalse(raiseAppointmentConfirmationCriteria.shouldRaiseAlert(appointment));
        }
    }
}
