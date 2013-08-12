package org.motechproject.tama.web.model;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class UniquePatientMobileNumberWarningTest {

    @Mock
    private AllPatients allPatients;

    private Patient patient;

    private Clinic clinic;

    private Patient duplicatePatient;

    private UniquePatientMobileNumberWarning uniquePatientMobileNumberWarning;

    static final String PATIENT_ID = "patient_id";

    @Before
    public void setUp() {
        initMocks(this);
        clinic = ClinicBuilder.startRecording().withDefaults().withName("clinic1").withId("1234").build();
        patient = PatientBuilder.startRecording().withId(PATIENT_ID).withDefaults().withClinic(clinic).build();
        duplicatePatient = PatientBuilder.startRecording().withId("patientId1").withDefaults().build();
        duplicatePatient.setMobilePhoneNumber("2222222222");
        allPatients.add(duplicatePatient);
        uniquePatientMobileNumberWarning = new UniquePatientMobileNumberWarning(allPatients);
    }

    @Test
    public void findAllMobileNumbersWhichMatchTheGivenNumber() {
        patient.setMobilePhoneNumber("2222222222");
        List<Patient> patients = new ArrayList<>();
        Clinic clinic = mock(Clinic.class);
        patient.setClinic(clinic);
        clinic.setName("clinic2");
        duplicatePatient.setClinic(clinic);
        patients.add(duplicatePatient);
        patients.add(patient);
        when(allPatients.findAllByMobileNumber("2222222222")).thenReturn(patients);
        when(patient.getClinic().getName()).thenReturn("clinic1");
        List<String> duplicatePatientList = uniquePatientMobileNumberWarning.findAllMobileNumbersWhichMatchTheGivenNumber(patient.getMobilePhoneNumber(), patient.getPatientId(), patient.getClinic().getName(),"patient");
        assertNotNull(duplicatePatientList);
        assertEquals(duplicatePatient.getPatientId(),duplicatePatientList.get(0));
       }

    @Test
    public void findAllMobileNumbersWhichMatchTheGivenNumberCreateClinicVisit() {
        patient.setMobilePhoneNumber("2222222222");
        List<Patient> patients = new ArrayList<>();
        Clinic clinic = mock(Clinic.class);
        patient.setClinic(clinic);
        clinic.setName("clinic2");
        duplicatePatient.setClinic(clinic);
        patients.add(duplicatePatient);
        patients.add(patient);
        when(allPatients.findAllByMobileNumber("2222222222")).thenReturn(patients);
        when(patient.getClinic().getName()).thenReturn("clinic1");

        List<String> duplicatePatientList = uniquePatientMobileNumberWarning.findAllMobileNumbersWhichMatchTheGivenNumberCreateClinicVisit(patient.getMobilePhoneNumber(), patient.getId(), patient.getClinic().getName(),"patient");
        assertNotNull(duplicatePatientList);
        assertEquals(duplicatePatient.getPatientId(),duplicatePatientList.get(0));
    }
    @Test
    public void findAllMobileNumbersWhichMatchTheGivenNumberCreateClinicVisitNoDuplicateNumbers() {
        patient.setMobilePhoneNumber("2222222222");
        List<Patient> patients = new ArrayList<>();
        Clinic clinic = mock(Clinic.class);
        patient.setClinic(clinic);
        patient.setClinic_id("clinic1");
        patients.add(patient);
        when(allPatients.findAllByMobileNumber("2222222222")).thenReturn(patients);
        when(patient.getClinic().getName()).thenReturn("clinic1");
        List<String> patientsList = uniquePatientMobileNumberWarning.findAllMobileNumbersWhichMatchTheGivenNumberCreateClinicVisit(patient.getMobilePhoneNumber(), patient.getId(), patient.getClinic().getName(),"Clinic");
        assertNull(patientsList);
    }
    @Test
    public void findAllMobileNumbersWhichMatchTheGivenNumberUpdateFindsDuplicate() {
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

        List<String> duplicatePatientList = uniquePatientMobileNumberWarning.findAllMobileNumbersWhichMatchTheGivenNumberOnUpdate(patient.getMobilePhoneNumber(), patient.getPatientId(), patient.getClinic().getName());
        assertNotNull(duplicatePatientList);
        assertEquals(duplicatePatient.getPatientId(),duplicatePatientList.get(0));
    }

    @Test
    public void findAllMobileNumbersWhichMatchTheGivenNumberUpdateNoDuplicateNumbers() {
        patient.setMobilePhoneNumber("2222222222");
        List<Patient> patients = new ArrayList<>();
        Clinic clinic = mock(Clinic.class);
        patient.setClinic(clinic);
        patient.setClinic_id("clinic1");
        patients.add(patient);
        when(allPatients.findAllByMobileNumber("2222222222")).thenReturn(patients);
        when(patient.getClinic().getName()).thenReturn("clinic1");
        when(patient.getClinic().getId()).thenReturn("clinic1");

        assertNull(uniquePatientMobileNumberWarning.findAllMobileNumbersWhichMatchTheGivenNumberOnUpdate(patient.getMobilePhoneNumber(), patient.getPatientId(), patient.getClinic().getId()));
    }

    @Test
    public void findAllMobileNumbersWhichMatchTheGivenNumberNoDuplicateNumbers() {
        patient.setMobilePhoneNumber("2222222222");
        List<Patient> patients = new ArrayList<>();
        Clinic clinic = mock(Clinic.class);
        patient.setClinic(clinic);
        patient.setClinic_id("clinic1");
        patients.add(patient);
        when(allPatients.findAllByMobileNumber("2222222222")).thenReturn(patients);
        when(patient.getClinic().getName()).thenReturn("clinic1");
        when(patient.getClinic().getId()).thenReturn("clinic1");

        assertNull(uniquePatientMobileNumberWarning.findAllMobileNumbersWhichMatchTheGivenNumber(patient.getMobilePhoneNumber(), patient.getPatientId(), patient.getClinic().getName(),"patient"));
    }

    @Test
    public void checkIfMobileNumberIsNotDuplicate() {
        patient.setMobilePhoneNumber("1111111111");
        assertTrue(uniquePatientMobileNumberWarning.isDuplicate(patient.getMobilePhoneNumber()));
    }

    @Test
    public void checkIfMobileNumberIsDuplicate() {
        patient.setMobilePhoneNumber("2222222222");
        List<Patient> patients = new ArrayList<>();
        patients.add(duplicatePatient);
        when(allPatients.findAllByMobileNumber("2222222222")).thenReturn(patients);
        assertFalse(uniquePatientMobileNumberWarning.isDuplicate(patient.getMobilePhoneNumber()));
    }

    @Test
    public void checkIfMobileNumberIsDuplicateOnUpdate() {
        patient.setMobilePhoneNumber("2222222222");
        List<Patient> patients = new ArrayList<>();
        patients.add(duplicatePatient);
        patients.add(patient);
        when(allPatients.findAllByMobileNumber("2222222222")).thenReturn(patients);
        assertFalse(CollectionUtils.isEmpty(uniquePatientMobileNumberWarning.shouldDisplayWarningForPatientsMobileNumberDuplicate(patient.getMobilePhoneNumber(),patient.getId(),patient.getClinic().getId())));
    }

    @Test
    public void checkIfMobileNumberIsNotDuplicateOnUpdate() {
        patient.setMobilePhoneNumber("2222222222");
        List<Patient> patients = new ArrayList<>();
        patients.add(patient);
        when(allPatients.findAllByMobileNumber("2222222222")).thenReturn(patients);
        assertTrue(CollectionUtils.isEmpty(uniquePatientMobileNumberWarning.shouldDisplayWarningForPatientsMobileNumberDuplicate(patient.getMobilePhoneNumber(),patient.getId(),patient.getClinic().getId())));
    }

    @Test
    public void checkIfGivenMobileNumberIsUnique()
    {
        patient.setMobilePhoneNumber("2222222222");
        List<Patient> patients = new ArrayList<>();
        patients.add(patient);
        when(allPatients.findAllByMobileNumber("2222222222")).thenReturn(patients);
        assertTrue(CollectionUtils.isEmpty(uniquePatientMobileNumberWarning.shouldDisplayWarningForPatientsMobileNumberDuplicate(patient.getMobilePhoneNumber(),patient.getId(),patient.getClinic().getId())));
    }
    @Test
    public void checkIfGivenMobileNumberIsNotUnique()
    {
        patient.setMobilePhoneNumber("2222222222");
        List<Patient> patients = new ArrayList<>();
        patients.add(patient);
        patients.add(duplicatePatient);
        when(allPatients.findAllByMobileNumber("2222222222")).thenReturn(patients);
        assertFalse(CollectionUtils.isEmpty(uniquePatientMobileNumberWarning.shouldDisplayWarningForPatientsMobileNumberDuplicate(patient.getMobilePhoneNumber(),patient.getId(),patient.getClinic().getId())));
    }
}

