package org.motechproject.tama.service;

import org.joda.time.LocalDate;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.domain.WeeklyAdherenceLog;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.tama.repository.AllWeeklyAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FourDayRecallService {

    public static final int DAYS_TO_RECALL = 4;

    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    private AllTreatmentAdvices allTreatmentAdvices;

    @Autowired
    public FourDayRecallService(AllWeeklyAdherenceLogs allWeeklyAdherenceLogs, AllTreatmentAdvices allTreatmentAdvices) {
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.allTreatmentAdvices = allTreatmentAdvices;
    }

    public boolean isAdherenceCapturedForCurrentWeek(String patientDocId, String treatmentAdviceId, LocalDate startDateOfTreatmentAdvice) {
        LocalDate startDateForCurrentWeek = getStartDateForCurrentWeek(startDateOfTreatmentAdvice);
        return allWeeklyAdherenceLogs.findLogCountByPatientIDAndTreatmentAdviceIdAndDateRange(patientDocId, treatmentAdviceId, startDateForCurrentWeek, DateUtil.today()) > 0;
    }

    public LocalDate getStartDateForCurrentWeek(LocalDate startDateOfTreatmentAdvice) {
        DayOfWeek startDayForTreatmentAdvice = DayOfWeek.getDayOfWeek(startDateOfTreatmentAdvice);
        return DateUtil.pastDateWith(startDayForTreatmentAdvice, startDateOfTreatmentAdvice.plusDays(4));
    }

    public int adherencePercentageForPreviousWeek(String patientId) {
        int previousWeekAdherencePercentage = 0;
        WeeklyAdherenceLog logForPreviousWeek = getAdherenceLogForPreviousWeek(patientId);
        if(logForPreviousWeek != null){
            previousWeekAdherencePercentage = adherencePercentageFor(logForPreviousWeek);
        }
        return previousWeekAdherencePercentage;
    }

    public int adherencePercentageFor(int numDaysMissed) {
        return ((DAYS_TO_RECALL - numDaysMissed) / DAYS_TO_RECALL) * 100;
    }

    protected int adherencePercentageFor(WeeklyAdherenceLog weeklyAdherenceLog) {
        return adherencePercentageFor(weeklyAdherenceLog.getNumberOfDaysMissed());
    }

    protected WeeklyAdherenceLog getAdherenceLogForPreviousWeek(String patientId) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.findByPatientId(patientId);
        LocalDate startDateOfTreatmentAdvice = DateUtil.newDate(treatmentAdvice.getStartDate());
        LocalDate lastPossibleAdherenceLogDate = getStartDateForCurrentWeek(startDateOfTreatmentAdvice).minusWeeks(1).plusDays(DAYS_TO_RECALL);
        LocalDate yesterday = DateUtil.today().minusDays(1);

        List<WeeklyAdherenceLog> logs = allWeeklyAdherenceLogs.findByDateRange(patientId, treatmentAdvice.getId(), lastPossibleAdherenceLogDate, yesterday);
        return logs.size() == 0 ? null : logs.get(0);
    }
}
