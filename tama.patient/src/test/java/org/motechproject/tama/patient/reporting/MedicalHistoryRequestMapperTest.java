package org.motechproject.tama.patient.reporting;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.MedicalHistoryBuilder;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.MedicalHistory;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.refdata.domain.HIVTestReason;
import org.motechproject.tama.refdata.domain.ModeOfTransmission;
import org.motechproject.tama.refdata.objectcache.AllHIVTestReasonsCache;
import org.motechproject.tama.refdata.objectcache.AllModesOfTransmissionCache;
import org.motechproject.tama.reports.contract.MedicalHistoryRequest;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MedicalHistoryRequestMapperTest {

    @Mock
    private AllHIVTestReasonsCache testReasonsCache;
    @Mock
    private AllModesOfTransmissionCache modesOfTransmissionCache;

    private MedicalHistoryRequestMapper medicalHistoryMapper;

    @Before
    public void setup() {
        initMocks(this);
        initTestReason();
        initModesOfTransmission();
        medicalHistoryMapper = new MedicalHistoryRequestMapper(modesOfTransmissionCache, testReasonsCache);
    }

    private void initModesOfTransmission() {
        ModeOfTransmission modesOfTransmission = new ModeOfTransmission();
        modesOfTransmission.setType("modeOfTransmission");
        when(modesOfTransmissionCache.getBy(anyString())).thenReturn(modesOfTransmission);
    }

    private void initTestReason() {
        HIVTestReason testReason = new HIVTestReason();
        testReason.setName("reason");
        when(testReasonsCache.getBy(anyString())).thenReturn(testReason);
    }

    @Test
    public void shouldSetHivTestReason() {
        MedicalHistory medicalHistory = MedicalHistoryBuilder.startRecording().withDefaults().build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withMedicalHistory(medicalHistory).build();

        MedicalHistoryRequest history = medicalHistoryMapper.map(patient);
        assertEquals("reason", history.getHivTestReason());
    }

    @Test
    public void shouldSetModesOfTransmission() {
        MedicalHistory medicalHistory = MedicalHistoryBuilder.startRecording().withDefaults().build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withMedicalHistory(medicalHistory).build();

        MedicalHistoryRequest history = medicalHistoryMapper.map(patient);
        assertEquals("modeOfTransmission", history.getModesOfTransmission());
    }

    @Test
    public void shouldMapPatientToMedicalHistoryRequest() throws IOException {
        MedicalHistory medicalHistory = MedicalHistoryBuilder.startRecording().withDefaults().build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withMedicalHistory(medicalHistory).build();

        MedicalHistoryRequest history = medicalHistoryMapper.map(patient);
        assertNotNull(history.getNonHivMedicalHistory().get("systemCategories"));
    }

    @Test
    public void shouldSetPatientId() {
        MedicalHistory medicalHistory = MedicalHistoryBuilder.startRecording().withDefaults().build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withMedicalHistory(medicalHistory).build();

        assertEquals(patient.getPatientId(), medicalHistoryMapper.map(patient).getPatientId());
    }

    @Test
    public void shouldSetPatientDocumentId() {
        MedicalHistory medicalHistory = MedicalHistoryBuilder.startRecording().withDefaults().build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withMedicalHistory(medicalHistory).build();

        assertEquals(patient.getId(), medicalHistoryMapper.map(patient).getPatientDoucmentId());
    }
}
