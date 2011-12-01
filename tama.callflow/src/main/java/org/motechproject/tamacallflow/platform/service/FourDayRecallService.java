package org.motechproject.tamacallflow.platform.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamadomain.domain.PatientAlertType;
import org.motechproject.tamadomain.domain.WeeklyAdherenceLog;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamadomain.domain.TreatmentAdvice;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.tamadomain.repository.AllTreatmentAdvices;
import org.motechproject.tamadomain.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tamacallflow.service.PatientAlertService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FourDayRecallService {

    public static final int DAYS_TO_RECALL = 4;

    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllPatients allPatients;
    private PatientAlertService patientAlertService;

    @Autowired
    public FourDayRecallService(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, AllWeeklyAdherenceLogs allWeeklyAdherenceLogs, PatientAlertService patientAlertService) {
        this.allPatients = allPatients;
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.patientAlertService = patientAlertService;
    }

    public boolean isAdherenceCapturedForCurrentWeek(String patientDocId, String treatmentAdviceId) {
        return isAdherenceCapturedForAnyWeek(patientDocId, treatmentAdviceId, getStartDateForCurrentWeek(patientDocId));
    }

    public boolean isAdherenceCapturedForAnyWeek(String patientDocId, String treatmentAdviceId, LocalDate weekStartDate) {
        return 1 == allWeeklyAdherenceLogs.findLogsByWeekStartDate(patientDocId, treatmentAdviceId, weekStartDate).size();
    }

    public LocalDate getStartDateForCurrentWeek(String patientDocId) {
        return getStartDateForAnyWeek(patientDocId, DateUtil.today());
    }

    public LocalDate getStartDateForAnyWeek(String patientDocId, LocalDate week) {
        Patient patient = allPatients.get(patientDocId);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientDocId);

        DayOfWeek preferredDayOfWeek = patient.getPatientPreferences().getDayOfWeeklyCall();

        int retryDayCount = 0;
        boolean isRetry = week.getDayOfWeek() != preferredDayOfWeek.getValue();
        if (isRetry) retryDayCount = getRetryDaysCount(preferredDayOfWeek, week);

        DayOfWeek treatmentAdviceStartDay = DayOfWeek.getDayOfWeek(DateUtil.newDate(treatmentAdvice.getStartDate()));
        return dateWith(treatmentAdviceStartDay, DAYS_TO_RECALL, week.minusDays(retryDayCount));
    }

    private int getRetryDaysCount(DayOfWeek preferredDayOfWeek, LocalDate date) {
        int count = 0;
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

    public LocalDate findFourDayRecallDateForAnyWeek(String patientDocId, LocalDate week) {
        Patient patient = allPatients.get(patientDocId);
        LocalDate startDayOfWeek = getStartDateForAnyWeek(patientDocId, week);
        LocalDate iteratingDayOfWeek = startDayOfWeek;
        DayOfWeek preferredDayOfWeek = patient.getPatientPreferences().getDayOfWeeklyCall();
        while (true){
            if(iteratingDayOfWeek.getDayOfWeek() == preferredDayOfWeek.getValue()){
                if(isStartDayEqualToOrSufficientlyBehindFourDayRecallDate(startDayOfWeek, iteratingDayOfWeek)){
                    return iteratingDayOfWeek;
                } else{
                    return iteratingDayOfWeek.plusWeeks(1);
                }
            }
            iteratingDayOfWeek = iteratingDayOfWeek.plusDays(1);
        }
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
        final int weeksBefore = 1;
        return getAdherenceLog(patientId, weeksBefore);
    }

    protected WeeklyAdherenceLog getAdherenceLog(String patientId, int weeksBefore) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        LocalDate startDateForPreviousWeek = getStartDateForCurrentWeek(patientId).minusWeeks(weeksBefore);

        List<WeeklyAdherenceLog> logs = allWeeklyAdherenceLogs.findLogsByWeekStartDate(patientId, treatmentAdvice.getId(), startDateForPreviousWeek);
        return logs.size() == 0 ? null : logs.get(0);
    }

    public boolean isAdherenceBeingCapturedForFirstWeek(String patientId) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        Patient patient = allPatients.get(patientId);
        LocalDate treatmentAdviceStartDate = DateUtil.newDate(treatmentAdvice.getStartDate());
        DateTime callPreferenceTransitionDate = patient.getPatientPreferences().getCallPreferenceTransitionDate();

        if (callPreferenceTransitionDate != null && callPreferenceTransitionDate.toLocalDate().isAfter(treatmentAdviceStartDate)) {
            return dateIsAtLeastOneWeekAgo(callPreferenceTransitionDate);
        }

        return getStartDateForCurrentWeek(patientId).equals(treatmentAdviceStartDate);
    }

    private boolean dateIsAtLeastOneWeekAgo(DateTime date) {
        return DateUtil.today().minusWeeks(1).isBefore(date.toLocalDate());
    }

    public boolean isAdherenceFalling(int dosageMissedDays, String patientId) {
        return adherencePercentageFor(dosageMissedDays) < adherencePercentageForPreviousWeek(patientId);
    }

    public void raiseAdherenceFallingAlert(String patientId) {
        final int weeksBefore = 0;
        int dosageMissedDays = getAdherenceLog(patientId, weeksBefore).getNumberOfDaysMissed();
        if (!isAdherenceFalling(dosageMissedDays, patientId)) return;

        final Map<String, String> data = new HashMap<String, String>();
        final int previousWeekPercentage = adherencePercentageForPreviousWeek(patientId);
        final int thisWeekPercentage = adherencePercentageFor(dosageMissedDays);
        final double fall = ((previousWeekPercentage - thisWeekPercentage) / previousWeekPercentage) * 100.0;
        final String description = String.format("Adherence fell by %2.2f%% from %d%% to %d%%", fall, previousWeekPercentage, thisWeekPercentage);
        patientAlertService.createAlert(patientId, TAMAConstants.NO_ALERT_PRIORITY, description, "Falling Adherence", PatientAlertType.FallingAdherence, data);
    }

    public boolean hasAdherenceFallingAlertBeenRaisedForCurrentWeek(String patientDocId) {
        DateTime startDateForCurrentWeek = DateUtil.newDateTime(getStartDateForCurrentWeek(patientDocId), 0, 0, 0);
        return patientAlertService.getFallingAdherenceAlerts(patientDocId, startDateForCurrentWeek, DateUtil.now()).size() > 0;
    }

    public LocalDate findFirstFourDayRecallDateForTreatmentAdvice(String patientId, LocalDate treatmentAdviceStartDate) {
        LocalDate fourDayRecallDate = findFourDayRecallDateForAnyWeek(patientId, treatmentAdviceStartDate);
        while (true){
            if(fourDayRecallDate.isBefore(treatmentAdviceStartDate)){
                fourDayRecallDate = fourDayRecallDate.plusWeeks(1);
            } else {
                if (isStartDayEqualToOrSufficientlyBehindFourDayRecallDate(treatmentAdviceStartDate, fourDayRecallDate)){
                    return fourDayRecallDate;
                } else {
                    return fourDayRecallDate.plusWeeks(1);
                }
            }
        }
    }

    boolean isStartDayEqualToOrSufficientlyBehindFourDayRecallDate(LocalDate treatmentAdviceStartDate, LocalDate fourDayRecallDate) {
        return treatmentAdviceStartDate.plusDays(DAYS_TO_RECALL).isBefore(fourDayRecallDate) || treatmentAdviceStartDate.plusDays(DAYS_TO_RECALL).isEqual(fourDayRecallDate);
    }
}
