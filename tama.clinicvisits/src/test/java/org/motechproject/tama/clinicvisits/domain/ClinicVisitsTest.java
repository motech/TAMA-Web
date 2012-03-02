package org.motechproject.tama.clinicvisits.domain;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.appointments.api.contract.VisitResponse;
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
        VisitResponse yesterdaysVisit = new VisitResponse().setName("week1").setOriginalAppointmentDueDate(yesterday).setAppointmentDueDate(yesterday).setAppointmentConfirmDate(yesterday);
        clinicVisits.add(new ClinicVisit(new Patient(), yesterdaysVisit));

        VisitResponse todaysVisit = new VisitResponse().setName("week2").setOriginalAppointmentDueDate(today).setAppointmentDueDate(today).setAppointmentConfirmDate(today);
        clinicVisits.add(new ClinicVisit(new Patient(), todaysVisit));

        VisitResponse dayAfterTomorrowsVisit = new VisitResponse().setName("week3").setOriginalAppointmentDueDate(dayAfterTomorrow).setAppointmentDueDate(dayAfterTomorrow);
        clinicVisits.add(new ClinicVisit(new Patient(), dayAfterTomorrowsVisit));
    }

    @Test
    public void calculateNextAppointmentDueDate() {
        mockCurrentDate(dayBeforeYesterday);
        assertEquals(yesterday.toLocalDate(), clinicVisits.nextAppointmentDueDate());

        mockCurrentDate(yesterday);
        assertEquals(yesterday.toLocalDate(), clinicVisits.nextAppointmentDueDate());

        mockCurrentDate(today);
        assertEquals(today.toLocalDate(), clinicVisits.nextAppointmentDueDate());

        mockCurrentDate(tomorrow);
        assertEquals(dayAfterTomorrow.toLocalDate(), clinicVisits.nextAppointmentDueDate());

        mockCurrentDate(dayAfterTomorrow);
        assertEquals(dayAfterTomorrow.toLocalDate(), clinicVisits.nextAppointmentDueDate());
    }

    @Test
    public void calculateNextAppointmentDueDate_WhenNoVisitsExists() {
        clinicVisits.clear();
        assertNull(clinicVisits.nextAppointmentDueDate());
    }

    @Test
    public void calculateNextConfirmedAppointmentDate() {
        mockCurrentDate(dayBeforeYesterday);
        assertEquals(yesterday.toLocalDate(), clinicVisits.nextConfirmedAppointmentDate());

        mockCurrentDate(yesterday);
        assertEquals(yesterday.toLocalDate(), clinicVisits.nextConfirmedAppointmentDate());

        mockCurrentDate(today);
        assertEquals(today.toLocalDate(), clinicVisits.nextConfirmedAppointmentDate());

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
