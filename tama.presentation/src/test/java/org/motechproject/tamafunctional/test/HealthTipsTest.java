package org.motechproject.tamafunctional.test;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctional.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.motechproject.tamacallflow.ivr.TamaIVRMessage.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationFunctionalTestContext.xml")
public class HealthTipsTest extends BaseIVRTest {

    private TestPatient patient;

    @Before
    public void setUp() {
        super.setUp();
        TestClinician clinician = TestClinician.withMandatory();
        new ClinicianDataService(webDriver).createWithClinc(clinician);
        patient = TestPatient.withMandatory();
        new PatientDataService(webDriver).createTestPatientForSymptomReporting(patient, clinician);
    }

    public Map<String, List> getHealthTipsWithPriorityWhenARTLessThan1MonthAndPatientOnDailyPillAndDosageMissed() {
        HashMap<String, List> priorityMap = new HashMap<String, List>();

        List<String> priority1Array = new ArrayList<String>();
        priority1Array.addAll(Arrays.asList("ht004a", "ht002a", "ht011a", "ht012a", "ht021a", "ht014a"));

        List<String> priority2Array = new ArrayList<String>();
        priority2Array.addAll(Arrays.asList("ht001a", "ht002a", "ht003a", "ht004a", "ht005a", "ht011a", "ht012a", "ht013a", "ht017a", "ht018a"));
        priority2Array.addAll(Arrays.asList("ht019a", "ht021a", "ht014a"));

        List<String> priority3Array = new ArrayList<String>();
        priority3Array.addAll(Arrays.asList("ht033a", "ht034a", "ht035a", "ht036a", "ht037a", "ht038a", "ht039a", "ht040a"));

        priorityMap.put("1", priority1Array);
        priorityMap.put("2", priority2Array);
        priorityMap.put("3", priority3Array);

        return priorityMap;
    } 

    @Ignore
    @Test
    public void shouldPlayHealthTips_WhenARTLessThan1MonthAndPatientOnDailyPill() throws IOException {
        assertHealthTipsForMissedDoseIsPlayed();
        recordDosageAsTaken();
        assertHealthTipsForRegimenLessThanOneMonthIsPlayed();
    }

    private void assertHealthTipsForMissedDoseIsPlayed() {
        ArrayList<String> audiosPlayed = new ArrayList<String>();
        caller = caller(patient);
        caller.call();
        IVRResponse ivrResponse = caller.enter("5678");
        asksForCollectDtmfWith(ivrResponse, ITS_TIME_FOR_THE_PILL, PILL_FROM_THE_BOTTLE, DOSE_TAKEN_MENU_OPTION, SYMPTOMS_REPORTING_MENU_OPTION, HEALTH_TIPS_MENU_OPTION);

        // collect only highest priority health tips and assert
        List<String> highestPriorityFilesExpectedToBePlayed = getHealthTipsWithPriorityWhenARTLessThan1MonthAndPatientOnDailyPillAndDosageMissed().get("1");
        collectAudioFiles(audiosPlayed, highestPriorityFilesExpectedToBePlayed.size());
        caller.hangup();

        assertEquals(highestPriorityFilesExpectedToBePlayed.size(), audiosPlayed.size());
        assertHealthTipsPlayed(highestPriorityFilesExpectedToBePlayed, audiosPlayed);

    }

    public void recordDosageAsTaken() throws IOException {
        caller = caller(patient);
        caller.call();
        IVRResponse ivrResponse = caller.enter("5678");
        asksForCollectDtmfWith(ivrResponse, ITS_TIME_FOR_THE_PILL, PILL_FROM_THE_BOTTLE, DOSE_TAKEN_MENU_OPTION, SYMPTOMS_REPORTING_MENU_OPTION, HEALTH_TIPS_MENU_OPTION);
        ivrResponse = caller.enter("1");
        assertAudioFilesPresent(ivrResponse, DOSE_TAKEN_ON_TIME);
        ivrResponse = caller.listenMore();
        assertAudioFilesPresent(ivrResponse, HANGUP_OR_MAIN_MENU);
        caller.hangup();
    }

    private void assertHealthTipsForRegimenLessThanOneMonthIsPlayed() {
        ArrayList<String> audiosPlayed = new ArrayList<String>();
        caller = caller(patient);
        caller.call();
        IVRResponse ivrResponse = caller.enter("5678");
        asksForCollectDtmfWith(ivrResponse, YOUR_NEXT_DOSE_IS, TOMORROW, SYMPTOMS_REPORTING_MENU_OPTION, HEALTH_TIPS_MENU_OPTION);

        Map<String, List> tipsWithPriority = getHealthTipsWithPriorityWhenARTLessThan1MonthAndPatientOnDailyPillAndDosageMissed();
        List<String> healthTipsAlreadyPlayed = tipsWithPriority.get("1");

        List<String> priority2HealthTips = (List<String>) tipsWithPriority.get("2");
        priority2HealthTips.removeAll(healthTipsAlreadyPlayed);

        List<String> priority3HealthTips = (List<String>) tipsWithPriority.get("3");
        priority3HealthTips.removeAll(healthTipsAlreadyPlayed);
        priority3HealthTips.removeAll(priority2HealthTips);

        collectAudioFiles(audiosPlayed, priority2HealthTips.size() + priority3HealthTips.size());
        caller.hangup();

        assertEquals(priority2HealthTips.size() + priority3HealthTips.size(), audiosPlayed.size());
        assertHealthTipsPlayed(priority2HealthTips, audiosPlayed.subList(0, priority2HealthTips.size()));
        assertHealthTipsPlayed(priority3HealthTips, audiosPlayed.subList(priority2HealthTips.size(), audiosPlayed.size()));

    }

    private void collectAudioFiles(ArrayList<String> audiosPlayed, int numberOfFilesToCollect) {
        boolean collectTwoFiles = true;
        for (int collected = 0; collected < numberOfFilesToCollect; ) {
            if ((numberOfFilesToCollect - collected) == 1)
                collectTwoFiles = false;
            collected = collected + listenTo2HealthTips(audiosPlayed, collectTwoFiles);
            getBackToMainMenu();
        }
    }

    private void getBackToMainMenu() {
        caller.listenMore();
        caller.listenMore();
    }

    private int listenTo2HealthTips(ArrayList<String> audiosPlayed, boolean listenToBothHealthTips) {
        int numberOfFilesCollected = 1;

        IVRResponse ivrResponse = caller.enter("5");
        collectFile(audiosPlayed, ivrResponse);

        ivrResponse = caller.listenMore();

        if (listenToBothHealthTips) {
            collectFile(audiosPlayed, ivrResponse);
            numberOfFilesCollected = 2;
        }
        return numberOfFilesCollected;
    }

    private void collectFile(ArrayList<String> audiosPlayed, IVRResponse ivrResponse) {
        String audioFile = ivrResponse.audiosPlayed();
        audiosPlayed.add(getHealthTipName(audioFile));
    }

    private String getHealthTipName(String audioFile) {
        int beginIndex = audioFile.lastIndexOf(File.separator) >0 ? audioFile.lastIndexOf(File.separator) + 1: 0;
        int endIndex = audioFile.length() > 4 ? audioFile.length() - 4 : audioFile.length();
        return audioFile.substring(beginIndex, endIndex);
    }

    private void assertHealthTipsPlayed(List<String> expectedHealthTips, List<String> actualHealthTipsPlayed) {
        for (String expectedHealthTip : expectedHealthTips) {
            assertTrue(actualHealthTipsPlayed.contains(expectedHealthTip));
        }
    }
}
