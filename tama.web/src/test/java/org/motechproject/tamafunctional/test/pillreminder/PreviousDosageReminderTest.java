package org.motechproject.tamafunctional.test.pillreminder;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.testdata.PillReminderCallInfo;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;
import org.motechproject.tamafunctional.testdataservice.ScheduledJobDataService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.motechproject.tamacallflow.ivr.TamaIVRMessage.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationFunctionalTestContext.xml")
public class PreviousDosageReminderTest extends BaseIVRTest {
    @Autowired
    private ScheduledJobDataService scheduledJobDataService;
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
        String currentDosageId = scheduledJobDataService.currentDosageId(patient.id());
        caller.replyToCall(new PillReminderCallInfo(currentDosageId, 1));

        IVRResponse ivrResponse = caller.enter("1234");
        patientConfirmsNotTakingCurrentDose(ivrResponse);
        ivrResponse = caller.listenMore();
        ivrResponse = patientConfirmsTakingHisPreviousDose(ivrResponse);
        patientListensToHisAdherencePercentage(ivrResponse);
        ivrResponse = caller.listenMore();
        assertAudioFilesPresent(ivrResponse, HANGUP_OR_MAIN_MENU);
        caller.hangup();
    }

    private void patientConfirmsNotTakingCurrentDose(IVRResponse ivrResponse) {
        asksForCollectDtmfWith(ivrResponse, PILL_REMINDER_RESPONSE_MENU, ITS_TIME_FOR_THE_PILL, PILL_FROM_THE_BOTTLE);
        ivrResponse = caller.enter("3");
        assertAudioFilesPresent(ivrResponse, MISSED_PILL_FEEDBACK_FIRST_TIME, DOSE_CANNOT_BE_TAKEN_MENU);
        ivrResponse = caller.enter("2");
        assertAudioFilesPresent(ivrResponse, PLEASE_CARRY_SMALL_BOX);
    }

    private IVRResponse patientConfirmsTakingHisPreviousDose(IVRResponse ivrResponse) {
        assertAudioFilesPresent(ivrResponse, YOUR, YESTERDAYS, DOSE_NOT_RECORDED, YESTERDAY, YOU_WERE_SUPPOSED_TO_TAKE, FROM_THE_BOTTLE, PREVIOUS_DOSE_MENU);
        ivrResponse = caller.enter("1");
        assertAudioFilesPresent(ivrResponse, YOU_SAID_YOU_TOOK, YESTERDAYS_CONFIRMATION, DOSE_TAKEN, DOSE_RECORDED);
        return ivrResponse;
    }

    private void patientListensToHisAdherencePercentage(IVRResponse ivrResponse) {
        assertAudioFilesPresent(ivrResponse, YOUR_ADHERENCE_IS_NOW, "Num_050", PERCENT);
    }
}
