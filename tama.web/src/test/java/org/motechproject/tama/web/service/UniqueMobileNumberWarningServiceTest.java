package org.motechproject.tama.web.service;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class UniqueMobileNumberWarningServiceTest {

    @Mock
    private AllPatients allPatients;

    private Patient patient;

    private Clinic clinic;

    @Mock
    Model uiModel;

    private Patient duplicatePatient;

    static final String PATIENT_ID = "patient_id";

    private UniqueMobileNumberWarningService uniqueMobileNumberWarningService;

    @Before
    public void setUp() {
        initMocks(this);
        clinic = ClinicBuilder.startRecording().withDefaults().withName("clinic1").withId("1234").build();
        patient = PatientBuilder.startRecording().withId(PATIENT_ID).withDefaults().withClinic(clinic).build();
        duplicatePatient = PatientBuilder.startRecording().withId("patientId1").withDefaults().build();
        duplicatePatient.setMobilePhoneNumber("2222222222");
        allPatients.add(duplicatePatient);
        uniqueMobileNumberWarningService = new UniqueMobileNumberWarningService(allPatients);
    }


    @Test
    public void verifyUIModelForIfNoDuplicateMobileNumberFound() {
        uiModel = new BindingAwareModelMap();
        uiModel = uniqueMobileNumberWarningService.checkUniquenessOfPatientMobileNumberAndRenderWarning(uiModel, patient);
        assertNotNull(uiModel);
        assertNull(uiModel.asMap().get("patientsWithSameMobileNumber"));
        assertNull(uiModel.asMap().get("warningMessage"));
        assertNull(uiModel.asMap().get("adviceMessage"));

    }

    @Test
    public void verifyUIModelForDuplicateMobileNumberFound() {
        patient.setMobilePhoneNumber("2222222222");
        List<Patient> patients = new ArrayList<>();
        Clinic clinic = mock(Clinic.class);
        patient.setClinic(clinic);
        patient.setClinic_id("clinic1");
        clinic.setName("clinic2");
        duplicatePatient.setClinic(clinic);
        duplicatePatient.setClinic_id("clinic2");
        patients.add(duplicatePatient);
        patients.add(patient);
        when(allPatients.findAllByMobileNumber("2222222222")).thenReturn(patients);
        when(patient.getClinic().getName()).thenReturn("clinic1");
        uiModel = new BindingAwareModelMap();
        uiModel = uniqueMobileNumberWarningService.checkUniquenessOfPatientMobileNumberAndRenderWarning(uiModel, patient);
        when(allPatients.findAllByMobileNumber("2222222222")).thenReturn(patients);
        when(patient.getClinic().getName()).thenReturn("clinic1");
        uiModel.asMap().containsKey("patientsWithSameMobileNumber");
        assertNotNull(uiModel);
        assertNotNull(uiModel.asMap().get("patientsWithSameMobileNumber"));
        assertNotNull(uiModel.asMap().get("warningMessage"));
        assertNotNull(uiModel.asMap().get("adviceMessage"));

    }

    @Test
    public void verifyUIModelForIfNoDuplicateMobileNumberFoundOnUpdate() {
        uiModel = new BindingAwareModelMap();
        uiModel = uniqueMobileNumberWarningService.checkUniquenessOfPatientMobileNumberAndRenderWarningClinicId(uiModel, patient, "clinic1");
        assertNotNull(uiModel);
        assertNull(uiModel.asMap().get("patientsWithSameMobileNumber"));
        assertNull(uiModel.asMap().get("warningMessage"));
        assertNull(uiModel.asMap().get("adviceMessage"));

    }

    @Test
    public void verifyUIModelForDuplicateMobileNumberFoundOnUpdate() {
        patient.setMobilePhoneNumber("2222222222");
        List<Patient> patients = new ArrayList<>();
        Clinic clinic = mock(Clinic.class);
        patient.setClinic(clinic);
        patient.setClinic_id("clinic1");
        clinic.setName("clinic2");
        duplicatePatient.setClinic(clinic);
        duplicatePatient.setClinic_id("clinic2");
        patients.add(duplicatePatient);
        patients.add(patient);
        when(allPatients.findAllByMobileNumber("2222222222")).thenReturn(patients);
        when(patient.getClinic().getName()).thenReturn("clinic1");
        uiModel = new BindingAwareModelMap();
        uiModel = uniqueMobileNumberWarningService.checkUniquenessOfPatientMobileNumberAndRenderWarningClinicId(uiModel, patient, "clinic1");
        when(allPatients.findAllByMobileNumber("2222222222")).thenReturn(patients);
        when(patient.getClinic().getName()).thenReturn("clinic1");
        uiModel.asMap().containsKey("patientsWithSameMobileNumber");
        assertNotNull(uiModel);
        assertNotNull(uiModel.asMap().get("patientsWithSameMobileNumber"));
        assertNotNull(uiModel.asMap().get("warningMessage"));
        assertNotNull(uiModel.asMap().get("adviceMessage"));

    }

}
