package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.*;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.TAMAMessages;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.ivr.service.AdherenceServiceStrategy;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.domain.TimeOfDay;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
public class FourDayRecallAdherenceService implements AdherenceServiceStrategy {
    public static final int DAYS_TO_RECALL = 4;

    private AllTreatmentAdvices allTreatmentAdvices;
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    private AllPatients allPatients;
    private PatientAlertService patientAlertService;
    private Properties properties;

    @Autowired
    public FourDayRecallAdherenceService(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, AllWeeklyAdherenceLogs allWeeklyAdherenceLogs,
                                PatientAlertService patientAlertService, @Qualifier("fourDayRecallProperties") Properties properties, AdherenceService adherenceService) {
        this.allPatients = allPatients;
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.patientAlertService = patientAlertService;
        this.properties = properties;
        adherenceService.register(CallPreference.FourDayRecall, this);
    }

    public boolean isAdherenceCapturedForCurrentWeek(String patientDocId, String treatmentAdviceId) {
        return isAdherenceCapturedForAnyWeek(patientDocId, treatmentAdviceId, getStartDateForCurrentWeek(patientDocId));
    }

    public boolean isAdherenceCapturedForAnyWeek(String patientDocId, String treatmentAdviceId, LocalDate weekStartDate) {
        return allWeeklyAdherenceLogs.findLogsByWeekStartDate(patientDocId, treatmentAdviceId, weekStartDate).size() > 0;
    }

    public LocalDate getStartDateForCurrentWeek(String patientDocId) {
        return getStartDateForWeek(patientDocId, DateUtil.today());
    }

    public LocalDate getStartDateForWeek(String patientDocId, LocalDate week) {
        Patient patient = allPatients.get(patientDocId);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientDocId);

        DayOfWeek preferredDayOfWeek = patient.getPatientPreferences().getDayOfWeeklyCall();

        int retryDayCount = 0;
        boolean isRetry = week.getDayOfWeek() != preferredDayOfWeek.getValue();
        if (isRetry) retryDayCount = getRetryDaysCount(preferredDayOfWeek, week);

