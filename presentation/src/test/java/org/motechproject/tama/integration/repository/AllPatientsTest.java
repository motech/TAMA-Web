package org.motechproject.tama.integration.repository;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.TamaException;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.builder.MedicalHistoryBuilder;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.repository.*;
import org.motechproject.tama.util.UniqueMobileNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;

import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllPatientsTest extends SpringIntegrationTest {

    private AllPatients allPatients;

    @Autowired
    private AllUniquePatientFields allUniquePatientFields;

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
        initMocks(this);
        allPatients = new AllPatients(tamaDbConnector, allClinics, allGenders, allIVRLanguages, allUniquePatientFields, allHIVTestReasons, allModesOfTransmission);
        markForDeletion(allUniquePatientFields.getAll().toArray());
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
        markForDeletion(allUniquePatientFields.getAll().toArray());
        markForDeletion(allPatients.getAll().toArray());
        super.after();
    }

    @Test
    public void shouldLoadPatientByPatientId() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withGender(gender).withIVRLanguage(ivrLanguage).withPatientId("12345678").build();
        allPatients.add(patient);

        assertEquals(0, allPatients.findByPatientId("9999").size());

        Patient loadedPatient = allPatients.findByPatientId("12345678").get(0);
        assertNotNull(loadedPatient);
        assertEquals("12345678", loadedPatient.getPatientId());
    }

    @Test
    @ExpectedException(TamaException.class)
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

        Patient similarPatient = PatientBuilder.startRecording().withDefaults().
                withGender(gender).
                withIVRLanguage(ivrLanguage).
                withPatientId("12345678").
                withClinic(clinic).build();
        allPatients.addToClinic(similarPatient, clinic.getId());
    }

    @Test
    @ExpectedException(TamaException.class)
    public void shouldNotAddPatientWithNonUniqueCombinationOfPhoneNumberAndPasscode() {
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withName("clinicForPatient").build();
        allClinics.add(clinic);
        markForDeletion(clinic);

        long mobileNumber = UniqueMobileNumber.generate();
        Patient patient = PatientBuilder.startRecording().withDefaults().
                withGender(gender).
                withIVRLanguage(ivrLanguage).
                withPatientId("rsa").
                withClinic(clinic).
                withMobileNumber(String.valueOf(mobileNumber)).
                withPasscode("1703").
                build();
        allPatients.addToClinic(patient, clinic.getId());

        Patient similarPatient = PatientBuilder.startRecording().withDefaults().
                withGender(gender).
                withIVRLanguage(ivrLanguage).
                withPatientId("md5").
                withClinic(clinic).
                withMobileNumber(String.valueOf(mobileNumber)).
                withPasscode("1703").
                build();
        allPatients.addToClinic(similarPatient, clinic.getId());
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

        List<Patient> dbPatients = allPatients.findByClinic(clinicForPatient.getId());

        assertTrue(dbPatients.contains(patient));
        assertFalse(dbPatients.contains(anotherPatient));
    }

    @Test
    public void shouldRemovePatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("5678").withGender(gender).withIVRLanguage(ivrLanguage).build();
        allPatients.add(patient);

        allPatients.remove(patient);
        List<Patient> dbPatients = allPatients.findByPatientId("5678");
        List<UniquePatientField> uniquePatientFields = allUniquePatientFields.get(patient);

        assertTrue(dbPatients.isEmpty());
        assertTrue(uniquePatientFields.isEmpty());
    }

    @Test
    public void shouldAddPatient() {
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("5678").withGender(gender).withIVRLanguage(ivrLanguage).withClinic(clinic).build();
        String mobilePhoneNumber = patient.getMobilePhoneNumber();
        allPatients.add(patient);

        List<Patient> dbPatients = allPatients.findByPatientId("5678");
        List<UniquePatientField> uniquePatientFields = allUniquePatientFields.getAll();

        assertThat(dbPatients.get(0).getPatientId(), is("5678"));
        assertThat(uniquePatientFields.get(0).getId(), is(Patient.CLINIC_AND_PATIENT_ID_UNIQUE_CONSTRAINT + "null/5678"));
        assertThat(uniquePatientFields.get(1).getId(), is(Patient.PHONE_NUMBER_AND_PASSCODE_UNIQUE_CONSTRAINT + mobilePhoneNumber + "/1234"));
    }

    @Test
    public void shouldActivatePatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("6666").withGender(gender).withIVRLanguage(ivrLanguage).build();
        allPatients.add(patient);

        allPatients.activate(patient.getId());

        List<Patient> dbPatients = allPatients.findByPatientId("6666");
        assertTrue(dbPatients.get(0).isActive());
    }

    @Test
    public void shouldDeactivatePatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withStatus(Patient.Status.Active).withPatientId("7890").withGender(gender).withIVRLanguage(ivrLanguage).build();
        allPatients.add(patient);

        allPatients.deactivate(patient.getId());

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

        assertEquals(clinicForPatient.getId(), allPatients.findClinicFor(patient));
    }

    @Test
    public void shouldGetPatientByMobileNumber() {
        String id = "12345678";
        Patient patient = PatientBuilder.startRecording()
                .withDefaults()
                .withPatientId(id)
                .withGender(gender).withIVRLanguage(ivrLanguage)
                .build();
        allPatients.add(patient);
        String mobileNumber = patient.getMobilePhoneNumber();

        Patient loadedPatient = allPatients.findByMobileNumber(mobileNumber);
        assertNotNull(loadedPatient);
        assertEquals(id, loadedPatient.getPatientId());
        assertEquals(mobileNumber, loadedPatient.getMobilePhoneNumber());
    }

    @Test
    public void shouldGetAllPatientsByMobileNumber() {
        Patient patient1 = PatientBuilder.startRecording().withDefaults().withPatientId("123213213").withPasscode("1212").build();
        String mobilePhoneNumber = patient1.getMobilePhoneNumber();
        Patient patient2 = PatientBuilder.startRecording().withDefaults().withPatientId("123213453").withMobileNumber(mobilePhoneNumber).withPasscode("1233").build();
        allPatients.add(patient1);
        allPatients.add(patient2);

        List<Patient> loadedPatients = allPatients.findAllByMobileNumber(mobilePhoneNumber);
        assertEquals(2, loadedPatients.size());
        assertEquals(mobilePhoneNumber, loadedPatients.get(0).getMobilePhoneNumber());
        assertEquals(mobilePhoneNumber, loadedPatients.get(1).getMobilePhoneNumber());
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

        Patient dbPatient1 = allPatients.findByIdAndClinicId(patient.getId(), clinicForPatient.getId());
        assertEquals(patient.getId(), dbPatient1.getId());

        Patient dbPatient2 = allPatients.findByIdAndClinicId(anotherPatient.getId(), anotherClinic.getId());
        assertEquals(anotherPatient.getId(), dbPatient2.getId());
    }

    @Test
    public void shouldFindByMobileNumberAndPasscode() {
        Clinic clinicForPatient = ClinicBuilder.startRecording().withDefaults().withName("clinicForPatient").build();
        allClinics.add(clinicForPatient);
        markForDeletion(clinicForPatient);

        Clinic anotherClinic = ClinicBuilder.startRecording().withDefaults().withName("anotherClinic").build();
        allClinics.add(anotherClinic);
        markForDeletion(anotherClinic);

        Patient patient = PatientBuilder.startRecording().withDefaults().withClinic(clinicForPatient).withGender(gender).withIVRLanguage(ivrLanguage).build();
        Patient anotherPatient = PatientBuilder.startRecording().withDefaults().withClinic(anotherClinic).withGender(gender).withIVRLanguage(ivrLanguage).withPasscode("9998").build();
        allPatients.add(patient);
        allPatients.add(anotherPatient);

        Patient dbPatient1 = allPatients.findByMobileNumberAndPasscode(patient.getMobilePhoneNumber(), patient.getPatientPreferences().getPasscode());
        assertEquals(patient.getMobilePhoneNumber(), dbPatient1.getMobilePhoneNumber());
        assertEquals(patient.getPatientPreferences().getPasscode(), dbPatient1.getPatientPreferences().getPasscode());

        Patient dbPatient2 = allPatients.findByMobileNumberAndPasscode(anotherPatient.getMobilePhoneNumber(), anotherPatient.getPatientPreferences().getPasscode());
        assertEquals(anotherPatient.getMobilePhoneNumber(), dbPatient2.getMobilePhoneNumber());
        assertEquals(anotherPatient.getPatientPreferences().getPasscode(), dbPatient2.getPatientPreferences().getPasscode());
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
        markForDeletion(testReason);
        markForDeletion(modeOfTransmission);
    }
}
