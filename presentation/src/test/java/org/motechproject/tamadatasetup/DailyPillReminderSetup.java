package org.motechproject.tamadatasetup;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.tamadatasetup.domain.DataSetupConfiguration;
import org.motechproject.tamadatasetup.domain.ExpectedDailyPillAdherence;
import org.motechproject.tamadatasetup.service.TAMADateTimeService;
import org.motechproject.tamafunctional.framework.FunctionalTestObject;
import org.motechproject.tamafunctional.framework.MyWebClient;
import org.motechproject.tamafunctional.testdataservice.PatientCallService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationDataSetupContext.xml")
public class DailyPillReminderSetup extends FunctionalTestObject {
    @Autowired
    DataSetupConfiguration configuration;

    @Test
    public void allPillsTaken() {
        percentagePillTaken(100);
    }

    @Test
    public void noPillsTaken() {
        percentagePillTaken(0);
    }

    @Test
    public void percentagePillTaken() {
        int percentage = configuration.percentageOfPillTaken();
        percentagePillTaken(percentage);
    }

    private void percentagePillTaken(int percentage) {
        MyWebClient webClient = new MyWebClient();
        PatientCallService patientCallService = new PatientCallService(webClient);
        TAMADateTimeService dateTimeService = new TAMADateTimeService(webClient);

        Time morningDoseTime = configuration.morningDoseTime();
        Time eveningDoseTime = configuration.eveningDoseTime();

        ExpectedDailyPillAdherence dailyPillAdherence = new ExpectedDailyPillAdherence(configuration.numberOfDaysToRunFor(), percentage, morningDoseTime, eveningDoseTime);
        int numberOfDosageTaken = dailyPillAdherence.numberOfDosageTaken();
        logger.info(String.format("Number of dosage which would be taken: %s, in the period of %s days.", numberOfDosageTaken, configuration.numberOfDaysToRunFor()));
        DateTime now = DateUtil.now();
        for (int i = 0; i < numberOfDosageTaken; i++) {
            DateTime dateTime = now.plusDays(i);

            call(patientCallService, dateTimeService, morningDoseTime, dateTime);
            call(patientCallService, dateTimeService, eveningDoseTime, dateTime);
        }
    }

    private void call(PatientCallService patientCallService, TAMADateTimeService dateTimeService, Time dosageTime, DateTime dateTime) {
        String phoneNumber = configuration.phoneNumber();
        String pinNumber = configuration.pinNumber();
        if (dosageTime != null) {
            DateTime dosageCallTime = dateTime.withHourOfDay(dosageTime.getHour()).withMinuteOfHour(dosageTime.getMinute());
            dateTimeService.adjustDateTime(dosageCallTime);
            patientCallService.takenPill(phoneNumber, pinNumber);
        }
    }
}
