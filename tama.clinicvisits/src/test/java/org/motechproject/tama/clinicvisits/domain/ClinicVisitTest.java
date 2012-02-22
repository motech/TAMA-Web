package org.motechproject.tama.clinicvisits.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class ClinicVisitTest {
    @Test
    public void shouldReturnEmptyLabResultsWhenVisitHasNoLabResults() {
        ClinicVisit visit = new ClinicVisit(new Patient(), new Visit());
        assertEquals(0, visit.getLabResultIds().size());
    }

    @Test
    public void shouldReturnAdjustedDueDateAsNullWhenAppointmentIsNull() {
        ClinicVisit clinicVisit = new ClinicVisit(new Patient(), new Visit());
        assertNull(clinicVisit.getAdjustedDueDate());
    }

    @Test
    public void shouldReturnAdjustedDueDateAsNullWhenNotSetOnAppointment() {
        Visit visit = new Visit().addAppointment(DateUtil.now(), null);
        ClinicVisit clinicVisit = new ClinicVisit(new Patient(), visit);
        assertNull(clinicVisit.getAdjustedDueDate());
    }

    @Test
    public void shouldReturnAdjustedDueDateWhenSetOnAppointment() {
        DateTime now = DateUtil.now();
        LocalDate today = now.toLocalDate();

        Visit visit = new Visit().addAppointment(now, null);
        visit.appointment().addData(ClinicVisit.ADJUSTED_DUE_DATE, today.toString());
        ClinicVisit clinicVisit = new ClinicVisit(new Patient(), visit);
        assertEquals(today, clinicVisit.getAdjustedDueDate());
    }

    @Test
    public void shouldReturnEffectiveDueDateForAnAppointment_WhenDueDateIsNotAdjusted() {
        LocalDate appointmentDueDate = new LocalDate(2010, 10, 10);

        Visit visit = new Visit().addAppointment(DateUtil.newDateTime(appointmentDueDate, 0, 0, 0), null);
        ClinicVisit clinicVisit = new ClinicVisit(new Patient(), visit);
        assertEquals(clinicVisit.getAppointmentDueDate().toLocalDate(), clinicVisit.getEffectiveDueDate());
    }

    @Test
    public void shouldReturnEffectiveDueDateForAnAppointment_WhenDueDateIsAdjusted() {
        LocalDate appointmentDueDate = new LocalDate(2010, 10, 10);
        LocalDate adjustedDueDate = new LocalDate(2010, 10, 20);

        Visit visit = new Visit().addAppointment(DateUtil.newDateTime(appointmentDueDate, 0, 0, 0), null);
        visit.appointment().addData(ClinicVisit.ADJUSTED_DUE_DATE, adjustedDueDate.toString());
        ClinicVisit clinicVisit = new ClinicVisit(new Patient(), visit);
        assertEquals(clinicVisit.getAdjustedDueDate(), clinicVisit.getEffectiveDueDate());
    }
}
