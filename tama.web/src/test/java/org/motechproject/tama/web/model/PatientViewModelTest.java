package org.motechproject.tama.web.model;


import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.refdata.domain.Gender;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PatientViewModelTest {

    Patient patient;

    @Before
    public void setUp(){
        patient = mock(Patient.class);
        Clinic clinic = mock(Clinic.class);

        when(patient.getId()).thenReturn("123");
        when(patient.getGender()).thenReturn(Gender.newGender("male"));
        when(patient.getClinic()).thenReturn(clinic);
    }

    @Test
    public void shouldReturnSummaryLinkWithPatientId() {
        PatientViewModel listPatientViewModel = new PatientViewModel(patient);
        String patientSummaryLink = listPatientViewModel.getPatientSummaryLink();
        assertEquals("patients/summary/123", patientSummaryLink);
    }

    @Test
    public void shouldReturnCompletionImageUrlForActivePatientWithWarnings(){
        when(patient.getStatus()).thenReturn(Status.Active);

        PatientViewModel listPatientViewModel = new PatientViewModel(patient);
        listPatientViewModel.setWarnings(Arrays.asList("warning1", "warning2"));
        listPatientViewModel.setIncompleteImageUrl("imageUrl");

        String imageUrl = listPatientViewModel.getCompletionStatusImageUrl();
        assertEquals("imageUrl", imageUrl);

    }

    @Test
    public void shouldNotReturnCompletionImageUrlForNonActivePatient(){
        when(patient.getStatus()).thenReturn(Status.Inactive);

        PatientViewModel listPatientViewModel = new PatientViewModel(patient);
        listPatientViewModel.setWarnings(Arrays.asList("warning1", "warning2"));
        listPatientViewModel.setIncompleteImageUrl("imageUrl");

        String imageUrl = listPatientViewModel.getCompletionStatusImageUrl();
        assertNull(imageUrl);

    }
}
