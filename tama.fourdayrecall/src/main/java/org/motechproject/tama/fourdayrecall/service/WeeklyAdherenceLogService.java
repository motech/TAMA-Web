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
    public WeeklyAdherenceLogService(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, AllWeeklyAdherenceLogs allWeeklyAdherenceLogs) {
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.fourDayRecallDateService = new FourDayRecallDateService();
    }

    public WeeklyAdherenceLogService(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, AllWeeklyAdherenceLogs allWeeklyAdherenceLogs, FourDayRecallDateService fourDayRecallDateService) {
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
        allWeeklyAdherenceLogs.add(WeeklyAdherenceLog.create(patientId, treatmentAdviceDocId, startDateForCurrentWeek, numberOfDaysMissed));
    }

    public void createLogOn(String patientId, LocalDate logDate, int dosesTaken) {
        TreatmentAdvice currentTreatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        if (allWeeklyAdherenceLogs.findLogByWeekStartDate(patientId, currentTreatmentAdvice.getId(), logDate) == null) {
            allWeeklyAdherenceLogs.add(WeeklyAdherenceLog.create(patientId, currentTreatmentAdvice.getId(), logDate, dosesTaken));
        }
    }
}
