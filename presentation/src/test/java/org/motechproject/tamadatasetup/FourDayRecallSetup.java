package org.motechproject.tamadatasetup;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.listener.FourDayRecallListener;
import org.motechproject.tamadatasetup.domain.FourDayRecallCall;
import org.motechproject.tamadatasetup.domain.FourDayRecallResponse;
import org.motechproject.tamadatasetup.domain.FourDayRecallSetupConfiguration;
import org.motechproject.tamadatasetup.service.TAMADateTimeService;
import org.motechproject.tamafunctional.framework.FunctionalTestObject;
import org.motechproject.tamafunctional.framework.MyWebClient;
import org.motechproject.tamafunctional.framework.ScheduledTaskRunner;
import org.motechproject.tamafunctional.testdataservice.PatientCallService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationDataSetupContext.xml")
public class FourDayRecallSetup extends FunctionalTestObject {
    @Autowired
    FourDayRecallSetupConfiguration configuration;

    @Test
    public void basedOnConfiguration() throws IOException {
        String patientDocId = configuration.patientsDocumentId();
        String phoneNumber = configuration.phoneNumber();
        String pinNumber = configuration.pinNumber();
        String adherenceResponse = configuration.adherenceResponse();

        MyWebClient webClient = new MyWebClient();
        ScheduledTaskRunner scheduledTaskRunner = new ScheduledTaskRunner(webClient);
        PatientCallService patientCallService = new PatientCallService(webClient);
        TAMADateTimeService dateTimeService = new TAMADateTimeService(webClient);

        HashMap<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put(FourDayRecallListener.PATIENT_DOC_ID_KEY, patientDocId);
        eventParams.put(FourDayRecallListener.RETRY_EVENT_KEY, true);

        FourDayRecallResponse fourDayRecallResponse = FourDayRecallResponse.parse(adherenceResponse);
        for (FourDayRecallCall fourDayRecallCall : fourDayRecallResponse.calls()) {
            logInfo("Four ");
            dateTimeService.adjustDateTime(fourDayRecallCall.callTime());
            scheduledTaskRunner.trigger(FourDayRecallListener.class, "handle", eventParams);
            patientCallService.takenPill(phoneNumber, pinNumber, fourDayRecallCall.numberOfDosageTaken());
        }
    }
}