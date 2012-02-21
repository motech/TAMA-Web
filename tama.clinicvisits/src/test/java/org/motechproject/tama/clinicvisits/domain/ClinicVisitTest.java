package org.motechproject.tama.clinicvisits.domain;

import org.junit.Test;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.tama.patient.domain.Patient;

import static junit.framework.Assert.assertEquals;

public class ClinicVisitTest {
    @Test
    public void shouldReturnEmptyLabResultsWhenVisitHasNoLabResults() {
        ClinicVisit visit = new ClinicVisit(new Patient(), new Visit());
        assertEquals(0, visit.getLabResultIds().size());
    }
}
