package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.LocalDate;
import org.motechproject.tama.common.NoAdherenceRecordedException;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.ivr.service.AdherenceServiceStrategy;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FourDayRecallAdherenceService implements AdherenceServiceStrategy {
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    private WeeklyAdherenceLogService weeklyAdherenceLogService;
    private FourDayRecallDateService fourDayRecallDateService;

    @Autowired
    public FourDayRecallAdherenceService(AllTreatmentAdvices allTreatmentAdvices,
                                         AllWeeklyAdherenceLogs allWeeklyAdherenceLogs,
                                         WeeklyAdherenceLogService weeklyAdherenceLogService,
                                         FourDayRecallDateService fourDayRecallDateService,
                                         AdherenceService adherenceService) {
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.weeklyAdherenceLogService = weeklyAdherenceLogService;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.fourDayRecallDateService = fourDayRecallDateService;
        adherenceService.register(CallPreference.FourDayRecall, this);
    }

    public void recordAdherence(String patientId, int numberOfDaysMissed) {
        weeklyAdherenceLogService.createLog(patientId, numberOfDaysMissed);
    }

    public int getAdherencePercentageForPreviousWeek(String patientId) throws NoAdherenceRecordedException {
        return adherencePercentageFor(weeklyAdherenceLogService.get(patientId, 1));
    }

    public Map<LocalDate, Double> getAdherenceOverTime(String patientDocId) throws NoAdherenceRecordedException {
        Map<LocalDate, Double> adherenceOverTime = new HashMap<LocalDate, Double>();
        List<WeeklyAdherenceLog> weeklyAdherenceLogs = allWeeklyAdherenceLogs.findAllByPatientId(patientDocId);
        for(WeeklyAdherenceLog log: weeklyAdherenceLogs){
            adherenceOverTime.put(log.getWeekStartDate(), (double) adherencePercentageFor(log));
        }
        return adherenceOverTime;
    }


    public int adherencePercentageFor(int numDaysMissed) {
        return (fourDayRecallDateService.DAYS_TO_RECALL - numDaysMissed) * 100 / fourDayRecallDateService.DAYS_TO_RECALL;
    }

    protected int adherencePercentageFor(WeeklyAdherenceLog weeklyAdherenceLog) throws NoAdherenceRecordedException {
        if (weeklyAdherenceLog == null) throw new NoAdherenceRecordedException("No Logs Found");
        if (weeklyAdherenceLog.getNotResponded()) return 0;
        return adherencePercentageFor(weeklyAdherenceLog.getNumberOfDaysMissed());
    }

    public boolean isAdherenceFalling(int dosageMissedDays, String patientId) {
        try {
            return adherencePercentageFor(dosageMissedDays) < getAdherencePercentageForPreviousWeek(patientId);
        } catch (NoAdherenceRecordedException e) {
            return false;
        }
    }

    protected int getAdherencePercentageForCurrentWeek(String patientId) throws NoAdherenceRecordedException {
        return adherencePercentageFor(weeklyAdherenceLogService.get(patientId, 0));
    }

    @Override
    public boolean wasAnyDoseMissedLastWeek(Patient patient) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());
        if (fourDayRecallDateService.isFirstTreatmentWeek(patient, treatmentAdvice)) return false;
        if (weeklyAdherenceLogService.get(patient.getId(), 1).getNotResponded()) return false;
        try {
            return ((double) getAdherencePercentageForPreviousWeek(patient.getId()) != 100.0);
        } catch (NoAdherenceRecordedException e) {
            return false;
        }
    }

    @Override
    public boolean wasAnyDoseTakenLateLastWeek(Patient patient) {
        return false;
    }
}