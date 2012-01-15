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
                                     AllWeeklyAdherenceLogs allWeeklyAdherenceLogs) {
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.fourDayRecallDateService = new FourDayRecallDateService();
    }

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
        Patient patient = allPatients.get(patientId);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        String treatmentAdviceDocId = treatmentAdvice.getId();
        LocalDate startDateForCurrentWeek = fourDayRecallDateService.treatmentWeekStartDate(DateUtil.today(), patient, treatmentAdvice);

        WeeklyAdherenceLog currentLog = allWeeklyAdherenceLogs.findLogByWeekStartDate(patient.getId(), treatmentAdvice.getId(), startDateForCurrentWeek);
        WeeklyAdherenceLog newLog = WeeklyAdherenceLog.create(patientId, treatmentAdviceDocId, startDateForCurrentWeek, numberOfDaysMissed);
        upsertLog(currentLog, newLog);
    }

    public void createLogOn(String patientId, LocalDate logDate, int dosesTaken) {
        TreatmentAdvice currentTreatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        WeeklyAdherenceLog logByWeekStartDate = allWeeklyAdherenceLogs.findLogByWeekStartDate(patientId, currentTreatmentAdvice.getId(), logDate);
        WeeklyAdherenceLog weeklyAdherenceLog = WeeklyAdherenceLog.create(patientId, currentTreatmentAdvice.getId(), logDate, dosesTaken);
        upsertLog(logByWeekStartDate, weeklyAdherenceLog);
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
