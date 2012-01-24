package org.motechproject.tamafunctional.serial;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamadatasetup.service.TAMADateTimeService;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.test.ivr.IVRAssert;
import org.motechproject.tamafunctional.testdata.FourDayRecallCallInfo;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.TestPatientPreferences;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;

import static org.motechproject.tama.ivr.TamaIVRMessage.*;

public class FourDayRecallTest extends BaseIVRTest {
    private TAMADateTimeService tamaDateTimeService;

    private TestPatient patient;
    private TestClinician clinician;

    private DateTime now = new DateTime(2012, 1, 1, 10, 0, 0, 0);
    private DateTime registrationTime = now.minusDays(7);

    @Before
    public void setUp() {
        super.setUp();
        tamaDateTimeService = new TAMADateTimeService(webClient);
        registerPatient();
        createTreatmentAdvice();
        caller = caller(patient);
    }

    @Test
    public void shouldReportAdherenceOnBestCallDay() {
        tamaDateTimeService.adjustDateTime(now);
        caller.replyToCall(new FourDayRecallCallInfo());
        IVRResponse ivrResponse = caller.enter(patient.patientPreferences().passcode());
        ivrResponse = enterAdherenceInformation(ivrResponse, "0");
        assertAdherencePercentage(ivrResponse, "100");
    }

    public void registerPatient() {
        tamaDateTimeService.adjustDateTime(registrationTime);
        clinician = TestClinician.withMandatory();
        TestPatientPreferences preferences = TestPatientPreferences.withMandatory().callPreference(TestPatientPreferences.CallPreference.WEEKLY_CALL).dayOfWeeklyCall("Monday").bestCallTime("10:10");
        patient = TestPatient.withMandatory().patientPreferences(preferences);
    }

    public void createTreatmentAdvice() {
        PatientDataService patientDataService = new PatientDataService(webDriver);
        TestTreatmentAdvice treatmentAdvice = setUpTreatmentAdviceOn(registrationTime.toLocalDate());
        patientDataService.setupRegimenWithDependents(treatmentAdvice, patient, clinician);
    }

    private TestTreatmentAdvice setUpTreatmentAdviceOn(LocalDate treatmentAdviceStartDate) {
        TestDrugDosage[] drugDosages = TestDrugDosage.create("Efferven", "Combivir");
        drugDosages[0].startDate(treatmentAdviceStartDate);
        drugDosages[1].startDate(treatmentAdviceStartDate);
        return TestTreatmentAdvice.withExtrinsic(drugDosages);
    }

    private IVRResponse enterAdherenceInformation(IVRResponse ivrResponse, String keyPressed) {
        IVRAssert.asksForCollectDtmfWith(ivrResponse, DEFAULT_OUTBOUND_CLINIC_MESSAGE, FDR_GREETING, FDR_MENU_FOR_SINGLE_DOSAGE);
        return caller.enter(keyPressed);
    }

    private void assertAdherencePercentage(IVRResponse ivrResponse, String expectedAdherence) {
        IVRAssert.assertAudioFilesPresent(ivrResponse, FDR_YOUR_WEEKLY_ADHERENCE_IS, "Num_" + expectedAdherence, FDR_PERCENT);
        caller.hangup();
    }
}
