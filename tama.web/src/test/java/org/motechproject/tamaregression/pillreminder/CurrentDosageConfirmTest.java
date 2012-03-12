package org.motechproject.tamaregression.pillreminder;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamafunctionalframework.ivr.BaseIVRTest;
import org.motechproject.tamafunctionalframework.ivr.IVRAssert;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;

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
        IVRAssert.asksForCollectDtmfWith(ivrResponse, DEFAULT_INBOUND_CLINIC_MESSAGE, ITS_TIME_FOR_THE_PILL_INCOMING_CALL_INSIDE_PILL_WINDOW, FROM_THE_BOTTLE_INCOMING_CALL_INSIDE_PILL_WINDOW, DOSE_TAKEN_MENU_OPTION, SYMPTOMS_REPORTING_MENU_OPTION);
        ivrResponse = caller.enter("3");
        IVRAssert.assertAudioFilesPresent(ivrResponse, NO_MESSAGES);
        ivrResponse = caller.listenMore();
        IVRAssert.assertAudioFilesPresent(ivrResponse, HANGUP_OR_MAIN_MENU);
        ivrResponse = caller.listenMore();
        IVRAssert.asksForCollectDtmfWith(ivrResponse, ITS_TIME_FOR_THE_PILL_INCOMING_CALL_INSIDE_PILL_WINDOW, FROM_THE_BOTTLE_INCOMING_CALL_INSIDE_PILL_WINDOW, DOSE_TAKEN_MENU_OPTION, SYMPTOMS_REPORTING_MENU_OPTION);
        IVRAssert.assertAudioFileNotPresent(ivrResponse, DEFAULT_INBOUND_CLINIC_MESSAGE);
    }
    
    @Test
    public void basicPatientCallsTAMA_ToReportAdherenceFlow(){
        caller.call();
        IVRResponse ivrResponse = caller.enter("1234");
        IVRAssert.asksForCollectDtmfWith(ivrResponse, DEFAULT_INBOUND_CLINIC_MESSAGE, ITS_TIME_FOR_THE_PILL_INCOMING_CALL_INSIDE_PILL_WINDOW, FROM_THE_BOTTLE_INCOMING_CALL_INSIDE_PILL_WINDOW, DOSE_TAKEN_MENU_OPTION, SYMPTOMS_REPORTING_MENU_OPTION);
        ivrResponse = caller.enter("1");
        IVRAssert.assertAudioFilesPresent(ivrResponse, DOSE_TAKEN_ON_TIME, DOSE_RECORDED, YOUR_ADHERENCE_IS_NOW, getNumberFilename(100), PERCENT);
        ivrResponse = caller.listenMore();
        IVRAssert.assertAudioFilesPresent(ivrResponse, HANGUP_OR_MAIN_MENU);
        ivrResponse = caller.listenMore();
        IVRAssert.asksForCollectDtmfWith(ivrResponse, SYMPTOMS_REPORTING_MENU_OPTION, HEALTH_TIPS_MENU_OPTION);
    }
}
