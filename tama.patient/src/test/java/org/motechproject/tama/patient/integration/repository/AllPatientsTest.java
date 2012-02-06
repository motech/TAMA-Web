package org.motechproject.tama.patient.integration.repository;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.patient.builder.MedicalHistoryBuilder;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.MedicalHistory;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.UniquePatientField;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllUniquePatientFields;
import org.motechproject.tama.refdata.domain.Gender;
import org.motechproject.tama.refdata.domain.HIVTestReason;
import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.motechproject.tama.refdata.domain.ModeOfTransmission;
import org.motechproject.tama.refdata.repository.AllGenders;
import org.motechproject.tama.refdata.repository.AllHIVTestReasons;
import org.motechproject.tama.refdata.repository.AllIVRLanguages;
import org.motechproject.tama.refdata.repository.AllModesOfTransmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.MockitoAnnotations.initMocks;

@ContextConfiguration(locations = "classpath*:applicationPatientContext.xml", inheritLocations = false)
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

        assertNull(allPatients.findByPatientId("9999"));

        Patient loadedPatient = allPatients.findByPatientId("12345678");
        assertNotNull(loadedPatient);
        assertEquals("12345678", loadedPatient.getPatientId());
    }

    @Test
    public void addToClinicShouldAddPatient_WhenPatientWithSamePhoneNumberAndPasscode_DoesNotExists() {
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withName("clinicForPatient").build();
        allClinics.add(clinic);
        markForDeletion(clinic);

        Patient patient_1 = PatientBuilder.startRecording().withDefaults().
                withGender(gender).
                withIVRLanguage(ivrLanguage).
                withPatientId("12345678").
                withMobileNumber("1111111111").
                withPasscode("2222").
                withClinic(clinic).build();
        allPatients.addToClinic(patient_1, clinic.getId());

        Patient patient_2 = PatientBuilder.startRecording().withDefaults().
                withGender(gender).
                withIVRLanguage(ivrLanguage).
                withPatientId("87654321").
                withMobileNumber("3333333333").
                withPasscode("2222").
                withClinic(clinic).build();
        allPatients.addToClinic(patient_2, clinic.getId());

        assertEquals(2, allPatients.getAll().size());
        assertNotNull(allPatients.get(patient_1.getId()));
        assertNotNull(allPatients.get(patient_2.getId()));
    }

    @Test
    public void addToClinicShouldUpdatePatient_WhenPatientWithSamePhoneNumberAndPasscode_Exists() {
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withName("clinicForPatient").build();
        allClinics.add(clinic);
        markForDeletion(clinic);

        Patient patient = PatientBuilder.startRecording().withDefaults().
                withGender(gender).
                withIVRLanguage(ivrLanguage).
                withPatientId("12345678").
                withMobileNumber("1111111111").
                withPasscode("2222").
                withClinic(clinic).build();
        allPatients.addToClinic(patient, clinic.getId());
        List<Patient> patients = allPatients.getAll();

        Patient updatedPatient = PatientBuilder.startRecording().withDefaults().
                withGender(gender).
                withIVRLanguage(ivrLanguage).
                withPatientId("87654321").
                withMobileNumber("1111111111").
                withPasscode("2222").
                withClinic(clinic).build();
        allPatients.addToClinic(updatedPatient, clinic.getId());

        assertEquals(patients.size(), allPatients.getAll().size());
        assertEquals("87654321", allPatients.get(patient.getId()).getPatientId());
    }

    @Test
    public void shouldUpdatePatient() {
        Clinic clinic1 = ClinicBuilder.startRecording().withDefaults().withName("clinic1").build();
        allClinics.add(clinic1);
        markForDeletion(clinic1);
        Clinic clinic2 = ClinicBuilder.startRecording().withDefaults().withName("clinic2").build();
        allClinics.add(clinic2);
        markForDeletion(clinic2);

        Patient patient = PatientBuilder.startRecording().withDefaults().
                withGender(gender).
                withIVRLanguage(ivrLanguage).
                withPatientId("12345678").
                withClinic(clinic1).build();
        allPatients.addToClinic(patient, clinic1.getId());

        Patient similarPatient = PatientBuilder.startRecording().withDefaults().
                withGender(gender).
                withIVRLanguage(ivrLanguage).
                withPatientId("12345678").
                withId(patient.getId()).
                withRevision(patient.getRevision()).
                withClinic(clinic2).build();
        allPatients.update(similarPatient);

        Patient patientFromDb = allPatients.get(patient.getId());
        assertEquals("clinic2", patientFromDb.getClinic().getName());
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
        Patient dbPatient = allPatients.findByPatientId("5678");
        List<UniquePatientField> uniquePatientFields = allUniquePatientFields.get(patient);

        assertNull(dbPatient);
        assertTrue(uniquePatientFields.isEmpty());
    }

    @Test
    public void shouldAddPatient() {
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("5678").withGender(gender).withIVRLanguage(ivrLanguage).withClinic(clinic).build();
        String mobilePhoneNumber = patient.getMobilePhoneNumber();
        allPatients.add(patient);

        Patient dbPatient = allPatients.findByPatientId("5678");
        List<UniquePatientField> uniquePatientFields = allUniquePatientFields.getAll();

        assertThat(dbPatient.getPatientId(), is("5678"));
        assertThat(uniquePatientFields.get(0).getId(), is(Patient.CLINIC_AND_PATIENT_ID_UNIQUE_CONSTRAINT + "null/5678"));
        assertThat(uniquePatientFields.get(1).getId(), is(Patient.PHONE_NUMBER_AND_PASSCODE_UNIQUE_CONSTRAINT + mobilePhoneNumber + "/1234"));
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
    public void shouldFindbyPatientIdAndClinicId(){
        Clinic clinicForPatient = ClinicBuilder.startRecording().withDefaults().withName("clinicForPatient").build();
        allClinics.add(clinicForPatient);
        markForDeletion(clinicForPatient);

        Clinic anotherClinic = ClinicBuilder.startRecording().withDefaults().withName("anotherClinic").build();
        allClinics.add(anotherClinic);
        markForDeletion(anotherClinic);

        Patient patient = PatientBuilder.startRecording().withDefaults().withClinic(clinicForPatient).withPatientId("patientId").withGender(gender).withIVRLanguage(ivrLanguage).build();
        Patient anotherPatient_1 = PatientBuilder.startRecording().withDefaults().withClinic(clinicForPatient).withPatientId("anotherPatient_1").withGender(gender).withIVRLanguage(ivrLanguage).build();
        Patient anotherPatient_2 = PatientBuilder.startRecording().withDefaults().withClinic(anotherClinic).withPatientId("another_patient_2").withGender(gender).withIVRLanguage(ivrLanguage).build();
        allPatients.add(patient);
        allPatients.add(anotherPatient_1);
        allPatients.add(anotherPatient_2);

        Patient dbPatient1 = allPatients.findByPatientIdAndClinicId(patient.getPatientId(), clinicForPatient.getId());
        assertEquals(patient.getId(), dbPatient1.getId());

        Patient dbPatient2 = allPatients.findByPatientIdAndClinicId(anotherPatient_2.getPatientId(), anotherClinic.getId());
        assertEquals(anotherPatient_2.getId(), dbPatient2.getId());

        Patient dbPatient3 = allPatients.findByPatientIdAndClinicId(anotherPatient_1.getPatientId(), anotherClinic.getId());
        assertNull(dbPatient3);
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

        Patient loadedPatient = allPatients.findByPatientId(patient.getPatientId());
        Assert.assertEquals("Vertical", loadedPatient.getMedicalHistory().getHivMedicalHistory().getModeOfTransmission().getType());
        Assert.assertEquals("STDs", loadedPatient.getMedicalHistory().getHivMedicalHistory().getTestReason().getName());
        markForDeletion(testReason);
        markForDeletion(modeOfTransmission);
    }
}
