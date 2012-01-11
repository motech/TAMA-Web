package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.LocalDate;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.ivr.service.AdherenceServiceStrategy;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FourDayRecallAdherenceService implements AdherenceServiceStrategy {
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    private AllPatients allPatients;
    private FourDayRecallDateService fourDayRecallDateService;

    @Autowired
    public FourDayRecallAdherenceService(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, AllWeeklyAdherenceLogs allWeeklyAdherenceLogs,
                                         FourDayRecallDateService fourDayRecallDateService, AdherenceService adherenceService) {
        this.allPatients = allPatients;
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
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
        Patient patient = allPatients.get(patientId);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        LocalDate startDateForPreviousWeek = fourDayRecallDateService.treatmentWeekStartDate(DateUtil.today(), patient, treatmentAdvice).minusWeeks(weeksBefore);

        List<WeeklyAdherenceLog> logs = allWeeklyAdherenceLogs.findLogsByWeekStartDate(patientId, treatmentAdvice.getId(), startDateForPreviousWeek);
        return logs.size() == 0 ? null : logs.get(0);
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