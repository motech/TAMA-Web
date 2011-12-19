package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.ivr.service.AdherenceServiceStrategy;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class FourDayRecallAdherenceService implements AdherenceServiceStrategy {

    private AllTreatmentAdvices allTreatmentAdvices;
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    private AllPatients allPatients;
    private FourDayRecallService fourDayRecallService;
    private Properties properties;

    @Autowired
    public FourDayRecallAdherenceService(AllWeeklyAdherenceLogs allWeeklyAdherenceLogs, AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, FourDayRecallService fourDayRecallService, @Qualifier("fourDayRecallProperties") Properties properties, AdherenceService adherenceService) {
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.fourDayRecallService = fourDayRecallService;
        this.properties = properties;
        adherenceService.register(CallPreference.FourDayRecall, this);
    }

    public void recordAdherence(String patientId, boolean doseTaken) {
        Patient suspendedPatient = allPatients.get(patientId);
        DateTime fromDate = suspendedPatient.getLastSuspendedDate();
        DateTime toDate = DateUtil.now();
        DateTime iteratingDate = fromDate;
        String treatmentAdviceDocId = allTreatmentAdvices.currentTreatmentAdvice(patientId).getId();
        LocalDate patientLastRecallDate = fourDayRecallService.findFourDayRecallDateForAnyWeek(patientId, iteratingDate.toLocalDate());
        boolean isFirstDayOfSuspensionPeriod;
        boolean isLastDayOfSuspensionPeriod;

        LocalDate startDateForAnyWeek = fourDayRecallService.getStartDateForAnyWeek(patientId, iteratingDate.toLocalDate());
        if (suspensionStartDateFallsInRetryIntervalAndAdherenceHasNotBeenRecordedForTheWeek(patientId, iteratingDate, startDateForAnyWeek, treatmentAdviceDocId, patientLastRecallDate)) {
            createWeeklyAdherenceLogForTheWeek(patientId, doseTaken, startDateForAnyWeek, treatmentAdviceDocId);
        }
        while (iteratingDateIsOnOrBeforeReactivationDate(toDate, iteratingDate)) {
            startDateForAnyWeek = fourDayRecallService.getStartDateForAnyWeek(patientId, iteratingDate.toLocalDate());
            if (itIsTheDayOfTheWeekForPatientsFourDayRecall(iteratingDate, suspendedPatient) && isStartDateOfWeekAfterTreatmentAdviceStartDate(patientId, startDateForAnyWeek)) {
                isFirstDayOfSuspensionPeriod = isFirstDayOfSuspensionPeriod(fromDate, iteratingDate);
                isLastDayOfSuspensionPeriod = isLastDayOfSuspensionPeriod(toDate, iteratingDate);
                if ((isFirstDayOfSuspensionPeriod || isLastDayOfSuspensionPeriod)
                        && suspendedPatient.getPatientPreferences().hasAgreedToBeCalledAtBestCallTime()) {
                    if (patientsBestCallTimeFallsWithinSuspensionPeriod(fromDate, toDate, suspendedPatient, isFirstDayOfSuspensionPeriod, isLastDayOfSuspensionPeriod)) {
                        createWeeklyAdherenceLogForTheWeek(patientId, doseTaken, startDateForAnyWeek, treatmentAdviceDocId);
                    }
                    iteratingDate = iteratingDate.plusDays(1);
                    continue;
                }
                createWeeklyAdherenceLogForTheWeek(patientId, doseTaken, startDateForAnyWeek, treatmentAdviceDocId);
            }
            iteratingDate = iteratingDate.plusDays(1);
        }
    }

    private boolean isStartDateOfWeekAfterTreatmentAdviceStartDate(String patientId, LocalDate startDateForAnyWeek) {
        LocalDate localDate = DateUtil.newDate(allTreatmentAdvices.currentTreatmentAdvice(patientId).getStartDate());
        return startDateForAnyWeek.isAfter(localDate) || startDateForAnyWeek.isEqual(localDate);
    }

    private boolean suspensionStartDateFallsInRetryIntervalAndAdherenceHasNotBeenRecordedForTheWeek(String patientId, DateTime iteratingDate, LocalDate startDateForAnyWeek, String treatmentAdviceDocId, LocalDate patientLastRecallDate) {
        return isIteratingDateInRetryIntervalOfPatientLastRecallDate(iteratingDate, patientLastRecallDate) && !fourDayRecallService.isAdherenceCapturedForAnyWeek(patientId, treatmentAdviceDocId, startDateForAnyWeek);
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

    private void createWeeklyAdherenceLogForTheWeek(String patientId, boolean doseTaken, LocalDate startDateForAnyWeek, String treatmentAdviceDocId) {
        int numberOfDaysMissed = doseTaken ? 0 : FourDayRecallService.DAYS_TO_RECALL;
        WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog(patientId,
                treatmentAdviceDocId,
                startDateForAnyWeek,
                DateUtil.today(), numberOfDaysMissed);
        allWeeklyAdherenceLogs.add(weeklyAdherenceLog);
    }

    @Override
    public boolean wasAnyDoseMissedLastWeek(Patient patient) {
        double adherenceForLastWeek = fourDayRecallService.getAdherencePercentageForPreviousWeek(patient.getId());
        if (adherenceForLastWeek == 0.0 && fourDayRecallService.getFirstWeeksFourDayRecallRetryEndDate(patient).isAfter(DateUtil.now()))
            return false;
        return (adherenceForLastWeek != 100.0);
    }

    @Override
    public boolean wasAnyDoseTakenLateSince(Patient patient, LocalDate since) {
        return false;
    }
}