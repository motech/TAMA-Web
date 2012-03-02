package org.motechproject.tama.clinicvisits.domain.criteria;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.motechproject.tama.clinicvisits.builder.ClinicVisitBuilder;
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

        ClinicVisit clinicVisit;
        Properties appointmentsConfiguration;
        Integer days = 2;
        DateTime now = DateTime.now();

        private ReminderAlertCriteria raiseAppointmentConfirmationCriteria;


        public WhenAppointmentIsNotConfirmed() {
            clinicVisit = new ClinicVisitBuilder().withAppointmentDueDate(now).build();
            appointmentsConfiguration = new Properties();
            appointmentsConfiguration.setProperty(TAMAConstants.DAYS_BEFORE_DUE_DATE_WHEN_ALERT_SHOULD_BE_RAISED, days.toString());

            raiseAppointmentConfirmationCriteria = new ReminderAlertCriteria(appointmentsConfiguration);
        }

        @Test
        public void shouldReturnTrueIfTodayIsConfiguredDaysBeforeDueDate() {
            mockCurrentDate(now.toLocalDate().minusDays(days));
            assertTrue(raiseAppointmentConfirmationCriteria.shouldRaiseAlert(clinicVisit));
        }

        @Test
        public void shouldReturnFalseIfTodayIsLessThanConfiguredDaysBeforeDueDate() {
            mockCurrentDate(now.toLocalDate().minusDays(days).plusDays(1));
            assertFalse(raiseAppointmentConfirmationCriteria.shouldRaiseAlert(clinicVisit));
        }

        @Test
        public void shouldReturnFalseIfTodayIsGreaterThanConfiguredDaysBeforeDueDate() {
            mockCurrentDate(now.toLocalDate().minusDays(days).minusDays(1));
            assertFalse(raiseAppointmentConfirmationCriteria.shouldRaiseAlert(clinicVisit));
        }
    }

    public static class WhenAppointmentIsConfirmed extends BaseUnitTest {

        ClinicVisit clinicVisit;
        Properties appointmentsConfiguration;
        Integer days = 2;
        DateTime now = DateTime.now();

        private ReminderAlertCriteria raiseAppointmentConfirmationCriteria;

        public WhenAppointmentIsConfirmed() {
            clinicVisit = new ClinicVisitBuilder().withAppointmentDueDate(now.minusDays(2)).withAppointmentConfirmedDate(now).build();

            appointmentsConfiguration = new Properties();
            appointmentsConfiguration.setProperty(TAMAConstants.DAYS_BEFORE_DUE_DATE_WHEN_ALERT_SHOULD_BE_RAISED, days.toString());

            raiseAppointmentConfirmationCriteria = new ReminderAlertCriteria(appointmentsConfiguration);
        }

        @Test
        public void shouldNotReturnTrueIfTodayIsConfiguredDaysBeforeConfirmedDate() {
            mockCurrentDate(now.toLocalDate().minusDays(days));
            assertFalse(raiseAppointmentConfirmationCriteria.shouldRaiseAlert(clinicVisit));
        }

        @Test
        public void shouldReturnFalseIfTodayIsLessThanConfiguredDaysBeforeConfirmedDate() {
            mockCurrentDate(now.toLocalDate().minusDays(days).plusDays(1));
            assertFalse(raiseAppointmentConfirmationCriteria.shouldRaiseAlert(clinicVisit));
        }

        @Test
        public void shouldReturnFalseIfTodayIsGreaterThanConfiguredDaysBeforeConfirmedDate() {
            mockCurrentDate(now.toLocalDate().minusDays(days).minusDays(1));
            assertFalse(raiseAppointmentConfirmationCriteria.shouldRaiseAlert(clinicVisit));
        }
    }

    public static class WhenAppointmentIsAdjusted extends BaseUnitTest {

        ClinicVisit clinicVisit;
        Properties appointmentsConfiguration;
        Integer days = 2;
        DateTime now = DateTime.now();

        private ReminderAlertCriteria raiseAppointmentConfirmationCriteria;

        public WhenAppointmentIsAdjusted() {
            clinicVisit = new ClinicVisitBuilder().withAppointmentDueDate(now.minusDays(2)).withAppointmentAdjustedDate(now).build();

            appointmentsConfiguration = new Properties();
            appointmentsConfiguration.setProperty(TAMAConstants.DAYS_BEFORE_DUE_DATE_WHEN_ALERT_SHOULD_BE_RAISED, days.toString());

            raiseAppointmentConfirmationCriteria = new ReminderAlertCriteria(appointmentsConfiguration);
        }

        @Test
        public void shouldReturnTrueIfTodayIsConfiguredDaysBeforeConfirmedDate() {
            mockCurrentDate(now.toLocalDate().minusDays(days));
            assertTrue(raiseAppointmentConfirmationCriteria.shouldRaiseAlert(clinicVisit));
        }

        @Test
        public void shouldReturnFalseIfTodayIsLessThanConfiguredDaysBeforeConfirmedDate() {
            mockCurrentDate(now.toLocalDate().minusDays(days).plusDays(1));
            assertFalse(raiseAppointmentConfirmationCriteria.shouldRaiseAlert(clinicVisit));
        }

        @Test
        public void shouldReturnFalseIfTodayIsGreaterThanConfiguredDaysBeforeConfirmedDate() {
            mockCurrentDate(now.toLocalDate().minusDays(days).minusDays(1));
            assertFalse(raiseAppointmentConfirmationCriteria.shouldRaiseAlert(clinicVisit));
        }
    }
}
