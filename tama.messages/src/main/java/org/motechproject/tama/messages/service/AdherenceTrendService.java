package org.motechproject.tama.messages.service;

import org.joda.time.DateTime;
import org.motechproject.tama.common.NoAdherenceRecordedException;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAdherenceService;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallDateService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdherenceTrendService {

    private DailyPillReminderAdherenceService dailyPillReminderAdherenceService;
    private FourDayRecallAdherenceService fourDayRecallAdherenceService;

    @Autowired
    public AdherenceTrendService(DailyPillReminderAdherenceService dailyPillReminderAdherenceService, FourDayRecallAdherenceService fourDayRecallAdherenceService) {
        this.dailyPillReminderAdherenceService = dailyPillReminderAdherenceService;
        this.fourDayRecallAdherenceService = fourDayRecallAdherenceService;
    }

    public double getAdherencePercentage(Patient patient, DateTime date) {
        try {
            if (patient.isOnDailyPillReminder()) {
                return dailyPillReminderAdherenceService.getAdherencePercentage(patient.getId(), date);
            } else {
                return fourDayRecallAdherenceService.getRunningAdherencePercentage(patient);
            }
        } catch (Exception e) {
            return 0d;
        }
    }

    public boolean isAdherenceFalling(Patient patient, DateTime dateTime) {
        try {
            double currentAdherence = getAdherencePercentage(patient, dateTime);
            double previousAdherence = getAdherenceForPreviousWeek(patient, dateTime);
            return currentAdherence < previousAdherence;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasAdherenceTrend(Patient patient, TreatmentAdvice advice, DateTime reference) {
        if (patient.isOnDailyPillReminder()) {
            return advice.hasAdherenceTrend(reference.toLocalDate());
        } else {
            return !new FourDayRecallDateService().isFirstTreatmentWeek(patient, advice);
        }
    }

    private double getAdherenceForPreviousWeek(Patient patient, DateTime dateTime) throws NoAdherenceRecordedException {
        if (patient.isOnDailyPillReminder()) {
            return dailyPillReminderAdherenceService.getAdherencePercentage(patient.getId(), dateTime.minusDays(7));
        } else {
            return fourDayRecallAdherenceService.getAdherencePercentageForPreviousWeek(patient.getId());
        }
    }
}
