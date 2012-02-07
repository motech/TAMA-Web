package org.motechproject.tamadatasetup;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.ReminderEventHandler;
import org.motechproject.tama.dailypillreminder.listener.AdherenceTrendListener;
import org.motechproject.tama.outbox.listener.OutboxCallListener;
import org.motechproject.tamadatasetup.domain.DailyPatientEvents;
import org.motechproject.tamadatasetup.domain.DailyPatientSchedule;
import org.motechproject.tamadatasetup.domain.ExpectedDailyPillAdherence;
import org.motechproject.tamadatasetup.domain.PillReminderDataSetupConfiguration;
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
@ContextConfiguration(locations = "classpath*:**/applicationDataSetupContext.xml")
public class DailyPillReminderSetup extends FunctionalTestObject {
    @Autowired
    PillReminderDataSetupConfiguration configuration;
    private ScheduledTaskManager scheduledTaskManager;
    private TAMADateTimeService dateTimeService;
    private PatientCallService patientCallService;

    @Before
    public void setUp() {
        MyWebClient webClient = new MyWebClient();
        patientCallService = new PatientCallService(webClient, 5);
        dateTimeService = new TAMADateTimeService(webClient);
        scheduledTaskManager = new ScheduledTaskManager(webClient);
    }

    @Test
    public void allPillsTaken() throws IOException {
        percentagePillTaken(100);
    }

    @Test
    public void noPillsTaken() throws IOException {
        percentagePillTaken(0);
    }

    @Test
    public void percentagePillTaken() throws IOException {
        int percentage = configuration.percentageOfPillTaken();
        percentagePillTaken(percentage);
    }

    private void percentagePillTaken(int percentage) throws IOException {
        Time morningDoseTime = configuration.morningDoseTime();
        Time eveningDoseTime = configuration.eveningDoseTime();
        String jobId = configuration.dosageId();

        ExpectedDailyPillAdherence dailyPillAdherence = new ExpectedDailyPillAdherence(configuration.numberOfDaysToRunFor(), percentage, morningDoseTime, eveningDoseTime);
        int numberOfDosageTaken = dailyPillAdherence.numberOfDosageTaken();
        logInfo(String.format("Number of dosage which would be taken: %s, in the period of %s days.", numberOfDosageTaken, configuration.numberOfDaysToRunFor()));

        scheduledTaskManager.clear();
        DailyPatientSchedule dailyPatientSchedule = new DailyPatientSchedule(configuration, numberOfDosageTaken);
        for (int i = 1; i <= configuration.numberOfDaysToRunFor(); i++) {
            DailyPatientEvents dailyPatientEvents = dailyPatientSchedule.nextDaysActivities();
            logInfo("===================  DAY# %s, DATE: %s  ==============================================================", "" + i, dailyPatientEvents.dateTime());

            if (dailyPatientEvents.runAdherenceTrendJob()) {
                logInfo("RUNNING ADHERENCE TREND JOB");
                scheduledTaskManager.trigger(AdherenceTrendListener.class, "handleAdherenceTrendEvent", configuration.patientDocId());
            }

            if (dailyPatientEvents.dosageTaken()) {
                takeCall(morningDoseTime, dailyPatientEvents.morningDosageDateTime(), jobId);
                takeCall(eveningDoseTime, dailyPatientEvents.eveningDosageDateTime(), jobId);
            } else {
                donotTakeCall(morningDoseTime, dailyPatientEvents.morningDosageDateTime(), jobId);
                donotTakeCall(eveningDoseTime, dailyPatientEvents.eveningDosageDateTime(), jobId);
            }

            if (configuration.signedUpForOutboxCall()) {
                patientCallService.clearLastCall();
                dateTimeService.adjustDateTime(dailyPatientEvents.bestCallTime());
                scheduledTaskManager.trigger(OutboxCallListener.class, "handleOutBoxCall", configuration.patientDocId());
                if (patientCallService.gotCall()) {
                    logInfo("GOT OUTBOX CALL AT: %s", dailyPatientEvents.bestCallTime());
                    patientCallService.listenToOutbox(configuration.pinNumber());
                } else {
                    logInfo("NO OUTBOX CALL AT: %s", dailyPatientEvents.bestCallTime());
                }
            }
        }
        logInfo("TO CHECK THE STATUS OF SCHEDULED JOBS YOU USED, YOU CAN USE %s/%s", TamaUrl.baseFor(ScheduledTaskManager.BASE_SCHEDULER_INVOKER_URL), "list");
    }

    private void donotTakeCall(Time dosageTime, DateTime dateTime, String jobId) {
        if (dosageTime != null) {
            triggerDailyReminderJob(dosageTime, dateTime, jobId);
            logInfo("NOT TAKING CALL AT %s", dateTime);
        }
    }

    private void takeCall(Time dosageTime, DateTime dateTime, String jobId) throws IOException {
        if (dosageTime != null) {
            String pinNumber = triggerDailyReminderJob(dosageTime, dateTime, jobId);
            logInfo("TAKING CALL AT %s", dateTime);
            patientCallService.takenPill(pinNumber);
        }
    }

    private String triggerDailyReminderJob(Time dosageTime, DateTime dateTime, String jobId) {
        patientCallService.clearLastCall();
        String pinNumber = configuration.pinNumber();
        DateTime dosageCallTime = dateTime.withHourOfDay(dosageTime.getHour()).withMinuteOfHour(dosageTime.getMinute());
        dateTimeService.adjustDateTime(dosageCallTime);
        scheduledTaskManager.trigger(ReminderEventHandler.class, "handleEvent", jobId);
        return pinNumber;
    }
}