        DayOfWeek treatmentAdviceStartDay = DayOfWeek.getDayOfWeek(DateUtil.newDate(treatmentAdvice.getStartDate()));
        return dateWith(treatmentAdviceStartDay, DAYS_TO_RECALL, week.minusDays(retryDayCount));
    }

    public LocalDate getMostRecentBestCallDay(String patientDocId) {
        LocalDate today = DateUtil.today();
        Patient patient = allPatients.get(patientDocId);

        DayOfWeek preferredDayOfWeek = patient.getPatientPreferences().getDayOfWeeklyCall();

        if (today.getDayOfWeek() != preferredDayOfWeek.getValue()) {
            return today.minusDays(getRetryDaysCount(preferredDayOfWeek, today));
        } else return today;
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
        LocalDate startDayOfWeek = getStartDateForWeek(patientDocId, week);
        LocalDate iteratingDayOfWeek = startDayOfWeek;
        DayOfWeek preferredDayOfWeek = patient.getPatientPreferences().getDayOfWeeklyCall();
        while (true) {
            if (iteratingDayOfWeek.getDayOfWeek() == preferredDayOfWeek.getValue()) {
                if (isStartDayEqualToOrSufficientlyBehindFourDayRecallDate(startDayOfWeek, iteratingDayOfWeek)) {
                    return iteratingDayOfWeek;
                } else {
                    return iteratingDayOfWeek.plusWeeks(1);
                }
            }
            iteratingDayOfWeek = iteratingDayOfWeek.plusDays(1);
        }
    }

    public int getAdherencePercentageForPreviousWeek(String patientId) {
        return adherencePercentageFor(getAdherenceLog(patientId, 1));
    }

    public int adherencePercentageFor(int numDaysMissed) {
        return (DAYS_TO_RECALL - numDaysMissed) * 100 / DAYS_TO_RECALL;
    }

    protected int adherencePercentageFor(WeeklyAdherenceLog weeklyAdherenceLog) {
        if (weeklyAdherenceLog == null) return 0;
        return adherencePercentageFor(weeklyAdherenceLog.getNumberOfDaysMissed());
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
        return adherencePercentageFor(dosageMissedDays) < getAdherencePercentageForPreviousWeek(patientId);
    }

    public void raiseAdherenceFallingAlert(String patientId) {
        if (isCurrentWeekTheFirstWeekOfTreatmentAdvice(patientId)) return;

        int adherencePercentageForCurrentWeek = getAdherencePercentageForCurrentWeek(patientId);
        if (adherencePercentageForCurrentWeek >= getAdherencePercentageForPreviousWeek(patientId)) return;

        final Map<String, String> data = new HashMap<String, String>();
        final int previousWeekPercentage = getAdherencePercentageForPreviousWeek(patientId);
        final double fall = ((previousWeekPercentage - adherencePercentageForCurrentWeek) / (double) previousWeekPercentage) * 100.0;
        final String description = String.format(TAMAMessages.ADHERENCE_FALLING_FROM_TO, fall, (double) previousWeekPercentage, (double) adherencePercentageForCurrentWeek);
        patientAlertService.createAlert(patientId, TAMAConstants.NO_ALERT_PRIORITY, TAMAConstants.FALLING_ADHERENCE, description, PatientAlertType.FallingAdherence, data);
    }

    public LocalDate getWeeklyAdherenceTrackingStartDate(Patient patient, TreatmentAdvice treatmentAdvice) {
        LocalDate weeklyAdherenceTrackingStartDate = DateUtil.newDate(treatmentAdvice.getStartDate());
        if (patient.getPatientPreferences().getCallPreferenceTransitionDate() != null && weeklyAdherenceTrackingStartDate.isBefore(patient.getPatientPreferences().getCallPreferenceTransitionDate().toLocalDate()))
            weeklyAdherenceTrackingStartDate = patient.getPatientPreferences().getCallPreferenceTransitionDate().toLocalDate();
        return weeklyAdherenceTrackingStartDate;
    }

    boolean isCurrentWeekTheFirstWeekOfTreatmentAdvice(String patientId) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        Patient patient = allPatients.get(patientId);
        LocalDate weeklyAdherenceTrackingStartDate = getWeeklyAdherenceTrackingStartDate(patient, treatmentAdvice);
        LocalDate calculatedStartDateForCurrentWeek = getStartDateForCurrentWeek(patientId);
        return weeklyAdherenceTrackingStartDate.isEqual(calculatedStartDateForCurrentWeek) || calculatedStartDateForCurrentWeek.isBefore(weeklyAdherenceTrackingStartDate);
    }

    protected int getAdherencePercentageForCurrentWeek(String patientId) {
        return adherencePercentageFor(getAdherenceLog(patientId, 0));
    }

    public boolean hasAdherenceFallingAlertBeenRaisedForCurrentWeek(String patientDocId) {
        DateTime startDateForCurrentWeek = DateUtil.newDateTime(getMostRecentBestCallDay(patientDocId), 0, 0, 0);
        return patientAlertService.getFallingAdherenceAlerts(patientDocId, startDateForCurrentWeek, DateUtil.now()).size() > 0;
    }

    public LocalDate findFirstFourDayRecallDateForTreatmentAdvice(String patientId, LocalDate treatmentAdviceStartDate) {
        LocalDate fourDayRecallDate = findFourDayRecallDateForAnyWeek(patientId, treatmentAdviceStartDate);
        while (true) {
            if (fourDayRecallDate.isBefore(treatmentAdviceStartDate)) {
                fourDayRecallDate = fourDayRecallDate.plusWeeks(1);
            } else {
                if (isStartDayEqualToOrSufficientlyBehindFourDayRecallDate(treatmentAdviceStartDate, fourDayRecallDate)) {
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

    public void raiseAdherenceInRedAlert(String patientId) {
        WeeklyAdherenceLog adherenceLog = getAdherenceLog(patientId, 0);
        String description = PatientAlertService.RED_ALERT_MESSAGE_NO_RESPONSE;
        double adherencePercentage = adherencePercentageFor(adherenceLog);

        if (adherenceLog != null) {
            double acceptableAdherencePercentage = Double.parseDouble(properties.getProperty(TAMAConstants.ACCEPTABLE_ADHERENCE_PERCENTAGE));
            if (adherencePercentage >= acceptableAdherencePercentage) return;
            description = String.format(TAMAMessages.ADHERENCE_PERCENTAGE_IS, adherencePercentage);
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.ADHERENCE, Double.toString(adherencePercentage));
        patientAlertService.createAlert(patientId, TAMAConstants.NO_ALERT_PRIORITY, TAMAConstants.ADHERENCE_IN_RED_ALERT, description, PatientAlertType.AdherenceInRed, data);
    }

    public boolean hasAdherenceInRedAlertBeenRaisedForCurrentWeek(String patientId) {
        DateTime startDateForCurrentWeek = DateUtil.newDateTime(getMostRecentBestCallDay(patientId), 0, 0, 0);
        return patientAlertService.getAdherenceInRedAlerts(patientId, startDateForCurrentWeek, DateUtil.now()).size() > 0;
    }

    public DateTime getFirstWeeksFourDayRecallRetryEndDate(Patient patient) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());
        LocalDate weeklyAdherenceTrakingStratDate = getWeeklyAdherenceTrackingStartDate(patient, treatmentAdvice);
        LocalDate firstRecallDate = weeklyAdherenceTrakingStratDate.plusDays(DAYS_TO_RECALL);
        DayOfWeek preferredDayOfWeeklyCall = patient.getPatientPreferences().getDayOfWeeklyCall();
        while (preferredDayOfWeeklyCall.getValue() != firstRecallDate.getDayOfWeek()) {
            firstRecallDate = firstRecallDate.plusDays(1);
        }
        Integer daysToRetry = Integer.valueOf(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));
        TimeOfDay bestCallTime = patient.getPatientPreferences().getBestCallTime();
        return firstRecallDate.plusDays(daysToRetry).toDateTime(new LocalTime(bestCallTime.getHour(), bestCallTime.getMinute()));
    }


    @Override
    public boolean wasAnyDoseMissedLastWeek(Patient patient) {
        double adherenceForLastWeek = getAdherencePercentageForPreviousWeek(patient.getId());
        if (adherenceForLastWeek == 0.0 && getFirstWeeksFourDayRecallRetryEndDate(patient).isAfter(DateUtil.now()))
            return false;
        return (adherenceForLastWeek != 100.0);
    }

    @Override
    public boolean wasAnyDoseTakenLateSince(Patient patient, LocalDate since) {
        return false;
    }
}
