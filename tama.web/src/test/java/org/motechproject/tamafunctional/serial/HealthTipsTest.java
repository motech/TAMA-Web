package org.motechproject.tamafunctional.serial;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamadatasetup.service.TAMADateTimeService;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.test.ivr.IVRAssert;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestLabResult;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctional.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;
import org.motechproject.util.DateUtil;

import java.io.File;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.motechproject.tama.ivr.TamaIVRMessage.*;

public class HealthTipsTest extends BaseIVRTest {

    private TestPatient patient;
    private TAMADateTimeService tamaDateTimeService;

    @Before
    public void setUp() {
        super.setUp();
        TestClinician clinician = TestClinician.withMandatory();
        patient = TestPatient.withMandatory();
        PatientDataService patientDataService = new PatientDataService(webDriver);

        tamaDateTimeService = new TAMADateTimeService(webClient);
        tamaDateTimeService.adjustDateTime(DateUtil.now().minusWeeks(2));

        TestTreatmentAdvice treatmentAdvice = setUpTreatmentAdviceToStartFrom2WeeksAgo();
        new ClinicianDataService(webDriver).createWithClinic(clinician);
        patientDataService.registerAndActivate(patient, clinician);
        TestLabResult labResult = TestLabResult.withMandatory().results(Arrays.asList("60", "10"));
        patientDataService.createRegimenWithLabResults(patient, clinician, treatmentAdvice, labResult);

        tamaDateTimeService.adjustDateTime(DateUtil.now());
    }

    private TestTreatmentAdvice setUpTreatmentAdviceToStartFrom2WeeksAgo() {
        TestDrugDosage[] drugDosages = TestDrugDosage.create("Efferven", "Combivir");
        LocalDate twoWeeksAgo = DateUtil.today().minusWeeks(2);
        drugDosages[0].startDate(twoWeeksAgo);
        drugDosages[1].startDate(twoWeeksAgo);
        return TestTreatmentAdvice.withExtrinsic(drugDosages);
    }

    @Test
    public void shouldPlayHealthTips_WhenARTLessThan1Month_AndAllDosesTakenInLastWeek_AndPatientOnDailyPill() {
        for (int i = 7; i >= 0; i--) {
            tamaDateTimeService.adjustDateTime(DateUtil.now().minusDays(i));
            recordCurrentDosageAsTaken();
        }
        tamaDateTimeService.adjustDateTime(DateUtil.now());
        assertHealthTipsForRegimenLessThanOneMonthIsPlayed();
    }

    public void recordCurrentDosageAsTaken() {
        caller = caller(patient);
        caller.call();
        IVRResponse ivrResponse = caller.enter(patient.patientPreferences().passcode());
        IVRAssert.asksForCollectDtmfWith(ivrResponse, ITS_TIME_FOR_THE_PILL, FROM_THE_BOTTLE, DOSE_TAKEN_MENU_OPTION, SYMPTOMS_REPORTING_MENU_OPTION, HEALTH_TIPS_MENU_OPTION);
        ivrResponse = caller.enter("1");
        IVRAssert.assertAudioFilesPresent(ivrResponse, DOSE_TAKEN_ON_TIME);
        caller.hangup();
    }

    private void assertHealthTipsForRegimenLessThanOneMonthIsPlayed() {
        ArrayList<String> audiosPlayed = new ArrayList<String>();
        caller = caller(patient);
        caller.call();
        IVRResponse ivrResponse = caller.enter(patient.patientPreferences().passcode());
        IVRAssert.asksForCollectDtmfWith(ivrResponse, YOUR_NEXT_DOSE_IS, TOMORROW, SYMPTOMS_REPORTING_MENU_OPTION, HEALTH_TIPS_MENU_OPTION);

        Map<String, List<String>> healthTips = getExpectedHealthTips();

        List<String> priority2HealthTips = healthTips.get("2");
        List<String> priority3HealthTips = healthTips.get("3");

        collectAudioFiles(audiosPlayed, priority2HealthTips.size() + priority3HealthTips.size());
        caller.hangup();

        assertEquals(priority2HealthTips.size() + priority3HealthTips.size(), audiosPlayed.size());
        assertHealthTipsPlayed(priority2HealthTips, audiosPlayed.subList(0, priority2HealthTips.size()));
        assertHealthTipsPlayed(priority3HealthTips, audiosPlayed.subList(priority2HealthTips.size(), audiosPlayed.size()));
    }

    public Map<String, List<String>> getExpectedHealthTips() {
        HashMap<String, List<String>> priorityMap = new HashMap<String, List<String>>();

        List<String> priority2Array = new ArrayList<String>();
        priority2Array.addAll(Arrays.asList("ht001a", "ht002a", "ht003a", "ht004a", "ht005a", "ht011a", "ht012a", "ht013a", "ht017a", "ht018a"));
        priority2Array.addAll(Arrays.asList("ht019a", "ht021a", "ht014a"));

        List<String> priority3Array = new ArrayList<String>();
        priority3Array.addAll(Arrays.asList("ht033a", "ht034a", "ht035a", "ht036a", "ht037a", "ht038a", "ht039a", "ht040a"));

        priorityMap.put("2", priority2Array);
        priorityMap.put("3", priority3Array);

        return priorityMap;
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
        int beginIndex = audioFile.lastIndexOf(File.separator) > 0 ? audioFile.lastIndexOf(File.separator) + 1 : 0;
        int endIndex = audioFile.length() > 4 ? audioFile.length() - 4 : audioFile.length();
        return audioFile.substring(beginIndex, endIndex);
    }

    private void assertHealthTipsPlayed(List<String> expectedHealthTips, List<String> actualHealthTipsPlayed) {
        for (String expectedHealthTip : expectedHealthTips) {
            assertTrue("expected:" + expectedHealthTip, actualHealthTipsPlayed.contains(expectedHealthTip));
        }
    }
}
