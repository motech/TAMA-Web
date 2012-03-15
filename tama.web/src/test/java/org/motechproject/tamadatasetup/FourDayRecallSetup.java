package org.motechproject.tamadatasetup;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.fourdayrecall.listener.FourDayRecallListener;
import org.motechproject.tamadatasetup.domain.FourDayRecallPatientEvents;
import org.motechproject.tamadatasetup.domain.FourDayRecallPatientSchedule;
import org.motechproject.tamadatasetup.domain.FourDayRecallSetupConfiguration;
import org.motechproject.tamadatasetup.service.TAMADateTimeService;
import org.motechproject.tamafunctionalframework.framework.FunctionalTestObject;
import org.motechproject.tamafunctionalframework.framework.MyWebClient;
import org.motechproject.tamafunctionalframework.framework.ScheduledTaskManager;
import org.motechproject.tamafunctionalframework.framework.TamaUrl;
import org.motechproject.tamafunctionalframework.testdataservice.PatientCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationDataSetup.xml")
public class FourDayRecallSetup extends FunctionalTestObject {
    @Autowired
    FourDayRecallSetupConfiguration configuration;

    @Test
    public void basedOnConfiguration() throws IOException {
        String patientDocId = configuration.patientDocId();
        String pinNumber = configuration.pinNumber();

        MyWebClient webClient = new MyWebClient();
        ScheduledTaskManager scheduledTaskManager = new ScheduledTaskManager(webClient);
        PatientCallService patientCallService = new PatientCallService(webClient, 5);
        TAMADateTimeService dateTimeService = new TAMADateTimeService(webClient);

        scheduledTaskManager.clear();

        FourDayRecallPatientSchedule schedule = new FourDayRecallPatientSchedule(configuration);
        for (int i = 0; i < schedule.numberOfWeeks(); i++) {
            logInfo("============================ WEEK #%s ==========================", i + 1);
            FourDayRecallPatientEvents events = schedule.events();

            dateTimeService.adjustDateTime(events.callTime());
            if (events.runFallingTrendJob())
                scheduledTaskManager.trigger(FourDayRecallListener.class, "handleWeeklyFallingAdherence", patientDocId);

            patientCallService.clearLastCall();
            scheduledTaskManager.trigger(FourDayRecallListener.class, "handle", "0" + patientDocId);
            patientCallService.takenPill(pinNumber, events.numberOfDosageTaken());
        }

        logInfo("TO CHECK THE STATUS OF SCHEDULED JOBS YOU USED, YOU CAN USE %s/%s", TamaUrl.baseFor(ScheduledTaskManager.BASE_SCHEDULER_INVOKER_URL), "list");
    }
}