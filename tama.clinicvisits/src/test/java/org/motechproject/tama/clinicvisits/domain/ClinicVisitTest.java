package org.motechproject.tama.clinicvisits.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.appointments.api.service.contract.VisitResponse;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.*;

public class ClinicVisitTest {

    @Test
    public void shouldReturnEmptyLabResultsWhenVisitHasNoLabResults() {
        ClinicVisit visit = new ClinicVisit(new Patient(), new VisitResponse());
        assertEquals(0, visit.getLabResultIds().size());
    }

    @Test
    public void shouldReturnAdjustedDueDateAsNullWhenAppointmentIsNull() {
        ClinicVisit clinicVisit = new ClinicVisit(new Patient(), new VisitResponse());
        assertNull(clinicVisit.getAdjustedDueDate());
    }

    @Test
    public void shouldReturnAdjustedDueDateAsNullWhenNotSetOnAppointment() {
        DateTime now = DateUtil.now();
        VisitResponse visitResponse = new VisitResponse().setOriginalAppointmentDueDate(now).setAppointmentDueDate(now);
        ClinicVisit clinicVisit = new ClinicVisit(new Patient(), visitResponse);
        assertNull(clinicVisit.getAdjustedDueDate());
    }

    @Test
    public void shouldBeUnscheduledWithAppointment() {
        VisitResponse visitResponse = new VisitResponse().setTypeOfVisit(TypeOfVisit.UnscheduledWithAppointment.name());
        ClinicVisit visit = new ClinicVisit(new Patient(), visitResponse);
        assertTrue(visit.isUnscheduledWithAppointment());
    }

    @Test
    public void shouldReturnAdjustedDueDateWhenSetOnAppointment() {
        DateTime now = DateUtil.now();
        DateTime tomorrow = now.plusDays(1);

        VisitResponse visitResponse = new VisitResponse().setOriginalAppointmentDueDate(now).setAppointmentDueDate(tomorrow);
        ClinicVisit clinicVisit = new ClinicVisit(new Patient(), visitResponse);
        assertEquals(tomorrow.toLocalDate(), clinicVisit.getAdjustedDueDate());
    }

    @Test
    public void shouldReturnEffectiveDueDateForAnAppointment_WhenDueDateIsNotAdjusted() {
        DateTime now = DateUtil.now();

        VisitResponse visitResponse = new VisitResponse().setOriginalAppointmentDueDate(now).setAppointmentDueDate(now);

        ClinicVisit clinicVisit = new ClinicVisit(new Patient(), visitResponse);
        assertEquals(clinicVisit.getAppointmentDueDate().toLocalDate(), clinicVisit.getEffectiveDueDate());
    }

    @Test
    public void shouldReturnEffectiveDueDateForAnAppointment_WhenDueDateIsAdjusted() {
        DateTime now = DateUtil.now();
        DateTime tomorrow = now.plusDays(1);

        VisitResponse visitResponse = new VisitResponse().setOriginalAppointmentDueDate(now).setAppointmentDueDate(tomorrow);
        ClinicVisit clinicVisit = new ClinicVisit(new Patient(), visitResponse);
        assertEquals(clinicVisit.getAdjustedDueDate(), clinicVisit.getEffectiveDueDate());
    }

    @Test
    public void shouldReturnTrueIfTypeOfVisitIsBaseline() {
        VisitResponse visitResponse = new VisitResponse().setTypeOfVisit(TypeOfVisit.Baseline.toString());
        ClinicVisit clinicVisit = new ClinicVisit(PatientBuilder.startRecording().withDefaults().build(), visitResponse);
        assertTrue(clinicVisit.isBaseline());
    }

    @Test
    public void shouldReturnFalseIfTypeOfVisitIsNotBaseline() {
        VisitResponse visitResponse = new VisitResponse().setTypeOfVisit(TypeOfVisit.Unscheduled.toString());
        ClinicVisit clinicVisit = new ClinicVisit(PatientBuilder.startRecording().withDefaults().build(), visitResponse);
        assertFalse(clinicVisit.isBaseline());
    }
}
