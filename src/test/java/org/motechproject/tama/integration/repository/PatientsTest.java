package org.motechproject.tama.integration.repository;

import org.ektorp.UpdateConflictException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.builder.MedicalHistoryBuilder;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;

import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PatientsTest extends SpringIntegrationTest {
    @Autowired
    private AllPatients allPatients;

    @Autowired
    private AllClinics allClinics;

    @Autowired
    private AllGenders allGenders;

    @Autowired
    private AllHIVTestReasons allHIVTestReasons;

    @Autowired
    private AllModesOfTransmission allModesOfTransmission;

    @Autowired
    private AllIVRLanguages allIVRLanguages;

    private Gender gender;
    private IVRLanguage ivrLanguage;

    @Before
    public void before() {
        super.before();
        markForDeletion(allPatients.getAll().toArray());
        deleteAll();
        gender = Gender.newGender("Male");
        allGenders.add(gender);
        ivrLanguage = IVRLanguage.newIVRLanguage("English", "en");
        allIVRLanguages.add(ivrLanguage);
    }

    @After
    public void after() {
        markForDeletion(gender);
        markForDeletion(ivrLanguage);
        super.after();
    }

    @Test
    public void shouldLoadPatientByPatientId() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withGender(gender).withIVRLanguage(ivrLanguage).withPatientId("12345678").build();
        allPatients.add(patient);
        markForDeletion(patient);
        assertEquals(0, allPatients.findByPatientId("9999").size());

        Patient loadedPatient = allPatients.findByPatientId("12345678").get(0);
        assertNotNull(loadedPatient);
        assertEquals("12345678", loadedPatient.getPatientId());
    }

    @Test
    @ExpectedException(UpdateConflictException.class)
    public void shouldNotAddPatientWithNonUniqueCombinationOfPatientIdAndClinicId() {
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withName("clinicForPatient").build();
        allClinics.add(clinic);
        markForDeletion(clinic);

        Patient patient = PatientBuilder.startRecording().withDefaults().
                withGender(gender).
                withIVRLanguage(ivrLanguage).
                withPatientId("12345678").
                withClinic(clinic).build();
        allPatients.addToClinic(patient, clinic.getId());
        markForDeletion(patient);

        Patient similarPatient = PatientBuilder.startRecording().withDefaults().
                withGender(gender).
                withIVRLanguage(ivrLanguage).
                withPatientId("12345678").
                withClinic(clinic).build();
        allPatients.addToClinic(similarPatient, clinic.getId());
        markForDeletion(similarPatient);
    }

    @Test
    public void shouldGetOnlyPatientsWithTheSpecifiedClinicID() {
        Clinic clinicForPatient = ClinicBuilder.startRecording().withDefaults().withName("clinicForPatient").build();
        allClinics.add(clinicForPatient);
        markForDeletion(clinicForPatient);

        Clinic anotherClinic = ClinicBuilder.startRecording().withDefaults().withName("anotherClinic").build();
        allClinics.add(anotherClinic);
        markForDeletion(anotherClinic);

        Patient patient = PatientBuilder.startRecording().withDefaults().withClinic(clinicForPatient).withGender(gender).withIVRLanguage(ivrLanguage).build();
        Patient anotherPatient = PatientBuilder.startRecording().withDefaults().withClinic(anotherClinic).build();
        allPatients.add(patient);
        allPatients.add(anotherPatient);
        markForDeletion(patient);
        markForDeletion(anotherPatient);

        List<Patient> dbPatients = allPatients.findByClinic(clinicForPatient.getId());

        assertTrue(dbPatients.contains(patient));
        assertFalse(dbPatients.contains(anotherPatient));
    }

    @Test
    public void shouldUpdatePatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("1234").withGender(gender).withIVRLanguage(ivrLanguage).build();
        allPatients.add(patient);

        String mobilePhoneNumber = "9986573310";
        patient.setMobilePhoneNumber(mobilePhoneNumber);
        allPatients.merge(patient);
        markForDeletion(patient);

        List<Patient> dbPatients = allPatients.findByPatientId("1234");
        assertEquals(mobilePhoneNumber, dbPatients.get(0).getMobilePhoneNumber());
    }

    @Test
    public void shouldRemovePatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("5678").withGender(gender).withIVRLanguage(ivrLanguage).build();
        allPatients.add(patient);
        markForDeletion(patient);

        allPatients.remove(patient);
        List<Patient> dbPatients = allPatients.findByPatientId("5678");
        assertTrue(dbPatients.isEmpty());
    }

    @Test
    public void shouldActivatePatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("6666").withGender(gender).withIVRLanguage(ivrLanguage).build();
        allPatients.add(patient);

        allPatients.activate(patient.getId());
        markForDeletion(patient);

        List<Patient> dbPatients = allPatients.findByPatientId("6666");
        assertTrue(dbPatients.get(0).isActive());
    }

    @Test
    public void shouldDeactivatePatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Patient.Status.Active).withPatientId("7890").withGender(gender).withIVRLanguage(ivrLanguage).build();
        allPatients.add(patient);

        allPatients.deactivate(patient.getId());
        markForDeletion(patient);

        List<Patient> dbPatients = allPatients.findByPatientId("7890");
        assertTrue(dbPatients.get(0).isNotActive());
    }

    @Test
    public void shouldFindClinicForPatient() {
        Clinic clinicForPatient = ClinicBuilder.startRecording().withDefaults().withName("clinicForPatient").build();
        allClinics.add(clinicForPatient);
        markForDeletion(clinicForPatient);

        Patient patient = PatientBuilder.startRecording().withDefaults().withClinic(clinicForPatient).withGender(gender).withIVRLanguage(ivrLanguage).build();
        allPatients.add(patient);
        markForDeletion(patient);

        assertEquals(clinicForPatient.getId(), allPatients.findClinicFor(patient));
    }

    @Test
    public void shouldGetPatientByMobileNumber() {
        String mobileNumber = "9898982323";
        String id = "12345678";
        Patient patient = PatientBuilder.startRecording()
                .withDefaults()
                .withPatientId(id)
                .withMobileNumber(mobileNumber)
                .withGender(gender).withIVRLanguage(ivrLanguage)
                .build();
        allPatients.add(patient);

        Patient loadedPatient = allPatients.findByMobileNumber(mobileNumber);
        assertNotNull(loadedPatient);
        assertEquals(id, loadedPatient.getPatientId());
        assertEquals(mobileNumber, loadedPatient.getMobilePhoneNumber());

        markForDeletion(patient);
    }

    @Test
    public void shouldGetPatientByIVRMobileNumber() {
        String mobileNumber = "9898982323";
        String id = "12345678";
        Patient patient = PatientBuilder.startRecording()
                .withDefaults()
                .withPatientId(id)
                .withMobileNumber(mobileNumber)
                .withGender(gender).withIVRLanguage(ivrLanguage)
                .build();
        allPatients.add(patient);

        Patient loadedPatient = allPatients.findByMobileNumber(patient.getIVRMobilePhoneNumber());
        assertNotNull(loadedPatient);
        assertEquals(id, loadedPatient.getPatientId());
        assertEquals(mobileNumber, loadedPatient.getMobilePhoneNumber());

        markForDeletion(patient);
    }

    @Test
    public void shouldFindByIdAndClinicId() {
        Clinic clinicForPatient = ClinicBuilder.startRecording().withDefaults().withName("clinicForPatient").build();
        allClinics.add(clinicForPatient);
        markForDeletion(clinicForPatient);

        Clinic anotherClinic = ClinicBuilder.startRecording().withDefaults().withName("anotherClinic").build();
        allClinics.add(anotherClinic);
        markForDeletion(anotherClinic);

        Patient patient = PatientBuilder.startRecording().withDefaults().withClinic(clinicForPatient).withGender(gender).withIVRLanguage(ivrLanguage).build();
        Patient anotherPatient = PatientBuilder.startRecording().withDefaults().withClinic(anotherClinic).withGender(gender).withIVRLanguage(ivrLanguage).build();
        allPatients.add(patient);
        allPatients.add(anotherPatient);
        markForDeletion(patient);
        markForDeletion(anotherPatient);

        Patient dbPatient1 = allPatients.findByIdAndClinicId(patient.getId(), clinicForPatient.getId());
        assertEquals(patient.getId(), dbPatient1.getId());

        Patient dbPatient2 = allPatients.findByIdAndClinicId(anotherPatient.getId(), anotherClinic.getId());
        assertEquals(anotherPatient.getId(), dbPatient2.getId());
    }

    @Test
    public void shouldLoadMedicalHistoryForAPatientWhenLoadedByPatientId() {
        MedicalHistory medicalHistory = MedicalHistoryBuilder.startRecording().withDefaults().build();
        HIVTestReason testReason = medicalHistory.getHivMedicalHistory().getTestReason();
        ModeOfTransmission modeOfTransmission = medicalHistory.getHivMedicalHistory().getModeOfTransmission();

        allHIVTestReasons.add(testReason);
        allModesOfTransmission.add(modeOfTransmission);
        Patient patient = PatientBuilder.startRecording().withDefaults().withHIVTestReason(testReason).withModeOfTransmission(modeOfTransmission).build();
        allPatients.add(patient);

        Patient loadedPatient = allPatients.findByPatientId(patient.getPatientId()).get(0);
        Assert.assertEquals("Vertical", loadedPatient.getMedicalHistory().getHivMedicalHistory().getModeOfTransmission().getType());
        Assert.assertEquals("STDs", loadedPatient.getMedicalHistory().getHivMedicalHistory().getTestReason().getName());

        markForDeletion(patient);
        markForDeletion(testReason);
        markForDeletion(modeOfTransmission);
    }
}
