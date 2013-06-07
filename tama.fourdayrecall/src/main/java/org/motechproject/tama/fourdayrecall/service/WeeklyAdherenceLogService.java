package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.reporting.WeeklyAdherenceMapper;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.reporting.service.WeeklyPatientReportingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WeeklyAdherenceLogService {
    protected WeeklyPatientReportingService weeklyPatientReportingService;
    protected WeeklyAdherenceMapper weeklyAdherenceMapper;
    protected AllPatients allPatients;
    protected AllTreatmentAdvices allTreatmentAdvices;
    protected FourDayRecallDateService fourDayRecallDateService;
    protected AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;

    @Autowired
    public WeeklyAdherenceLogService(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices,
                                     AllWeeklyAdherenceLogs allWeeklyAdherenceLogs,
                                     FourDayRecallDateService fourDayRecallDateService, WeeklyPatientReportingService weeklyPatientReportingService,
                                     WeeklyAdherenceMapper weeklyAdherenceMapper) {
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.fourDayRecallDateService = fourDayRecallDateService;
        this.weeklyPatientReportingService = weeklyPatientReportingService;
        this.weeklyAdherenceMapper = weeklyAdherenceMapper;
    }

    public WeeklyAdherenceLog get(String patientId, int weeksBefore) {
        Patient patient = allPatients.get(patientId);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        LocalDate startDateForPreviousWeek = fourDayRecallDateService.treatmentWeekStartDate(DateUtil.today(), patient, treatmentAdvice).minusWeeks(weeksBefore);

        return allWeeklyAdherenceLogs.findLogByWeekStartDate(patientId, treatmentAdvice.getId(), startDateForPreviousWeek);
    }

    public void createLog(String patientId, int numberOfDaysMissed) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        LocalDate startDateForCurrentWeek = fourDayRecallDateService.treatmentWeekStartDate(DateUtil.today(), allPatients.get(patientId), treatmentAdvice);
        createLog(patientId, startDateForCurrentWeek, numberOfDaysMissed);
    }

    public void createLog(String patientId, LocalDate weekStartDate, int numberOfDaysMissed) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        WeeklyAdherenceLog logInDb = allWeeklyAdherenceLogs.findLogByWeekStartDate(patientId, treatmentAdvice.getId(), weekStartDate);
        WeeklyAdherenceLog newLog = WeeklyAdherenceLog.create(patientId, treatmentAdvice.getId(), weekStartDate, numberOfDaysMissed);
        upsertLog(logInDb, newLog);
    }

    public void createLog(String patientId, LocalDate weekStartDate, int numberOfDaysMissed, DateTime logDate) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        WeeklyAdherenceLog logInDb = allWeeklyAdherenceLogs.findLogByWeekStartDate(patientId, treatmentAdvice.getId(), weekStartDate);
        WeeklyAdherenceLog newLog = WeeklyAdherenceLog.create(patientId, treatmentAdvice.getId(), weekStartDate, numberOfDaysMissed, logDate);
        upsertLog(logInDb, newLog);
    }

    public void createNotRespondedLog(String patientId) {
        Patient patient = allPatients.get(patientId);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        String treatmentAdviceDocId = treatmentAdvice.getId();
        LocalDate startDateForCurrentWeek = fourDayRecallDateService.treatmentWeekStartDate(DateUtil.today(), patient, treatmentAdvice);

        WeeklyAdherenceLog weeklyAdherenceLog = WeeklyAdherenceLog.create(patientId, treatmentAdviceDocId, startDateForCurrentWeek, TAMAConstants.DAYS_TO_RECALL_FOR_PATIENTS_ON_WEEKLY_ADHERENCE_CALL);
        weeklyAdherenceLog.setNotResponded(true);
        upsertLog(findLogSimilarLog(weeklyAdherenceLog), weeklyAdherenceLog);
    }

    private WeeklyAdherenceLog findLogSimilarLog(WeeklyAdherenceLog log) {
        return allWeeklyAdherenceLogs.findLogByWeekStartDate(log.getPatientId(), log.getTreatmentAdviceId(), log.getWeekStartDate());
    }

    private void upsertLog(WeeklyAdherenceLog currentLog, WeeklyAdherenceLog newLog) {
        this.weeklyAdherenceMapper = new WeeklyAdherenceMapper(allWeeklyAdherenceLogs, allPatients, allTreatmentAdvices);


        if (currentLog == null) {
            allWeeklyAdherenceLogs.add(newLog);
            Patient patient = allPatients.findByPatientId(newLog.getPatientId());
            weeklyPatientReportingService.save(weeklyAdherenceMapper.map(newLog));
        } else if (currentLog.getNotResponded()) {
            currentLog.merge(newLog);
            Patient patient = allPatients.findByPatientId(currentLog.getPatientId());
            allWeeklyAdherenceLogs.update(currentLog);
            weeklyPatientReportingService.update(weeklyAdherenceMapper.map(newLog));
        }
    }
}
