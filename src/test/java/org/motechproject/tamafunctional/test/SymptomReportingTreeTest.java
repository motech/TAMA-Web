package org.motechproject.tamafunctional.test;

import org.junit.Test;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.testdata.*;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctional.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;

import java.io.IOException;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

public class SymptomReportingTreeTest extends BaseIVRTest {
    @Test
    public void shouldTakeThePatientToTheCorrectSymptomReportingTree() throws IOException {
        TestClinician clinician = TestClinician.withMandatory();
        new ClinicianDataService(webDriver).createWithClinc(clinician);

        TestPatient patient = TestPatient.withMandatory();
        patient.patientPreferences().passcode("5678");
        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.registerAndActivate(patient, clinician);

        TestLabResult labResult = TestLabResult.withMandatory().results(Arrays.asList("60", "10"));
        patientDataService.setupLabResult(patient, clinician, labResult);

        patientDataService.setInitialVitalStatistics(TestVitalStatistics.withMandatory(), patient, clinician);

        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Efferven", "Combivir"));
        patientDataService.createARTRegimen(treatmentAdvice, patient, clinician);

        TestTreatmentAdvice savedTreatmentAdvice = patientDataService.getTreatmentAdvice(patient, clinician);
        assertEquals(savedTreatmentAdvice.regimenName(), treatmentAdvice.regimenName());
        assertEquals(savedTreatmentAdvice.drugCompositionName(), treatmentAdvice.drugCompositionName());

        caller = caller(patient);
        IVRResponse ivrResponse = caller.call();
        asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);

        ivrResponse = caller.enter("5678#");
        asksForCollectDtmfWith(ivrResponse, "welcome_to_" + clinician.clinic().name(), TamaIVRMessage.ITS_TIME_FOR_THE_PILL, "pillazt3tc_combivir", "pillefv_efavir", TamaIVRMessage.PILL_FROM_THE_BOTTLE, TamaIVRMessage.PILL_CONFIRM_CALL_MENU);

        // Regimen4_2
        ivrResponse = caller.enter("2");
        ivrResponse = caller.listenMore();
        assertAudioFilesPresent(ivrResponse, "q_nauseaorvomiting");

        ivrResponse = caller.enter("1");
        assertAudioFilesPresent(ivrResponse, "cy_nauseaorvomiting", "q_shortnessofbreathorgiddiness");

        ivrResponse = caller.enter("1");
        assertAudioFilesPresent(ivrResponse, "ppc_nvshortbreathgiddi", "adv_continuemedicineseeclinicasap");

        ivrResponse = caller.listenMore();
        assertAudioFilesPresent(ivrResponse, TamaIVRMessage.MORE_OPTIONS, TamaIVRMessage.SIGNATURE_MUSIC);

        caller.hangup();
    }


}
