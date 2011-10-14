package org.motechproject.tama.service;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.domain.WeeklyAdherenceLog;
import org.motechproject.tama.repository.AllPatients;
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
    private AllPatients allPatients;

    @Autowired
    public FourDayRecallService(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, AllWeeklyAdherenceLogs allWeeklyAdherenceLogs) {
        this.allPatients = allPatients;
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.allTreatmentAdvices = allTreatmentAdvices;
    }

    public boolean isAdherenceCapturedForCurrentWeek(String patientDocId, String treatmentAdviceId) {
        LocalDate startDateForCurrentWeek = getStartDateForCurrentWeek(patientDocId);
        return allWeeklyAdherenceLogs.logExistsFor(patientDocId, treatmentAdviceId, startDateForCurrentWeek);
    }

    public LocalDate getStartDateForCurrentWeek(String patientDocId) {
        Patient patient = allPatients.get(patientDocId);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.findByPatientId(patientDocId);

        DayOfWeek preferredDayOfWeek = patient.getPatientPreferences().getDayOfWeeklyCall();

        int retryDayCount = 0;
        boolean isRetry = DateUtil.today().getDayOfWeek() != preferredDayOfWeek.getValue();
        if (isRetry) retryDayCount = getRetryDaysCount(preferredDayOfWeek);

        DayOfWeek treatmentAdviceStartDay = DayOfWeek.getDayOfWeek(DateUtil.newDate(treatmentAdvice.getStartDate()));
        return dateWith(treatmentAdviceStartDay, DAYS_TO_RECALL, DateUtil.today().minusDays(retryDayCount));
    }

    private int getRetryDaysCount(DayOfWeek preferredDayOfWeek) {
        int count = 0;
        LocalDate date = DateUtil.today();
        while (date.getDayOfWeek() != preferredDayOfWeek.getValue()) {
            date = date.minusDays(1);
            count++;
        }
        return count;
    }

    private LocalDate dateWith(DayOfWeek dayOfWeek, int minNumberOfDaysAgo, LocalDate maxDate) {
        LocalDate date = dateWith(dayOfWeek, maxDate);

        Period period = new Period(date, maxDate, PeriodType.days());
        if (period.getDays() >= minNumberOfDaysAgo) return date;

        return dateWith(dayOfWeek, date);
    }

    private LocalDate dateWith(DayOfWeek dayOfWeek, LocalDate maxDate) {
        LocalDate returnDate = maxDate.withDayOfWeek(dayOfWeek.getValue());
        boolean dateAfterMaxDate = returnDate.compareTo(maxDate) >= 0;
        if (dateAfterMaxDate) {
            returnDate = returnDate.minusWeeks(1);
        }
        return returnDate;
    }

    public int adherencePercentageForPreviousWeek(String patientId) {
        int previousWeekAdherencePercentage = 0;
        WeeklyAdherenceLog logForPreviousWeek = getAdherenceLogForPreviousWeek(patientId);
        if (logForPreviousWeek != null) {
            previousWeekAdherencePercentage = adherencePercentageFor(logForPreviousWeek);
        }
        return previousWeekAdherencePercentage;
    }

    public int adherencePercentageFor(int numDaysMissed) {
        return (DAYS_TO_RECALL - numDaysMissed) * 100 / DAYS_TO_RECALL;
    }

    protected int adherencePercentageFor(WeeklyAdherenceLog weeklyAdherenceLog) {
        return adherencePercentageFor(weeklyAdherenceLog.getNumberOfDaysMissed());
    }

    protected WeeklyAdherenceLog getAdherenceLogForPreviousWeek(String patientId) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.findByPatientId(patientId);
        LocalDate lastPossibleAdherenceLogDate = getStartDateForCurrentWeek(patientId).minusWeeks(1).plusDays(DAYS_TO_RECALL);
        LocalDate yesterday = DateUtil.today().minusDays(1);

        List<WeeklyAdherenceLog> logs = allWeeklyAdherenceLogs.findByDateRange(patientId, treatmentAdvice.getId(), lastPossibleAdherenceLogDate, yesterday);
        return logs.size() == 0 ? null : logs.get(0);
    }

    public boolean isAdherenceBeingCapturedForFirstWeek(String patientId) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.findByPatientId(patientId);
        LocalDate treatmentAdviceStartDate = DateUtil.newDate(treatmentAdvice.getStartDate());
        return getStartDateForCurrentWeek(patientId).equals(treatmentAdviceStartDate);
    }
}
