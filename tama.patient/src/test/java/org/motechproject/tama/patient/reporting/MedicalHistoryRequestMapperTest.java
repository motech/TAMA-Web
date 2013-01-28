package org.motechproject.tama.patient.reporting;

import org.junit.Test;
import org.motechproject.tama.patient.builder.MedicalHistoryBuilder;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.MedicalHistory;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.reports.contract.MedicalHistoryRequest;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class MedicalHistoryRequestMapperTest {

    @Test
    public void shouldMapPatientToMedicalHistoryRequest() throws IOException {
        MedicalHistory medicalHistory = MedicalHistoryBuilder.startRecording().withDefaults().build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withMedicalHistory(medicalHistory).build();

        MedicalHistoryRequestMapper medicalHistoryRequestMapper = new MedicalHistoryRequestMapper();
        MedicalHistoryRequest history = medicalHistoryRequestMapper.map(patient);
        assertNotNull(history.getNonHivMedicalHistory().get("systemCategories"));
    }

    @Test
    public void shouldSetPatientId() {
        MedicalHistory medicalHistory = MedicalHistoryBuilder.startRecording().withDefaults().build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withMedicalHistory(medicalHistory).build();

        assertEquals(patient.getPatientId(), new MedicalHistoryRequestMapper().map(patient).getPatientId());
    }
}
