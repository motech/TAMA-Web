package org.motechproject.tama.web.model;


import org.junit.Test;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.refdata.domain.Gender;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListPatientViewModelTest {

    @Test
    public void shouldReturnSummaryLinkWithPatientId() {
        Patient patient = mock(Patient.class);
        Clinic clinic = mock(Clinic.class);

        when(patient.getId()).thenReturn("123");
        when(patient.getGender()).thenReturn(Gender.newGender("male"));
        when(patient.getClinic()).thenReturn(clinic);

        ListPatientViewModel listPatientViewModel = new ListPatientViewModel(patient);
        String patientSummaryLink = listPatientViewModel.getPatientSummaryLink();
        assertEquals("patients/summary/123", patientSummaryLink);
    }
}
