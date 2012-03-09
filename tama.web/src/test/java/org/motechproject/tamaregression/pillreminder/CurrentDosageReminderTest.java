package org.motechproject.tamaregression.pillreminder;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamafunctionalframework.ivr.BaseIVRTest;
import org.motechproject.tamafunctionalframework.ivr.IVRAssert;
import org.motechproject.tamafunctionalframework.testdata.PillReminderCallInfo;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;

import java.io.IOException;

import static org.motechproject.tama.ivr.TamaIVRMessage.*;

public class CurrentDosageReminderTest extends BaseIVRTest {

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
    public void dosageTakenFlow_WhenTAMA_CallsPatient() throws IOException {
        caller.replyToCall(new PillReminderCallInfo(1));
        IVRResponse ivrResponse = caller.enter("1234");
        IVRAssert.asksForCollectDtmfWith(ivrResponse, PILL_REMINDER_RESPONSE_MENU, ITS_TIME_FOR_THE_PILL_OUTGOING_CALL_FOR_CURRENT_DOSAGE, FROM_THE_BOTTLE_OUTGOING_CALL_FOR_CURRENT_DOSAGE);
        ivrResponse = caller.enter("1");
        IVRAssert.assertAudioFilesPresent(ivrResponse, DOSE_RECORDED, YOUR_ADHERENCE_IS_NOW, "Num_100", PERCENT);
    }

    @Test
    public void dosageWillTakeLaterFlow_WhenTAMA_CallsPatient() throws IOException {
        caller.replyToCall(new PillReminderCallInfo(1));
        IVRResponse ivrResponse = caller.enter("1234");
        IVRAssert.asksForCollectDtmfWith(ivrResponse, PILL_REMINDER_RESPONSE_MENU, ITS_TIME_FOR_THE_PILL_OUTGOING_CALL_FOR_CURRENT_DOSAGE, FROM_THE_BOTTLE_OUTGOING_CALL_FOR_CURRENT_DOSAGE);
        ivrResponse = caller.enter("2");
        IVRAssert.assertAudioFilesPresent(ivrResponse, PLEASE_TAKE_DOSE, "Num_015", CALL_AFTER_SOME_TIME);
    }
}
