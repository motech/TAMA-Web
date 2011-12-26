package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class ResumeFourDayRecallService {

    public static final int DAYS_TO_RECALL = 4;

    private AllTreatmentAdvices allTreatmentAdvices;
    private FourDayRecallAdherenceService fourDayRecallAdherenceService;
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    private AllPatients allPatients;
    private Properties properties;

    @Autowired
    public ResumeFourDayRecallService(AllWeeklyAdherenceLogs allWeeklyAdherenceLogs, AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, @Qualifier("fourDayRecallProperties") Properties properties, FourDayRecallAdherenceService fourDayRecallAdherenceService) {
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.properties = properties;
        this.fourDayRecallAdherenceService = fourDayRecallAdherenceService;
    }

    public void backfillAdherenceForPeriodOfSuspension(String patientId, boolean doseTaken) {
        Patient suspendedPatient = allPatients.get(patientId);
        DateTime fromDate = suspendedPatient.getLastSuspendedDate();
        DateTime toDate = DateUtil.now();
        DateTime iteratingDate = fromDate;
        String treatmentAdviceDocId = allTreatmentAdvices.currentTreatmentAdvice(patientId).getId();
        LocalDate patientLastRecallDate = fourDayRecallAdherenceService.findFourDayRecallDateForAnyWeek(patientId, iteratingDate.toLocalDate());
        boolean isFirstDayOfSuspensionPeriod;
        boolean isLastDayOfSuspensionPeriod;

        LocalDate startDateForWeek = fourDayRecallAdherenceService.getStartDateForWeek(patientId, iteratingDate.toLocalDate());
        if (suspensionStartDateFallsInRetryIntervalAndAdherenceHasNotBeenRecordedForTheWeek(patientId, iteratingDate, startDateForWeek, treatmentAdviceDocId, patientLastRecallDate)) {
            allWeeklyAdherenceLogs.add(WeeklyAdherenceLog.create(patientId, treatmentAdviceDocId, startDateForWeek, doseTaken ? 0 : WeeklyAdherenceLog.DAYS_TO_RECALL));
        }
        while (iteratingDateIsOnOrBeforeReactivationDate(toDate, iteratingDate)) {
            startDateForWeek = fourDayRecallAdherenceService.getStartDateForWeek(patientId, iteratingDate.toLocalDate());
            if (itIsTheDayOfTheWeekForPatientsFourDayRecall(iteratingDate, suspendedPatient) && isStartDateOfWeekAfterTreatmentAdviceStartDate(patientId, startDateForWeek)) {
                isFirstDayOfSuspensionPeriod = isFirstDayOfSuspensionPeriod(fromDate, iteratingDate);
                isLastDayOfSuspensionPeriod = isLastDayOfSuspensionPeriod(toDate, iteratingDate);
                if (isFirstDayOfSuspensionPeriod || isLastDayOfSuspensionPeriod) {
                    if (patientsBestCallTimeFallsWithinSuspensionPeriod(fromDate, toDate, suspendedPatient, isFirstDayOfSuspensionPeriod, isLastDayOfSuspensionPeriod)) {
                        allWeeklyAdherenceLogs.add(WeeklyAdherenceLog.create(patientId, treatmentAdviceDocId, startDateForWeek, doseTaken ? 0 : WeeklyAdherenceLog.DAYS_TO_RECALL));
                    }
                    iteratingDate = iteratingDate.plusDays(1);
                    continue;
                }
                allWeeklyAdherenceLogs.add(WeeklyAdherenceLog.create(patientId, treatmentAdviceDocId, startDateForWeek, doseTaken ? 0 : WeeklyAdherenceLog.DAYS_TO_RECALL));
            }
            iteratingDate = iteratingDate.plusDays(1);
        }
    }

    private boolean isStartDateOfWeekAfterTreatmentAdviceStartDate(String patientId, LocalDate startDateForAnyWeek) {
        LocalDate localDate = DateUtil.newDate(allTreatmentAdvices.currentTreatmentAdvice(patientId).getStartDate());
        return startDateForAnyWeek.isAfter(localDate) || startDateForAnyWeek.isEqual(localDate);
    }

    private boolean suspensionStartDateFallsInRetryIntervalAndAdherenceHasNotBeenRecordedForTheWeek(String patientId, DateTime iteratingDate, LocalDate startDateForAnyWeek, String treatmentAdviceDocId, LocalDate patientLastRecallDate) {
        return isIteratingDateInRetryIntervalOfPatientLastRecallDate(iteratingDate, patientLastRecallDate) && !fourDayRecallAdherenceService.isAdherenceCapturedForAnyWeek(patientId, treatmentAdviceDocId, startDateForAnyWeek);
    }

    private boolean isIteratingDateInRetryIntervalOfPatientLastRecallDate(DateTime iteratingDate, LocalDate patientLastRecallDate) {
        Integer daysToRetry = Integer.valueOf(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));
        for (int i = 1; i <= daysToRetry; i++) {
            if ((patientLastRecallDate.plusDays(i).compareTo(iteratingDate.toLocalDate()) == 0)) {
                return true;
            }
        }
        return false;
    }

    private boolean itIsTheDayOfTheWeekForPatientsFourDayRecall(DateTime iteratingDate, Patient suspendedPatient) {
        return iteratingDate.toLocalDate().getDayOfWeek() == suspendedPatient.getPatientPreferences().getDayOfWeeklyCall().getValue();
    }

    private boolean iteratingDateIsOnOrBeforeReactivationDate(DateTime toDate, DateTime iteratingDate) {
        return iteratingDate.isBefore(toDate) || isLastDayOfSuspensionPeriod(toDate, iteratingDate);
    }

    private boolean patientsBestCallTimeFallsWithinSuspensionPeriod(DateTime fromDate, DateTime toDate, Patient suspendedPatient, boolean isFirstDayOfSuspensionPeriod, boolean isLastDayOfSuspensionPeriod) {
        return ((patientsBestCallTimeBeforeReactivationTime(toDate, suspendedPatient) && isLastDayOfSuspensionPeriod && !isFirstDayOfSuspensionPeriod)
                || (patientsBestCallTimeAfterSuspensionTime(fromDate, suspendedPatient) && isFirstDayOfSuspensionPeriod && !isLastDayOfSuspensionPeriod)
                || (patientsBestCallTimeBeforeReactivationTime(toDate, suspendedPatient) && isLastDayOfSuspensionPeriod
                && patientsBestCallTimeAfterSuspensionTime(fromDate, suspendedPatient) && isFirstDayOfSuspensionPeriod));
    }

    private boolean patientsBestCallTimeBeforeReactivationTime(DateTime toDate, Patient suspendedPatient) {
        return suspendedPatient.getPatientPreferences().getBestCallTime().toTime().getDateTime(toDate).compareTo(toDate) <= 0;
    }

    private boolean patientsBestCallTimeAfterSuspensionTime(DateTime fromDate, Patient suspendedPatient) {
        return suspendedPatient.getPatientPreferences().getBestCallTime().toTime().getDateTime(fromDate).compareTo(fromDate) >= 0;
    }

    private boolean isLastDayOfSuspensionPeriod(DateTime toDate, DateTime iteratingDate) {
        return (iteratingDate.toLocalDate().compareTo(toDate.toLocalDate()) == 0);
    }

    private boolean isFirstDayOfSuspensionPeriod(DateTime fromDate, DateTime iteratingDate) {
        return (iteratingDate.toLocalDate().compareTo(fromDate.toLocalDate()) == 0);
    }
}