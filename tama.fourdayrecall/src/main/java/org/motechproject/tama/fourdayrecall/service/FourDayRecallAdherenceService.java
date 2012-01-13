package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.LocalDate;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.ivr.service.AdherenceServiceStrategy;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FourDayRecallAdherenceService implements AdherenceServiceStrategy {
    private AllTreatmentAdvices allTreatmentAdvices;
    private WeeklyAdherenceLogService weeklyAdherenceLogService;
    private FourDayRecallDateService fourDayRecallDateService;

    @Autowired
    public FourDayRecallAdherenceService(AllTreatmentAdvices allTreatmentAdvices, WeeklyAdherenceLogService weeklyAdherenceLogService,
                                         FourDayRecallDateService fourDayRecallDateService, AdherenceService adherenceService) {
        this.weeklyAdherenceLogService = weeklyAdherenceLogService;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.fourDayRecallDateService = fourDayRecallDateService;
        adherenceService.register(CallPreference.FourDayRecall, this);
    }

    public int getAdherencePercentageForPreviousWeek(String patientId) {
        return adherencePercentageFor(getAdherenceLog(patientId, 1));
    }

    public int adherencePercentageFor(int numDaysMissed) {
        return (fourDayRecallDateService.DAYS_TO_RECALL - numDaysMissed) * 100 / fourDayRecallDateService.DAYS_TO_RECALL;
    }

    protected int adherencePercentageFor(WeeklyAdherenceLog weeklyAdherenceLog) {
        if (weeklyAdherenceLog == null) return 0;
        return adherencePercentageFor(weeklyAdherenceLog.getNumberOfDaysMissed());
    }

    protected WeeklyAdherenceLog getAdherenceLog(String patientId, int weeksBefore) {
        return weeklyAdherenceLogService.get(patientId, weeksBefore);
    }

    public boolean isAdherenceFalling(int dosageMissedDays, String patientId) {
        return adherencePercentageFor(dosageMissedDays) < getAdherencePercentageForPreviousWeek(patientId);
    }

    protected int getAdherencePercentageForCurrentWeek(String patientId) {
        return adherencePercentageFor(getAdherenceLog(patientId, 0));
    }

    @Override
    public boolean wasAnyDoseMissedLastWeek(Patient patient) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());
        if (fourDayRecallDateService.isFirstTreatmentWeek(patient, treatmentAdvice)) return false;
        return ((double) getAdherencePercentageForPreviousWeek(patient.getId()) != 100.0);
    }

    @Override
    public boolean wasAnyDoseTakenLateSince(Patient patient, LocalDate since) {
        return false;
    }
}