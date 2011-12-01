package org.motechproject.tamacallflow.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamadomain.domain.WeeklyAdherenceLog;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamadomain.domain.SuspendedAdherenceData;
import org.motechproject.tamacallflow.platform.service.FourDayRecallService;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.tamadomain.repository.AllTreatmentAdvices;
import org.motechproject.tamadomain.repository.AllWeeklyAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class WeeklyAdherenceService {

    private AllTreatmentAdvices allTreatmentAdvices;
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    private AllPatients allPatients;
    private FourDayRecallService fourDayRecallService;
    private Properties properties;

    @Autowired
    public WeeklyAdherenceService(AllWeeklyAdherenceLogs allWeeklyAdherenceLogs, AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, FourDayRecallService fourDayRecallService, @Qualifier("ivrProperties") Properties properties) {
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.fourDayRecallService = fourDayRecallService;
        this.properties = properties;
    }

    public void recordAdherence(SuspendedAdherenceData suspendedAdherenceData) {
        DateTime fromDate = suspendedAdherenceData.suspendedFrom();
        DateTime toDate = DateUtil.now();
        DateTime iteratingDate = fromDate;
        String patientId = suspendedAdherenceData.patientId();
        Patient suspendedPatient = allPatients.get(patientId);
        String treatmentAdviceDocId = allTreatmentAdvices.currentTreatmentAdvice(patientId).getId();
        LocalDate patientLastRecallDate = fourDayRecallService.findFourDayRecallDateForAnyWeek(patientId, iteratingDate.toLocalDate());
        boolean isFirstDayOfSuspensionPeriod;
        boolean isLastDayOfSuspensionPeriod;

        LocalDate startDateForAnyWeek = fourDayRecallService.getStartDateForAnyWeek(patientId, iteratingDate.toLocalDate());
        if(suspensionStartDateFallsInRetryIntervalAndAdherenceHasNotBeenRecordedForTheWeek(suspendedAdherenceData, iteratingDate, startDateForAnyWeek, treatmentAdviceDocId, patientLastRecallDate)){
            createWeeklyAdherenceLogForTheWeek(suspendedAdherenceData, startDateForAnyWeek, treatmentAdviceDocId);
        }
        while (iteratingDateIsOnOrBeforeReactivationDate(toDate, iteratingDate)) {
            startDateForAnyWeek = fourDayRecallService.getStartDateForAnyWeek(patientId, iteratingDate.toLocalDate());
            if (itIsTheDayOfTheWeekForPatientsFourDayRecall(iteratingDate, suspendedPatient) && isStartDateOfWeekAfterTreatmentAdviceStartDate(patientId, startDateForAnyWeek)) {
                isFirstDayOfSuspensionPeriod = isFirstDayOfSuspensionPeriod(fromDate, iteratingDate);
                isLastDayOfSuspensionPeriod = isLastDayOfSuspensionPeriod(toDate, iteratingDate);
                if ((isFirstDayOfSuspensionPeriod || isLastDayOfSuspensionPeriod)
                     && suspendedPatient.getPatientPreferences().hasAgreedToBeCalledAtBestCallTime()) {
                    if (patientsBestCallTimeFallsWithinSuspensionPeriod(fromDate, toDate, suspendedPatient, isFirstDayOfSuspensionPeriod, isLastDayOfSuspensionPeriod)) {
                        createWeeklyAdherenceLogForTheWeek(suspendedAdherenceData, startDateForAnyWeek, treatmentAdviceDocId);
                    }
                    iteratingDate = iteratingDate.plusDays(1);
                    continue;
                }
                createWeeklyAdherenceLogForTheWeek(suspendedAdherenceData, startDateForAnyWeek, treatmentAdviceDocId);
            }
            iteratingDate = iteratingDate.plusDays(1);
        }
    }

    private boolean isStartDateOfWeekAfterTreatmentAdviceStartDate(String patientId, LocalDate startDateForAnyWeek) {
        LocalDate localDate = DateUtil.newDate(allTreatmentAdvices.currentTreatmentAdvice(patientId).getStartDate());
        return startDateForAnyWeek.isAfter(localDate) || startDateForAnyWeek.isEqual(localDate);
    }

    private boolean suspensionStartDateFallsInRetryIntervalAndAdherenceHasNotBeenRecordedForTheWeek(SuspendedAdherenceData suspendedAdherenceData, DateTime iteratingDate, LocalDate startDateForAnyWeek, String treatmentAdviceDocId, LocalDate patientLastRecallDate) {
        return isIteratingDateInRetryIntervalOfPatientLastRecallDate(iteratingDate, patientLastRecallDate) && !fourDayRecallService.isAdherenceCapturedForAnyWeek(suspendedAdherenceData.patientId(),
                                                                     treatmentAdviceDocId, startDateForAnyWeek);
    }

    private boolean isIteratingDateInRetryIntervalOfPatientLastRecallDate(DateTime iteratingDate, LocalDate patientLastRecallDate) {
        Integer daysToRetry = Integer.valueOf(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));
        for(int i = 1; i <= daysToRetry; i++){
            if((patientLastRecallDate.plusDays(i).compareTo(iteratingDate.toLocalDate()) == 0)){
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
        return ((suspendedPatient.getPatientPreferences().getBestCallTime().toTime().getDateTime(toDate).compareTo(toDate) <= 0 && isLastDayOfSuspensionPeriod)
                || (suspendedPatient.getPatientPreferences().getBestCallTime().toTime().getDateTime(fromDate).compareTo(fromDate) >= 0 && isFirstDayOfSuspensionPeriod));
    }

    private boolean isLastDayOfSuspensionPeriod(DateTime toDate, DateTime iteratingDate) {
        return (iteratingDate.toLocalDate().compareTo(toDate.toLocalDate()) == 0);
    }

    private boolean isFirstDayOfSuspensionPeriod(DateTime fromDate, DateTime iteratingDate) {
        return (iteratingDate.toLocalDate().compareTo(fromDate.toLocalDate()) == 0);
    }

    private void createWeeklyAdherenceLogForTheWeek(SuspendedAdherenceData suspendedAdherenceData, LocalDate startDateForAnyWeek, String treatmentAdviceDocId) {
        WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog(suspendedAdherenceData.patientId(),
                treatmentAdviceDocId,
                startDateForAnyWeek,
                DateUtil.today(), suspendedAdherenceData.getAdherenceDataWhenPatientWasSuspended().numberOfDaysMissed());
        allWeeklyAdherenceLogs.add(weeklyAdherenceLog);
    }
}