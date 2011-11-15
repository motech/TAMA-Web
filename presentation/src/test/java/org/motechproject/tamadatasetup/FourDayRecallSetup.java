package org.motechproject.tamadatasetup;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.tama.listener.FourDayRecallListener;
import org.motechproject.tamadatasetup.domain.FourDayRecallSetupConfiguration;
import org.motechproject.tamadatasetup.service.TAMADateTimeService;
import org.motechproject.tamafunctional.framework.MyWebClient;
import org.motechproject.tamafunctional.framework.ScheduledTaskRunner;
import org.motechproject.tamafunctional.testdataservice.PatientCallService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;

public class FourDayRecallSetup {
    @Autowired
    FourDayRecallSetupConfiguration configuration;

    @Test
    public void basedOnConfiguration() throws IOException {
        String patientDocId = configuration.patientsDocumentId();
        String phoneNumber = configuration.phoneNumber();
        String pinNumber = configuration.pinNumber();
        String pattern = configuration.weeklyPattern();

        MyWebClient webClient = new MyWebClient();
        ScheduledTaskRunner scheduledTaskRunner = new ScheduledTaskRunner(webClient);
        PatientCallService patientCallService = new PatientCallService(webClient);
        TAMADateTimeService dateTimeService = new TAMADateTimeService(webClient);

        HashMap<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put(FourDayRecallListener.PATIENT_DOC_ID_KEY, patientDocId);
        String[] numberOfDosagesTaken = StringUtils.split(pattern, ",");
        DateTime now = DateUtil.now();
        for (int i = 0; i < numberOfDosagesTaken.length; i++) {
            DateTime callTime = now.plusWeeks(i + 1);
            dateTimeService.adjustDateTime(callTime);
            scheduledTaskRunner.trigger(FourDayRecallListener.class, "handle", eventParams);
            patientCallService.takenPill(phoneNumber, pinNumber, Integer.parseInt(numberOfDosagesTaken[i]));
        }
    }
}