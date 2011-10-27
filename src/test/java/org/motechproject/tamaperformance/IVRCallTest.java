package org.motechproject.tamaperformance;

import ch.lambdaj.Lambda;
import org.junit.Test;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;

public class IVRCallTest extends BaseIVRTest {

    private int callCount = 1;

    @Test
    public void executePillReminderFlow() throws IOException {
        String patientId = System.getProperty("patientId");
        TestPatient patient = selectFirst(new TestSample().patients, having(on(TestPatient.class).patientId(), equalTo(patientId)));
        while (callCount-- > 0)
            callTama(patient);
    }

    private void callTama(TestPatient patient) throws IOException {
        caller = caller(patient);
        IVRResponse ivrResponse = caller.call();
        asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);

        ivrResponse = caller.enter("1234#");
        asksForCollectDtmfWith(ivrResponse, "welcome_to_clinic1", TamaIVRMessage.ITS_TIME_FOR_THE_PILL, "pillazt3tc_combivir", "pillefv_efavir", TamaIVRMessage.PILL_FROM_THE_BOTTLE, TamaIVRMessage.PILL_CONFIRM_CALL_MENU);

        ivrResponse = caller.enter("1#");

    }
}

