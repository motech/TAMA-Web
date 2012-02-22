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
}
