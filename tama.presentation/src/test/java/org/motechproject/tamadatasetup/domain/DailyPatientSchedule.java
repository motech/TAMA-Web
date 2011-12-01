package org.motechproject.tamadatasetup.domain;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;

public class DailyPatientSchedule {
    private PillReminderDataSetupConfiguration configuration;
    private int numberOfDosageToBeTaken;
    private int numberOfDosageTakenSoFar;
    private int numberOfDays;

    public DailyPatientSchedule(PillReminderDataSetupConfiguration configuration, int numberOfDosageToBeTaken) {
        this.configuration = configuration;
        this.numberOfDosageToBeTaken = numberOfDosageToBeTaken;
    }

    public DailyPatientEvents nextDaysActivities() {
        LocalDate currentDate = configuration.treatmentAdviceGivenDate().plusDays(configuration.startFromDaysAfterTreatmentAdvice()).plusDays(numberOfDays);
        DailyPatientEvents dailyPatientEvents = new DailyPatientEvents().
                morningDosageDateTime(dateTimeFrom(currentDate, configuration.morningDoseTime())).
                eveningDosageDateTime(dateTimeFrom(currentDate, configuration.eveningDoseTime()));

        if (numberOfDosageTakenSoFar <= numberOfDosageToBeTaken) {
            dailyPatientEvents.dosageTaken(true);
            numberOfDosageTakenSoFar++;
        }

        if (Days.daysBetween(configuration.treatmentAdviceGivenDate(), currentDate).getDays() > 35
                && configuration.treatmentAdviceGivenDate().getDayOfWeek() == currentDate.getDayOfWeek())
            dailyPatientEvents.runAdherenceTrendJob(true);

        Time bestCallTime = configuration.bestCallTime();
        dailyPatientEvents.bestCallTime(dateTimeFrom(currentDate, bestCallTime));

        numberOfDays++;
        return dailyPatientEvents;
    }

    private DateTime dateTimeFrom(LocalDate currentDate, Time time) {
        return time == null ? null : DateUtil.newDateTime(currentDate, time);
    }
}
