package org.motechproject.tama.patient.reporting;

import org.junit.Test;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.reports.contract.PatientRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BasicDetailsTest {

    @Test
    public void shouldMapPatientId() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        PatientRequest request = new PatientRequest();
        new BasicDetails(patient).copyTo(request);
        assertEquals(patient.getPatientId(), request.getPatientId());
    }

    @Test
    public void shouldMapPatientDocumentId() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        patient.setId("patientDocumentId");
        PatientRequest request = new PatientRequest();
        new BasicDetails(patient).copyTo(request);
        assertEquals(patient.getId(), request.getPatientDocumentId());
    }

    @Test
    public void shouldMapDateOfBirth() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        PatientRequest request = new PatientRequest();
        new BasicDetails(patient).copyTo(request);
        assertEquals(patient.getDateOfBirthAsDate(), request.getDateOfBirth());
    }

    @Test
    public void shouldMapGender() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        PatientRequest request = new PatientRequest();
        new BasicDetails(patient).copyTo(request);
        assertEquals("Female", request.getGender());
    }

    @Test
    public void shouldMapPatientCompleteionStatus() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        patient.setComplete(true);

        PatientRequest request = new PatientRequest();
        new BasicDetails(patient).copyTo(request);
        assertTrue(request.getComplete());
    }

    @Test
    public void shouldMapClinic() {
        Clinic clinic = new Clinic("id");

        Patient patient = PatientBuilder.startRecording().withDefaults().withClinic(clinic).build();
        PatientRequest request = new PatientRequest();
        new BasicDetails(patient).copyTo(request);
        assertEquals("id", request.getClinicId());
    }

    @Test
    public void shouldMapTravelTimeToClinic() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        PatientRequest request = new PatientRequest();
        new BasicDetails(patient).copyTo(request);
        assertEquals("1 days, 3 hours, 0 minutes", request.getTravelTimeToClinic());
    }
}
