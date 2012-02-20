package org.motechproject.tama.clinicvisits.domain;

import org.junit.Test;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.tama.clinicvisits.builder.ClinicVisitBuilder;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class ClinicVisitsTest {

    @Test
    public void getBaselineVisit() {
        final ClinicVisit baseLineClinicVisit = ClinicVisitBuilder.startRecording().withPatientId("pid1").withTypeOfVisit(ClinicVisit.TypeOfVisit.Baseline).build();
        final ClinicVisit scheduledClinicVisit = ClinicVisitBuilder.startRecording().withPatientId("pid1").withTypeOfVisit(ClinicVisit.TypeOfVisit.Scheduled).build();
        final ClinicVisit unscheduledClinicVisit = ClinicVisitBuilder.startRecording().withPatientId("pid1").withTypeOfVisit(ClinicVisit.TypeOfVisit.Unscheduled).build();

        ClinicVisits clinicVisits = new ClinicVisits();
        clinicVisits.add(baseLineClinicVisit);
        clinicVisits.add(scheduledClinicVisit);
        clinicVisits.add(unscheduledClinicVisit);

        assertEquals(baseLineClinicVisit, clinicVisits.getBaselineVisit());
    }

    @Test
    public void shouldReturnEmptyLabResultsWhenVisitHasNoLabResults() throws Exception {
        ClinicVisit visit = new ClinicVisit("patientId", new Visit());
        assertNotNull(visit.getLabResultIds());
    }
}
