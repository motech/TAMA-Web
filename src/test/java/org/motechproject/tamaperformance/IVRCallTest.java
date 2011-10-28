package org.motechproject.tamaperformance;

import org.junit.Test;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.testdata.TestClinic;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;

public class IVRCallTest extends BaseIVRTest {

    private int callCount = 20;

    @Test
    public void executePillReminderFlow() throws IOException {
        String patientId = System.getProperty("patientId");
        String clinicName = System.getProperty("clinicName");
        TestPatient patient = selectFirst(new TestSample().patients, having(on(TestPatient.class).patientId(), equalTo(patientId)));
        TestClinic clinic = selectFirst(new TestSample().clinics, having(on(TestClinic.class).name(), equalTo(clinicName)));
        while (callCount-- > 0) {
            callTama(patient, clinic);
        }
    }

    private void callTama(TestPatient patient, TestClinic clinic) throws IOException {
        caller = caller(patient);
        IVRResponse ivrResponse = caller.call();
        asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);

        ivrResponse = caller.enter("1234#");
        asksForCollectDtmfWith(ivrResponse, welcomeAudioForClinic(clinic), TamaIVRMessage.ITS_TIME_FOR_THE_PILL, "pillazt3tc_combivir", "pillefv_efavir", TamaIVRMessage.PILL_FROM_THE_BOTTLE, TamaIVRMessage.PILL_CONFIRM_CALL_MENU);

        ivrResponse = caller.enter("3#");
        assertAudioFilesPresent(ivrResponse, TamaIVRMessage.NO_MESSAGES);

        caller.hangup();
    }

    private String welcomeAudioForClinic(TestClinic clinic) {
        return "welcome_to_" + clinic.name();
    }
}

