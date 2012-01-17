package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.LocalDate;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WeeklyAdherenceLogService {
    protected AllPatients allPatients;
    protected AllTreatmentAdvices allTreatmentAdvices;
    protected FourDayRecallDateService fourDayRecallDateService;
    protected AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;

    @Autowired
    public WeeklyAdherenceLogService(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices,
                                     AllWeeklyAdherenceLogs allWeeklyAdherenceLogs,
                                     FourDayRecallDateService fourDayRecallDateService) {
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.fourDayRecallDateService = fourDayRecallDateService;
    }

    public WeeklyAdherenceLog get(String patientId, int weeksBefore) {
        Patient patient = allPatients.get(patientId);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        LocalDate startDateForPreviousWeek = fourDayRecallDateService.treatmentWeekStartDate(DateUtil.today(), patient, treatmentAdvice).minusWeeks(weeksBefore);

        return allWeeklyAdherenceLogs.findLogByWeekStartDate(patientId, treatmentAdvice.getId(), startDateForPreviousWeek);
    }

    public void createLogFor(String patientId, int numberOfDaysMissed) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        LocalDate startDateForCurrentWeek = fourDayRecallDateService.treatmentWeekStartDate(DateUtil.today(), allPatients.get(patientId), treatmentAdvice);

        createLogFor(patientId, startDateForCurrentWeek, numberOfDaysMissed);
    }

    public void createLogFor(String patientId, LocalDate weekStartDate, int numberOfDaysMissed) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        WeeklyAdherenceLog logInDb = allWeeklyAdherenceLogs.findLogByWeekStartDate(patientId, treatmentAdvice.getId(), weekStartDate);
        WeeklyAdherenceLog newLog = WeeklyAdherenceLog.create(patientId, treatmentAdvice.getId(), weekStartDate, numberOfDaysMissed);
        upsertLog(logInDb, newLog);
    }

    public void createLogFor(String patientId, LocalDate weekStartDate, int numberOfDaysMissed, LocalDate logDate) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        WeeklyAdherenceLog logInDb = allWeeklyAdherenceLogs.findLogByDate(patientId, treatmentAdvice.getId(), logDate);
        WeeklyAdherenceLog newLog = WeeklyAdherenceLog.create(patientId, treatmentAdvice.getId(), weekStartDate, numberOfDaysMissed, logDate);
        upsertLog(logInDb, newLog);
    }

    public void createNotRespondedLog(String patientId, int numberOfDaysMissed) {
        Patient patient = allPatients.get(patientId);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        String treatmentAdviceDocId = treatmentAdvice.getId();
        LocalDate startDateForCurrentWeek = fourDayRecallDateService.treatmentWeekStartDate(DateUtil.today(), patient, treatmentAdvice);

        WeeklyAdherenceLog weeklyAdherenceLog = WeeklyAdherenceLog.create(patientId, treatmentAdviceDocId, startDateForCurrentWeek, numberOfDaysMissed);
        weeklyAdherenceLog.setNotResponded(true);
        allWeeklyAdherenceLogs.add(weeklyAdherenceLog);
    }

    private void upsertLog(WeeklyAdherenceLog currentLog, WeeklyAdherenceLog newLog) {
        if (currentLog == null) {
            allWeeklyAdherenceLogs.add(newLog);
        } else if (currentLog.getNotResponded()) {
            currentLog.merge(newLog);
            allWeeklyAdherenceLogs.update(currentLog);
        }
    }
}
