package org.motechproject.tama.clinicvisits.domain;

import org.junit.Test;
import org.motechproject.appointments.api.model.Visit;

import static junit.framework.Assert.assertEquals;

public class ClinicVisitTest {

    @Test
    public void shouldReturnEmptyLabResultsWhenVisitHasNoLabResults() {
        ClinicVisit visit = new ClinicVisit("patientId", new Visit());
        assertEquals(0, visit.getLabResultIds().size());
    }
}
