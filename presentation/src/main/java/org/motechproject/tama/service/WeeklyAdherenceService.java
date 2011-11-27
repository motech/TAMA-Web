package org.motechproject.tama.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.WeeklyAdherenceLog;
import org.motechproject.tama.platform.service.FourDayRecallService;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.tama.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.web.view.SuspendedAdherenceData;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeeklyAdherenceService {

    private AllTreatmentAdvices allTreatmentAdvices;
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    private AllPatients allPatients;
    private FourDayRecallService fourDayRecallService;

    @Autowired
    public WeeklyAdherenceService(AllWeeklyAdherenceLogs allWeeklyAdherenceLogs, AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, FourDayRecallService fourDayRecallService) {
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.fourDayRecallService = fourDayRecallService;
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
        
        if(suspensionStartDateFallsInRetryIntervalAndAdherenceHasNotBeenRecordedForTheWeek(suspendedAdherenceData, iteratingDate, treatmentAdviceDocId, patientLastRecallDate)){
            createWeeklyAdherenceLogForTheWeek(suspendedAdherenceData, iteratingDate, treatmentAdviceDocId);
        }
        while (iteratingDateIsOnOrBeforeReactivationDate(toDate, iteratingDate)) {
            if (itIsTheDayOfTheWeekForPatientsFourDayRecall(iteratingDate, suspendedPatient)) {
                isFirstDayOfSuspensionPeriod = isFirstDayOfSuspensionPeriod(fromDate, iteratingDate);
                isLastDayOfSuspensionPeriod = isLastDayOfSuspensionPeriod(toDate, iteratingDate);
                if ((isFirstDayOfSuspensionPeriod || isLastDayOfSuspensionPeriod)
                     && suspendedPatient.getPatientPreferences().hasAgreedToBeCalledAtBestCallTime()) {
                    if (patientsBestCallTimeFallsWithinSuspensionPeriod(fromDate, toDate, suspendedPatient, isFirstDayOfSuspensionPeriod, isLastDayOfSuspensionPeriod)) {
                        createWeeklyAdherenceLogForTheWeek(suspendedAdherenceData, iteratingDate, treatmentAdviceDocId);
                    }
                    iteratingDate = iteratingDate.plusDays(1);
                    continue;
                }
                createWeeklyAdherenceLogForTheWeek(suspendedAdherenceData, iteratingDate, treatmentAdviceDocId);
            }
            iteratingDate = iteratingDate.plusDays(1);
        }
    }

    private boolean suspensionStartDateFallsInRetryIntervalAndAdherenceHasNotBeenRecordedForTheWeek(SuspendedAdherenceData suspendedAdherenceData, DateTime iteratingDate, String treatmentAdviceDocId, LocalDate patientLastRecallDate) {
        return isIteratingDateInRetryIntervalOfPatientLastRecallDate(iteratingDate, patientLastRecallDate) && !fourDayRecallService.isAdherenceCapturedForAnyWeek(suspendedAdherenceData.patientId(),
                                                                     treatmentAdviceDocId, fourDayRecallService.getStartDateForAnyWeek(suspendedAdherenceData.patientId(), iteratingDate.toLocalDate()));
    }

    private boolean isIteratingDateInRetryIntervalOfPatientLastRecallDate(DateTime iteratingDate, LocalDate patientLastRecallDate) {
        return ((patientLastRecallDate.plusDays(1).compareTo(iteratingDate.toLocalDate()) == 0) || (patientLastRecallDate.plusDays(2).compareTo(iteratingDate.toLocalDate()) == 0));
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

    private void createWeeklyAdherenceLogForTheWeek(SuspendedAdherenceData suspendedAdherenceData, DateTime iteratingDate, String treatmentAdviceDocId) {
        WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog(suspendedAdherenceData.patientId(),
                treatmentAdviceDocId,
                fourDayRecallService.getStartDateForAnyWeek(suspendedAdherenceData.patientId(), iteratingDate.toLocalDate()),
                DateUtil.today(), suspendedAdherenceData.getAdherenceDataWhenPatientWasSuspended().numberOfDaysMissed());
        allWeeklyAdherenceLogs.add(weeklyAdherenceLog);
    }
}