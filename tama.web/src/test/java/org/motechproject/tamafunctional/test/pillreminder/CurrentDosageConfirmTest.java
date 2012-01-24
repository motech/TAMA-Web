package org.motechproject.tamafunctional.test.pillreminder;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.test.ivr.IVRAssert;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;

import java.io.IOException;

import static org.motechproject.tama.ivr.TamaIVRMessage.*;

public class CurrentDosageConfirmTest extends BaseIVRTest {

    @Before
    public void setUp() {
        super.setUp();
        TestClinician clinician = TestClinician.withMandatory();
        TestPatient patient = TestPatient.withMandatory();
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Efferven", "Combivir"));

        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.setupRegimenWithDependents(treatmentAdvice, patient, clinician);
        caller = caller(patient);
    }

    @Test
    public void outboxFlow_WhenPatient_CallsTAMA() throws IOException {
        caller.call();
        IVRResponse ivrResponse = caller.enter("1234");
        IVRAssert.asksForCollectDtmfWith(ivrResponse, ITS_TIME_FOR_THE_PILL_INCOMING_CALL_INSIDE_PILL_WINDOW, FROM_THE_BOTTLE_INCOMING_CALL_INSIDE_PILL_WINDOW, DOSE_TAKEN_MENU_OPTION, SYMPTOMS_REPORTING_MENU_OPTION);
        ivrResponse = caller.enter("3");
        IVRAssert.assertAudioFilesPresent(ivrResponse, NO_MESSAGES);
        ivrResponse = caller.listenMore();
        IVRAssert.assertAudioFilesPresent(ivrResponse, HANGUP_OR_MAIN_MENU);
    }
}
