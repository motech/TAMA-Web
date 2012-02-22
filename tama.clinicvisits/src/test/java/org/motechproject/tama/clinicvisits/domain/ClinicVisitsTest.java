package org.motechproject.tama.clinicvisits.domain;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.TypeOfVisit;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class ClinicVisitsTest extends BaseUnitTest {

    private ClinicVisits clinicVisits = new ClinicVisits();
    private DateTime today = DateUtil.now();
    private DateTime yesterday = today.minusDays(1);
    private DateTime dayBeforeYesterday = yesterday.minusDays(1);
    private DateTime tomorrow = today.plusDays(1);
    private DateTime dayAfterTomorrow = tomorrow.plusDays(1);

    @Before
    public void setUp() {
        Visit yesterdaysVisit = new Visit().typeOfVisit(TypeOfVisit.Scheduled).name("week1").addAppointment(yesterday, new Reminder());
        yesterdaysVisit.appointment().firmDate(yesterday);
        clinicVisits.add(new ClinicVisit(new Patient(), yesterdaysVisit));

        Visit tomorrowsVisit = new Visit().typeOfVisit(TypeOfVisit.Scheduled).name("week2").addAppointment(tomorrow, new Reminder());
        tomorrowsVisit.appointment().firmDate(tomorrow);
        clinicVisits.add(new ClinicVisit(new Patient(), tomorrowsVisit));

        Visit dayAfterTomorrowVisit = new Visit().typeOfVisit(TypeOfVisit.Scheduled).name("week3").addAppointment(dayAfterTomorrow, new Reminder());
        dayAfterTomorrowVisit.appointment().firmDate(null);
        clinicVisits.add(new ClinicVisit(new Patient(), dayAfterTomorrowVisit));
    }

    @Test
    public void calculateNextAppointmentDueDate() {
        mockCurrentDate(dayBeforeYesterday);
        assertEquals(yesterday, clinicVisits.nextAppointmentDueDate());

        mockCurrentDate(today);
        assertEquals(tomorrow, clinicVisits.nextAppointmentDueDate());

        mockCurrentDate(tomorrow.plusMinutes(1));
        assertEquals(dayAfterTomorrow, clinicVisits.nextAppointmentDueDate());
    }

    @Test
    public void calculateNextAppointmentDueDate_WhenNoVisitsExists() {
        clinicVisits.clear();
        assertNull(clinicVisits.nextAppointmentDueDate());
    }

    @Test
    public void calculateNextConfirmedAppointmentDate() {
        mockCurrentDate(dayBeforeYesterday);
        assertEquals(yesterday, clinicVisits.nextConfirmedAppointmentDate());

        mockCurrentDate(today);
        assertEquals(tomorrow, clinicVisits.nextConfirmedAppointmentDate());

        mockCurrentDate(tomorrow.plusMinutes(1));
        assertNull(clinicVisits.nextConfirmedAppointmentDate());
    }

    @Test
    public void calculateNextConfirmedAppointmentDate_WhenNoVisitsExists() {
        clinicVisits.clear();
        assertNull(clinicVisits.nextConfirmedAppointmentDate());
    }
}
