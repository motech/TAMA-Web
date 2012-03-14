package org.motechproject.tamaregression.serial;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tamadatasetup.service.TAMADateTimeService;
import org.motechproject.tamafunctionalframework.ivr.BaseIVRTest;
import org.motechproject.tamafunctionalframework.ivr.IVRAssert;
import org.motechproject.tamafunctionalframework.testdata.PillReminderCallInfo;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;
import org.motechproject.util.DateUtil;

import java.io.IOException;

public class VariableDosageTest extends BaseIVRTest {

    public static final String TOOK_THE_DOSE = "1";
    public static final String DID_NOT_TAKE_THE_DOSE = "3";

    private TestPatient patient;
    private TestClinician clinician;
    private TAMADateTimeService tamaDateTimeService;
    private TestDrugDosage drugDosage;
    private DateTime now;

    @Before
    public void setUp() {
        super.setUp();
        tamaDateTimeService = new TAMADateTimeService(webClient);
        setupPatient();
        caller = caller(patient);
        now = DateUtil.now();
    }

    @After
    public void tearDown() throws IOException {
        tamaDateTimeService.adjustDateTime(now);
        super.tearDown();
    }

    private void setupPatient() {
        clinician = TestClinician.withMandatory();
        patient = TestPatient.withMandatory();
        enrollPatientIntoRegimen();
    }

    private void enrollPatientIntoRegimen() {
        drugDosage = setupVariableDosages();

        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(drugDosage).regimenName("d4T + 3TC + NVP").drugCompositionName("d4T+3TC+NVP").drugName("d4T+3TC+NVP");
        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.setupRegimenWithDependents(treatmentAdvice, patient, clinician);
    }

    private TestDrugDosage setupVariableDosages() {
        TestDrugDosage[] drugDosages = TestDrugDosage.create("Triomune");
        drugDosages[0].setVariableDose("1");
        return drugDosages[0];
    }

    @Test
    public void testAdherence() {
        DateTime time = getEveningTime();
        assertAsksForMorningDose(time);
        assertAsksForVariableDose(time.plusDays(1));
    }

    private void assertAsksForMorningDose(DateTime time) {
        //Morning dose is not yet reported
        tamaDateTimeService.adjustDateTime(time);

        IVRResponse ivrResponse = authenticate(caller.call());
        IVRAssert.asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.NOT_REPORTED_IF_TAKEN, "pilld4t3tcnvp_triomune");

        ivrResponse = caller.enter(TOOK_THE_DOSE);
        assertAdherenceIs("Num_100", ivrResponse);

        caller.hangup();
    }

    private void assertAsksForVariableDose(DateTime time) {
        tamaDateTimeService.adjustDateTime(time);

        IVRResponse ivrResponse = authenticate(caller.replyToCall(new PillReminderCallInfo(3)));
        IVRAssert.asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.ITS_TIME_FOR_THE_PILL_OUTGOING_CALL_FOR_CURRENT_DOSAGE, "pilld4t3tcnvp_triomune");

        ivrResponse = caller.enter(TOOK_THE_DOSE);
        IVRAssert.assertAudioFilesPresent(ivrResponse, TamaIVRMessage.DOSE_RECORDED);

        ivrResponse = caller.listenMore();
        IVRAssert.asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.YOU_WERE_SUPPOSED_TO_TAKE, "pilld4t3tcnvp_triomune", TamaIVRMessage.PREVIOUS_DOSE_MENU);

        ivrResponse = caller.enter(DID_NOT_TAKE_THE_DOSE);
        assertAdherenceIs("Num_066", ivrResponse);

        caller.hangup();
    }

    private IVRResponse authenticate(IVRResponse ivrResponse) {
        IVRAssert.asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);
        return caller.enter(patient.patientPreferences().passcode());
    }

    private void assertAdherenceIs(String adherencePercentage, IVRResponse ivrResponse) {
        IVRAssert.assertAudioFilesPresent(ivrResponse, adherencePercentage);
    }

    private DateTime getEveningTime() {
        Time time = Time.parseTime(drugDosage.dosageSchedule(), ":");
        if (time.getHour() < 12)
            time.setHour(time.getHour() + 12);
        return DateUtil.newDateTime(now.toLocalDate(), time);
    }
}
