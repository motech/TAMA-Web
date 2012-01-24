package org.motechproject.tamafunctional.test.pillreminder;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.test.ivr.IVRAssert;
import org.motechproject.tamafunctional.testdata.PillReminderCallInfo;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;
import org.motechproject.util.DateUtil;

import java.io.IOException;

import static org.motechproject.tama.ivr.TamaIVRMessage.*;

public class PreviousDosageReminderTest extends BaseIVRTest {
    private TestPatient patient;

    @Before
    public void setUp() {
        super.setUp();
        TestClinician clinician = TestClinician.withMandatory();
        patient = TestPatient.withMandatory();
        TestDrugDosage[] drugDosages = TestDrugDosage.create("Efferven", "Combivir");
        LocalDate yesterday = DateUtil.today().minusDays(1);
        drugDosages[0].startDate(yesterday);
        drugDosages[1].startDate(yesterday);
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(drugDosages);

        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.setupARTRegimenWithDependents(treatmentAdvice, patient, clinician);
        caller = caller(patient);
    }

    @Test
    public void previousDosageTakenFlow_WhenTAMA_CallsPatient() throws IOException {
        caller.replyToCall(new PillReminderCallInfo(1));

        IVRResponse ivrResponse = caller.enter("1234");
        patientConfirmsNotTakingCurrentDose(ivrResponse);
        ivrResponse = caller.listenMore();
        ivrResponse = patientConfirmsTakingHisPreviousDose(ivrResponse);
        patientListensToHisAdherencePercentage(ivrResponse);
        ivrResponse = caller.listenMore();
        IVRAssert.assertAudioFilesPresent(ivrResponse, HANGUP_OR_MAIN_MENU);
        caller.hangup();
    }

    private void patientConfirmsNotTakingCurrentDose(IVRResponse ivrResponse) {
        IVRAssert.asksForCollectDtmfWith(ivrResponse, PILL_REMINDER_RESPONSE_MENU, ITS_TIME_FOR_THE_PILL_OUTGOING_CALL_FOR_CURRENT_DOSAGE, FROM_THE_BOTTLE_OUTGOING_CALL_FOR_CURRENT_DOSAGE);
        ivrResponse = caller.enter("3");
        IVRAssert.assertAudioFilesPresent(ivrResponse, MISSED_PILL_FEEDBACK_FIRST_TIME, DOSE_CANNOT_BE_TAKEN_MENU);
        ivrResponse = caller.enter("2");
        IVRAssert.assertAudioFilesPresent(ivrResponse, PLEASE_CARRY_SMALL_BOX);
    }

    private IVRResponse patientConfirmsTakingHisPreviousDose(IVRResponse ivrResponse) {
        IVRAssert.assertAudioFilesPresent(ivrResponse, YOUR, YESTERDAYS, DOSE_NOT_RECORDED, YESTERDAY, YOU_WERE_SUPPOSED_TO_TAKE, FROM_THE_BOTTLE_FOR_PREVIOUS_DOSAGE, PREVIOUS_DOSE_MENU);
        ivrResponse = caller.enter("1");
        IVRAssert.assertAudioFilesPresent(ivrResponse, YOU_SAID_YOU_TOOK, YESTERDAYS_CONFIRMATION, DOSE_TAKEN, DOSE_RECORDED);
        return ivrResponse;
    }

    private void patientListensToHisAdherencePercentage(IVRResponse ivrResponse) {
        IVRAssert.assertAudioFilesPresent(ivrResponse, YOUR_ADHERENCE_IS_NOW, "Num_050", PERCENT);
    }
}
