package org.motechproject.tama.patient.integration.repository;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.common.TamaException;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.common.repository.AllAuditRecords;
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
import org.motechproject.tama.refdata.domain.*;
import org.motechproject.tama.refdata.objectcache.*;
import org.motechproject.tama.refdata.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

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
    private AllGendersCache allGendersCache;

    @Autowired
    private AllHIVTestReasons allHIVTestReasons;

    @Autowired
    private AllHIVTestReasonsCache allHIVTestReasonsCache;

    @Autowired
    private AllModesOfTransmission allModesOfTransmission;

    @Autowired
    private AllModesOfTransmissionCache allModesOfTransmissionCache;

    @Autowired
    private AllIVRLanguages allIVRLanguages;

    @Autowired
    private AllIVRLanguagesCache allIVRLanguagesCache;

    @Autowired
    private AllAuditRecords allAuditRecords;

    @Autowired
    private AllCitiesCache allCitiesCache;

    @Autowired
    private AllCities allCities;

    private Gender gender;
    private IVRLanguage ivrLanguage;
    private static final String USER_NAME = "USER_NAME";
    private City city;


    @Before
    public void before() {
        super.before();
        allPatients = new AllPatients(tamaDbConnector, allClinics, allGendersCache, allIVRLanguagesCache, allUniquePatientFields, allHIVTestReasonsCache, allModesOfTransmissionCache, allAuditRecords);
        markForDeletion(allUniquePatientFields.getAll().toArray());
        markForDeletion(allPatients.getAll().toArray());
        deleteAll();
        gender = Gender.newGender("Male");
        allGenders.add(gender);
        allGendersCache.refresh();
        ivrLanguage = IVRLanguage.newIVRLanguage("English", "en");
        allIVRLanguages.add(ivrLanguage);
        allIVRLanguagesCache.refresh();
        allCitiesCache.refresh();
        city = createCityForClinic();
    }

    @After
    public void after() {
        markForDeletion(allClinics.getAll().toArray());
        markForDeletion(gender);
        markForDeletion(ivrLanguage);
        markForDeletion(allUniquePatientFields.getAll().toArray());
        markForDeletion(allPatients.getAll().toArray());
        markForDeletion(allCities.getAll().toArray());
        super.after();
    }

    @Test
    public void shouldLoadPatientByPatientId() {
        Clinic patientClinic = ClinicBuilder.startRecording().withCity(city).withCityId(city.getId()).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withClinic(patientClinic).withGender(gender).withIVRLanguage(ivrLanguage).withPatientId("12345678").build();
        final Clinic clinic = patient.getClinic();
        allClinics.add(clinic, "admin");
        patient.setClinic_id(clinic.getId());
        markForDeletion(clinic);
        allPatients.add(patient, USER_NAME);

        assertNull(allPatients.findByPatientId("9999"));

        Patient loadedPatient = allPatients.findByPatientId("12345678");
        assertNotNull(loadedPatient);
        assertEquals("12345678", loadedPatient.getPatientId());
    }

    @Test
    public void addToClinicShouldAddPatient_WhenPatientWithSamePhoneNumberAndPasscode_DoesNotExists() {
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withCity(city).withCityId(city.getId()).withName("clinicForPatient").build();
        allClinics.add(clinic, "admin");
        markForDeletion(clinic);

        Patient patient_1 = PatientBuilder.startRecording().withDefaults().
                withGender(gender).
                withIVRLanguage(ivrLanguage).
                withPatientId("12345678").
                withMobileNumber("1111111111").
                withPasscode("2222").
                withClinic(clinic).build();
        allPatients.addToClinic(patient_1, clinic.getId(), USER_NAME);

        Patient patient_2 = PatientBuilder.startRecording().withDefaults().
                withGender(gender).
                withIVRLanguage(ivrLanguage).
                withPatientId("87654321").
                withMobileNumber("3333333333").
                withPasscode("2222").
                withClinic(clinic).build();
        allPatients.addToClinic(patient_2, clinic.getId(), USER_NAME);

        assertEquals(2, allPatients.getAll().size());
        assertNotNull(allPatients.get(patient_1.getId()));
        assertNotNull(allPatients.get(patient_2.getId()));
    }

    @Test(expected = TamaException.class)
    public void addToClinicShouldThrowException_WhenPatientWithSamePhoneNumberAndPasscode_Exists() {
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withCity(city).withCityId(city.getId()).withName("clinicForPatient").build();
        allClinics.add(clinic, "admin");
        markForDeletion(clinic);

        Patient patient = PatientBuilder.startRecording().withDefaults().
                withGender(gender).
                withIVRLanguage(ivrLanguage).
                withPatientId("12345678").
                withMobileNumber("1111111111").
                withPasscode("2222").
                withClinic(clinic).build();
        allPatients.addToClinic(patient, clinic.getId(), USER_NAME);

        Patient updatedPatient = PatientBuilder.startRecording().withDefaults().
                withGender(gender).
                withIVRLanguage(ivrLanguage).
                withPatientId("87654321").
                withMobileNumber("1111111111").
                withPasscode("2222").
                withClinic(clinic).build();
        allPatients.addToClinic(updatedPatient, clinic.getId(), USER_NAME);
    }

    @Test
    public void shouldUpdatePatient() {
        Clinic clinic1 = ClinicBuilder.startRecording().withDefaults().withCity(city).withCityId(city.getId()).withName("clinic1").build();
        allClinics.add(clinic1, "admin");
        markForDeletion(clinic1);
        Clinic clinic2 = ClinicBuilder.startRecording().withDefaults().withCity(city).withCityId(city.getId()).withName("clinic2").build();
        allClinics.add(clinic2, "admin");
        markForDeletion(clinic2);

        Patient patient = PatientBuilder.startRecording().withDefaults().
                withGender(gender).
                withIVRLanguage(ivrLanguage).
                withPatientId("12345678").
                withClinic(clinic1).build();
        allPatients.addToClinic(patient, clinic1.getId(), USER_NAME);

        Patient similarPatient = PatientBuilder.startRecording().withDefaults().
                withGender(gender).
                withIVRLanguage(ivrLanguage).
                withPatientId("12345678").
                withId(patient.getId()).
                withRevision(patient.getRevision()).
                withClinic(clinic2).build();
        allPatients.update(similarPatient, "USER_NAME");

        Patient patientFromDb = allPatients.get(patient.getId());
        assertEquals("clinic2", patientFromDb.getClinic().getName());
    }

    @Test
    public void shouldGetOnlyPatientsWithTheSpecifiedClinicID() {
        Clinic clinicForPatient = ClinicBuilder.startRecording().withDefaults().withCity(city).withCityId(city.getId()).withName("clinicForPatient").build();
        allClinics.add(clinicForPatient, "admin");
        markForDeletion(clinicForPatient);

        Clinic anotherClinic = ClinicBuilder.startRecording().withDefaults().withCity(city).withCityId(city.getId()).withName("anotherClinic").build();
        allClinics.add(anotherClinic, "admin");
        markForDeletion(anotherClinic);

        Patient patient = PatientBuilder.startRecording().withDefaults().withClinic(clinicForPatient).withGender(gender).withIVRLanguage(ivrLanguage).build();
        Patient anotherPatient = PatientBuilder.startRecording().withDefaults().withClinic(anotherClinic).build();
        allPatients.add(patient, USER_NAME);
        allPatients.add(anotherPatient, USER_NAME);

        List<Patient> dbPatients = allPatients.findByClinic(clinicForPatient.getId());

        assertTrue(dbPatients.contains(patient));
        assertFalse(dbPatients.contains(anotherPatient));
    }

    @Test
    public void shouldRemovePatient() {
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withCity(city).withCityId(city.getId()).withName("clinic").build();
        allClinics.add(clinic, "admin");
        markForDeletion(clinic);
        Patient patient = PatientBuilder.startRecording().withDefaults().withClinic(clinic).withPatientId("5678").withGender(gender).withIVRLanguage(ivrLanguage).build();
        allPatients.add(patient, USER_NAME);

        allPatients.remove(patient, USER_NAME);
        Patient dbPatient = allPatients.findByPatientId("5678");
        List<UniquePatientField> uniquePatientFields = allUniquePatientFields.get(patient);

        assertNull(dbPatient);
        assertTrue(uniquePatientFields.isEmpty());
    }

    @Test
    public void shouldAddPatient() {
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withCity(city).withCityId(city.getId()).withId("8790").build();
        allClinics.add(clinic, "admin");
        markForDeletion(clinic);
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("5678").withGender(gender).withIVRLanguage(ivrLanguage).withClinic(clinic).build();
        String mobilePhoneNumber = patient.getMobilePhoneNumber();
        allPatients.add(patient, USER_NAME);

        Patient dbPatient = allPatients.findByPatientId("5678");
        List<UniquePatientField> uniquePatientFields = allUniquePatientFields.getAll();

        assertThat(dbPatient.getPatientId(), is("5678"));
        assertThat(uniquePatientFields.get(0).getId(), is(Patient.CLINIC_AND_PATIENT_ID_UNIQUE_CONSTRAINT + "8790/5678"));
        assertThat(uniquePatientFields.get(1).getId(), is(Patient.PHONE_NUMBER_AND_PASSCODE_UNIQUE_CONSTRAINT + mobilePhoneNumber + "/1234"));
    }

    @Test
    public void shouldFindClinicForPatient() {
        Clinic clinicForPatient = ClinicBuilder.startRecording().withDefaults().withCity(city).withCityId(city.getId()).withName("clinicForPatient").build();
        allClinics.add(clinicForPatient, "admin");
        markForDeletion(clinicForPatient);

        Patient patient = PatientBuilder.startRecording().withDefaults().withClinic(clinicForPatient).withGender(gender).withIVRLanguage(ivrLanguage).build();
        allPatients.add(patient, USER_NAME);

        assertEquals(clinicForPatient.getId(), allPatients.findClinicFor(patient));
    }

    @Test
    public void shouldGetPatientByMobileNumber() {
        String id = "12345678";
        Clinic clinicForPatient = ClinicBuilder.startRecording().withDefaults().withName("clinicForPatient").build();
        allClinics.add(clinicForPatient, "admin");
        markForDeletion(clinicForPatient);

        Patient patient = PatientBuilder.startRecording()
                .withDefaults()
                .withClinic(clinicForPatient)
                .withPatientId(id)
                .withGender(gender).withIVRLanguage(ivrLanguage)
                .build();
        allPatients.add(patient, USER_NAME);
        String mobileNumber = patient.getMobilePhoneNumber();

        Patient loadedPatient = allPatients.findByMobileNumber(mobileNumber);
        assertNotNull(loadedPatient);
        assertEquals(id, loadedPatient.getPatientId());
        assertEquals(mobileNumber, loadedPatient.getMobilePhoneNumber());
    }

    @Test
    public void shouldGetAllPatientsByMobileNumber() {
        Clinic clinicForPatient = ClinicBuilder.startRecording().withDefaults().withCity(city).withCityId(city.getId()).withName("clinicForPatient").build();
        allClinics.add(clinicForPatient, "admin");
        markForDeletion(clinicForPatient);

        Clinic anotherClinic = ClinicBuilder.startRecording().withDefaults().withCity(city).withCityId(city.getId()).withName("anotherClinic").build();
        allClinics.add(anotherClinic, "admin");
        markForDeletion(anotherClinic);

        Patient patient1 = PatientBuilder.startRecording().withDefaults().withClinic(clinicForPatient).withPatientId("123213213").withPasscode("1212").build();
        String mobilePhoneNumber = patient1.getMobilePhoneNumber();
        Patient patient2 = PatientBuilder.startRecording().withDefaults().withClinic(anotherClinic).withPatientId("123213453").withMobileNumber(mobilePhoneNumber).withPasscode("1233").build();
        allPatients.add(patient1, USER_NAME);
        allPatients.add(patient2, USER_NAME);

        List<Patient> loadedPatients = allPatients.findAllByMobileNumber(mobilePhoneNumber);
        assertEquals(2, loadedPatients.size());
        assertEquals(mobilePhoneNumber, loadedPatients.get(0).getMobilePhoneNumber());
        assertEquals(mobilePhoneNumber, loadedPatients.get(1).getMobilePhoneNumber());
    }

    @Test
    public void findbyPatientIdAndClinicId_shouldFindBy_CaseInsensitivePatientId_And_ClinicId() {
        Clinic clinicForPatient = ClinicBuilder.startRecording().withDefaults().withCity(city).withCityId(city.getId()).withName("clinicForPatient").build();
        allClinics.add(clinicForPatient, "admin");
        markForDeletion(clinicForPatient);

        Clinic anotherClinic = ClinicBuilder.startRecording().withDefaults().withCity(city).withCityId(city.getId()).withName("anotherClinic").build();
        allClinics.add(anotherClinic, "admin");
        markForDeletion(anotherClinic);

        Patient patient = PatientBuilder.startRecording().withDefaults().withClinic(clinicForPatient).withPatientId("patientId").withGender(gender).withIVRLanguage(ivrLanguage).build();
        Patient anotherPatient_1 = PatientBuilder.startRecording().withDefaults().withClinic(clinicForPatient).withPatientId("anotherPatient_1").withGender(gender).withIVRLanguage(ivrLanguage).build();
        Patient anotherPatient_2 = PatientBuilder.startRecording().withDefaults().withClinic(anotherClinic).withPatientId("another_patient_2").withGender(gender).withIVRLanguage(ivrLanguage).build();
        allPatients.add(patient, USER_NAME);
        allPatients.add(anotherPatient_1, USER_NAME);
        allPatients.add(anotherPatient_2, USER_NAME);

        Patient dbPatient1 = allPatients.findByPatientIdAndClinicId("PatientID", clinicForPatient.getId());
        assertEquals(patient.getId(), dbPatient1.getId());

        Patient dbPatient2 = allPatients.findByPatientIdAndClinicId(anotherPatient_2.getPatientId(), anotherClinic.getId());
        assertEquals(anotherPatient_2.getId(), dbPatient2.getId());

        Patient dbPatient3 = allPatients.findByPatientIdAndClinicId(anotherPatient_1.getPatientId(), anotherClinic.getId());
        assertNull(dbPatient3);
    }

    @Test
    public void shouldFindByIdAndClinicId() {
        Clinic clinicForPatient = ClinicBuilder.startRecording().withDefaults().withCity(city).withCityId(city.getId()).withName("clinicForPatient").build();
        allClinics.add(clinicForPatient, "admin");
        markForDeletion(clinicForPatient);

        Clinic anotherClinic = ClinicBuilder.startRecording().withDefaults().withCity(city).withCityId(city.getId()).withName("anotherClinic").build();
        allClinics.add(anotherClinic, "admin");
        markForDeletion(anotherClinic);

        Patient patient = PatientBuilder.startRecording().withDefaults().withClinic(clinicForPatient).withGender(gender).withIVRLanguage(ivrLanguage).build();
        Patient anotherPatient = PatientBuilder.startRecording().withDefaults().withClinic(anotherClinic).withGender(gender).withIVRLanguage(ivrLanguage).build();
        allPatients.add(patient, USER_NAME);
        allPatients.add(anotherPatient, USER_NAME);

        Patient dbPatient1 = allPatients.findByIdAndClinicId(patient.getId(), clinicForPatient.getId());
        assertEquals(patient.getId(), dbPatient1.getId());

        Patient dbPatient2 = allPatients.findByIdAndClinicId(anotherPatient.getId(), anotherClinic.getId());
        assertEquals(anotherPatient.getId(), dbPatient2.getId());
    }

    @Test
    public void shouldFindByMobileNumberAndPasscode() {
        Clinic clinicForPatient = ClinicBuilder.startRecording().withDefaults().withCity(city).withCityId(city.getId()).withName("clinicForPatient").build();
        allClinics.add(clinicForPatient, "admin");
        markForDeletion(clinicForPatient);

        Clinic anotherClinic = ClinicBuilder.startRecording().withDefaults().withCity(city).withCityId(city.getId()).withName("anotherClinic").build();
        allClinics.add(anotherClinic, "admin");
        markForDeletion(anotherClinic);

        Patient patient = PatientBuilder.startRecording().withDefaults().withClinic(clinicForPatient).withGender(gender).withIVRLanguage(ivrLanguage).build();
        Patient anotherPatient = PatientBuilder.startRecording().withDefaults().withClinic(anotherClinic).withGender(gender).withIVRLanguage(ivrLanguage)
                .withPasscode("9998").build();
        allPatients.add(patient, USER_NAME);
        allPatients.add(anotherPatient, USER_NAME);

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
        allHIVTestReasonsCache.refresh();
        allModesOfTransmission.add(modeOfTransmission);
        allModesOfTransmissionCache.refresh();
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withCity(city).withCityId(city.getId()).withName("clinic").build();
        allClinics.add(clinic, "admin");
        markForDeletion(clinic);
        Patient patient = PatientBuilder.startRecording().withDefaults().withClinic(clinic).withHIVTestReason(testReason)
                .withModeOfTransmission(modeOfTransmission).build();
        allPatients.add(patient, USER_NAME);

        Patient loadedPatient = allPatients.findByPatientId(patient.getPatientId());
        Assert.assertEquals("Vertical", loadedPatient.getMedicalHistory().getHivMedicalHistory().getModeOfTransmission().getType());
        Assert.assertEquals("STDs", loadedPatient.getMedicalHistory().getHivMedicalHistory().getTestReason().getName());
        markForDeletion(testReason);
        markForDeletion(modeOfTransmission);
    }

    @Test
    public void shouldFindAllPatientsWithSamePatientId() {
        String patientId = "patientId";
        Patient patient1 = PatientBuilder.startRecording().withDefaults().withPatientId(patientId).build();

        assertTrue(allPatients.findAllByPatientId(patientId).isEmpty());

        allPatients.add(patient1, "user");
        markForDeletion(patient1);

        List<Patient> allByPatientId = allPatients.findAllByPatientId(patientId);
        assertEquals(1, allByPatientId.size());
        assertEquals(patient1.getPatientId(), allByPatientId.get(0).getPatientId());
    }

    private City createCityForClinic() {
        City city = City.newCity("Bangalore");
        city.setId("city_id");
        allCities.add(city);
        allCitiesCache.refresh();
        return city;
    }
}
