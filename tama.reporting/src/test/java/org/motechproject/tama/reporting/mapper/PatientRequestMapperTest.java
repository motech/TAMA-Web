package org.motechproject.tama.reporting.mapper;

import org.junit.Test;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.reports.contract.PatientRequest;

import static org.junit.Assert.assertEquals;

public class PatientRequestMapperTest {

    @Test
    public void shouldMapBasicDetails() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        PatientRequest patientRequest = new PatientRequestMapper(patient).map();
        assertEquals(new BasicDetails(patient), new BasicDetails(patientRequest));
    }

    @Test
    public void shouldMapIVRDetails() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        PatientRequest patientRequest = new PatientRequestMapper(patient).map();
        assertEquals(new IVRDetails(patient), new IVRDetails(patientRequest));
    }
}
