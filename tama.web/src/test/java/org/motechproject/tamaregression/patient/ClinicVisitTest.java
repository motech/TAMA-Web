package org.motechproject.tamaregression.patient;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamafunctionalframework.framework.BaseTest;
import org.motechproject.tamafunctionalframework.testdata.*;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

public class ClinicVisitTest extends BaseTest {

    private TestClinician clinician;

    @Before
    public void setUp() {
        super.setUp();
        clinician = TestClinician.withMandatory();
        new ClinicianDataService(webDriver).createWithClinic(clinician);
    }

    @Test
    public void testCreateARTRegimenForPatientOnDailyCall() {
        TestPatient patient = TestPatient.withMandatory();
        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.registerAndActivate(patient, clinician);

        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Efferven", "Combivir"));
        patientDataService.createRegimen(patient, clinician, treatmentAdvice);

        TestTreatmentAdvice savedTreatmentAdvice = patientDataService.getSavedTreatmentAdvice(patient, clinician);
        assertEquals(savedTreatmentAdvice.regimenName(), treatmentAdvice.regimenName());
        assertEquals(savedTreatmentAdvice.drugCompositionName(), treatmentAdvice.drugCompositionName());
    }

    @Test
    public void testCreateARTRegimenForPatientOnWeeklyCall() {
        TestPatient patient = TestPatient.withMandatory();
        patient.patientPreferences().callPreference(TestPatientPreferences.CallPreference.WEEKLY_CALL);
        patient.patientPreferences().dayOfWeeklyCall("Monday");
        patient.patientPreferences().bestCallTime("10:20");
        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.registerAndActivate(patient, clinician);

        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Efferven", "Combivir"));
        patientDataService.createRegimen(patient, clinician, treatmentAdvice);

        TestTreatmentAdvice savedTreatmentAdvice = patientDataService.getSavedTreatmentAdvice(patient, clinician);
        assertEquals(savedTreatmentAdvice.regimenName(), treatmentAdvice.regimenName());
        assertEquals(savedTreatmentAdvice.drugCompositionName(), treatmentAdvice.drugCompositionName());
    }

    @Test
    public void shouldChangeRegimenForPatientOnDailyCall() {
        TestPatient patient = TestPatient.withMandatory();
        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.registerAndActivate(patient, clinician);

        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Efferven", "Combivir"));
        patientDataService.changeRegimen(patient, clinician, treatmentAdvice);

        TestTreatmentAdvice savedTreatmentAdvice = patientDataService.getSavedTreatmentAdvice(patient, clinician);
        assertEquals(savedTreatmentAdvice.regimenName(), treatmentAdvice.regimenName());
        assertEquals(savedTreatmentAdvice.drugCompositionName(), treatmentAdvice.drugCompositionName());
    }

    @Test
    public void testCreateRegimenWithLabResults() {
        TestPatient patient = TestPatient.withMandatory();
        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.registerAndActivate(patient, clinician);

        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Efferven", "Combivir"));
        TestLabResult labResult = TestLabResult.withMandatory();

        patientDataService.createRegimen(patient, clinician, treatmentAdvice, labResult);
        TestLabResult savedLabResult = patientDataService.getSavedLabResult(patient, clinician);
        assertEquals(labResult, savedLabResult);

        final TestLabResult newLabResults = TestLabResult.withMandatory().results(Arrays.asList("1", "2"));
        patientDataService.updateLabResults(patient, clinician, newLabResults);

        final TestLabResult updatedLabResult = patientDataService.getSavedLabResult(patient, clinician);
        assertEquals(newLabResults, updatedLabResult);
    }

    @Test
    public void testCreateRegimenWithVitalStatistics() {
        TestPatient patient = TestPatient.withMandatory();
        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.registerAndActivate(patient, clinician);

        TestVitalStatistics vitalStatistics = TestVitalStatistics.withMandatory();
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Efferven", "Combivir"));
        patientDataService.createRegimen(patient, clinician, treatmentAdvice, vitalStatistics);

        TestVitalStatistics savedVitalStatistics = patientDataService.getSavedVitalStatistics(patient, clinician);
        assertEquals(vitalStatistics, savedVitalStatistics);

        vitalStatistics.heightInCm((double) 155);
        patientDataService.updateVitalStatistics(patient, clinician, vitalStatistics);

        savedVitalStatistics = patientDataService.getSavedVitalStatistics(patient, clinician);
        assertEquals(vitalStatistics, savedVitalStatistics);
    }

}