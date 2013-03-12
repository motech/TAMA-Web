package org.motechproject.tama.testdata.seed;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.repository.AllGenders;
import org.motechproject.tama.refdata.repository.AllHIVTestReasons;
import org.motechproject.tama.refdata.repository.AllIVRLanguages;
import org.motechproject.tama.refdata.repository.AllModesOfTransmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestPatientSeed {

    public static final String TEST_SEED = "testseed";

    private AllPatients allPatients;
    private AllGenders allGenders;
    private AllIVRLanguages allIVRLanguages;
    private AllModesOfTransmission allModesOfTransmission;
    private AllHIVTestReasons allHIVTestReasons;
    private AllClinics allClinics;

    @Autowired
    public TestPatientSeed(AllPatients allPatients, AllGenders allGenders, AllIVRLanguages allIVRLanguages, AllModesOfTransmission allModesOfTransmission, AllHIVTestReasons allHIVTestReasons, AllClinics allClinics) {
        this.allPatients = allPatients;
        this.allGenders = allGenders;
        this.allIVRLanguages = allIVRLanguages;
        this.allModesOfTransmission = allModesOfTransmission;
        this.allHIVTestReasons = allHIVTestReasons;
        this.allClinics = allClinics;
    }

    @Seed(version = "3.0", priority = 0, test = true)
    public void loadTesData() {
        createPatient("testPatient1", "9999999999", "1111", 0);
        createPatient("testPatient2", "9999999998", "1111", 1);
    }

    private void createPatient(String patientId, String mobileNumber, String passcode, int clinicIndex) {
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId(patientId).withMobileNumber(mobileNumber).withPasscode(passcode).withMedicalHistory(medicalHistory()).build();
        setDependencies(patient, clinicIndex);
        allPatients.add(patient, TEST_SEED);
    }

    private void setDependencies(Patient patient, int clinicIndex) {
        patient.setGender(allGenders.getAll().get(0));
        PatientPreferences patientPreferences = patient.getPatientPreferences();
        patientPreferences.setIvrLanguage(allIVRLanguages.getAll().get(0));
        patientPreferences.setCallPreference(CallPreference.DailyPillReminder);
        patient.setClinic(allClinics.getAll().get(clinicIndex));
    }

    private MedicalHistory medicalHistory() {
        MedicalHistory medicalHistory = new MedicalHistory();
        setHIVMedicalHistory(medicalHistory);
        nonHIVMedicalHistory(medicalHistory);
        return medicalHistory;
    }

    private void setHIVMedicalHistory(MedicalHistory medicalHistory) {
        HIVMedicalHistory hivMedicalHistory = new HIVMedicalHistory();
        hivMedicalHistory.setModeOfTransmissionId(allModesOfTransmission.getAll().get(0).getId());
        hivMedicalHistory.setTestReasonId(allHIVTestReasons.getAll().get(0).getId());
        medicalHistory.setHivMedicalHistory(hivMedicalHistory);
    }

    private void nonHIVMedicalHistory(MedicalHistory medicalHistory) {
        NonHIVMedicalHistory nonHivMedicalHistory = new NonHIVMedicalHistory();
        nonHivMedicalHistory.setSystemCategories(SystemCategoryDefinition.all());
        medicalHistory.setNonHivMedicalHistory(nonHivMedicalHistory);
    }
}
