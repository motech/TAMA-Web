package org.motechproject.tama.clinicvisits.domain;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.TypeOfVisit;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.*;

public class ClinicVisitsTest extends BaseUnitTest {

    private ClinicVisits clinicVisits = new ClinicVisits();
    private LocalDate today = DateUtil.today();
    private LocalDate yesterday = today.minusDays(1);
    private LocalDate dayBeforeYesterday = yesterday.minusDays(1);
    private LocalDate tomorrow = today.plusDays(1);
    private LocalDate dayAfterTomorrow = tomorrow.plusDays(1);

    @Before
    public void setUp() {
        Visit yesterdaysVisit = new Visit().typeOfVisit(TypeOfVisit.Scheduled).name("week1").addAppointment(DateUtil.newDateTime(yesterday, 0, 0, 0), new Reminder());
        yesterdaysVisit.appointment().firmDate(DateUtil.newDateTime(yesterday, 0, 0, 0));
        clinicVisits.add(new ClinicVisit(new Patient(), yesterdaysVisit));

        Visit todaysVisit = new Visit().typeOfVisit(TypeOfVisit.Scheduled).name("week2").addAppointment(DateUtil.newDateTime(today, 0, 0, 0), new Reminder());
        todaysVisit.appointment().firmDate(DateUtil.newDateTime(today, 0, 0, 0));
        clinicVisits.add(new ClinicVisit(new Patient(), todaysVisit));

        Visit dayAfterTomorrowsVisit = new Visit().typeOfVisit(TypeOfVisit.Scheduled).name("week3").addAppointment(DateUtil.newDateTime(dayAfterTomorrow, 0, 0, 0), new Reminder());
        dayAfterTomorrowsVisit.appointment().firmDate(null);
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
    public void shouldReturnTrueIfTypeOfVisitIsBaseline() {
        Visit visit = new Visit();
        visit.typeOfVisit(TypeOfVisit.Baseline);
        ClinicVisit clinicVisit = new ClinicVisit(PatientBuilder.startRecording().withDefaults().build(), visit);
        assertTrue(clinicVisit.isBaseline());
    }

    @Test
    public void shouldReturnFalseIfTypeOfVisitIsNotBaseline() {
        Visit visit = new Visit();
        visit.typeOfVisit(TypeOfVisit.Scheduled);
        ClinicVisit clinicVisit = new ClinicVisit(PatientBuilder.startRecording().withDefaults().build(), visit);
        assertFalse(clinicVisit.isBaseline());
    }

    @Test
    public void shouldReturnFalseIfTypeOfVisitIsNull() {
        Visit visit = new Visit();
        ClinicVisit clinicVisit = new ClinicVisit(PatientBuilder.startRecording().withDefaults().build(), visit);
        assertFalse(clinicVisit.isBaseline());
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
