package org.motechproject.tama.clinicvisits.domain;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class ClinicVisitsTest extends BaseUnitTest {

    private ClinicVisits clinicVisits = new ClinicVisits();
    private LocalDate today = DateUtil.today();
    private LocalDate yesterday = today.minusDays(1);
    private LocalDate dayBeforeYesterday = yesterday.minusDays(1);
    private LocalDate tomorrow = today.plusDays(1);
    private LocalDate dayAfterTomorrow = tomorrow.plusDays(1);

    @Before
    public void setUp() {
        Visit yesterdaysVisit = new Visit().name("week1").addAppointment(DateUtil.newDateTime(yesterday, 0, 0, 0), new Reminder());
        yesterdaysVisit.addData(ClinicVisit.TYPE_OF_VISIT, TypeOfVisit.Scheduled);
        yesterdaysVisit.appointment().confirmedDate(DateUtil.newDateTime(yesterday, 0, 0, 0));
        clinicVisits.add(new ClinicVisit(new Patient(), yesterdaysVisit));

        Visit todaysVisit = new Visit().name("week2").addAppointment(DateUtil.newDateTime(today, 0, 0, 0), new Reminder());
        todaysVisit.addData(ClinicVisit.TYPE_OF_VISIT, TypeOfVisit.Scheduled);
        todaysVisit.appointment().confirmedDate(DateUtil.newDateTime(today, 0, 0, 0));
        clinicVisits.add(new ClinicVisit(new Patient(), todaysVisit));

        Visit dayAfterTomorrowsVisit = new Visit().name("week3").addAppointment(DateUtil.newDateTime(dayAfterTomorrow, 0, 0, 0), new Reminder());
        dayAfterTomorrowsVisit.addData(ClinicVisit.TYPE_OF_VISIT, TypeOfVisit.Scheduled);
        dayAfterTomorrowsVisit.appointment().confirmedDate(null);
        clinicVisits.add(new ClinicVisit(new Patient(), dayAfterTomorrowsVisit));
    }

    @Test
    public void calculateNextAppointmentDueDate() {
        mockCurrentDate(dayBeforeYesterday);
        assertEquals(yesterday, clinicVisits.nextAppointmentDueDate());

        mockCurrentDate(yesterday);
        assertEquals(yesterday, clinicVisits.nextAppointmentDueDate());

        mockCurrentDate(today);
        assertEquals(today, clinicVisits.nextAppointmentDueDate());

        mockCurrentDate(tomorrow);
        assertEquals(dayAfterTomorrow, clinicVisits.nextAppointmentDueDate());

        mockCurrentDate(dayAfterTomorrow);
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

        mockCurrentDate(yesterday);
        assertEquals(yesterday, clinicVisits.nextConfirmedAppointmentDate());

        mockCurrentDate(today);
        assertEquals(today, clinicVisits.nextConfirmedAppointmentDate());

        mockCurrentDate(tomorrow);
        assertNull(clinicVisits.nextConfirmedAppointmentDate());

        mockCurrentDate(dayAfterTomorrow);
        assertNull(clinicVisits.nextConfirmedAppointmentDate());
    }

    @Test
    public void calculateNextConfirmedAppointmentDate_WhenNoVisitsExists() {
        clinicVisits.clear();
        assertNull(clinicVisits.nextConfirmedAppointmentDate());
    }
}
