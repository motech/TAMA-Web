package org.motechproject.tama.fourdayrecall.reporting;


import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.reports.contract.WeeklyAdherenceLogRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WeeklyAdherenceMapper {

    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;

    private AllPatients allPatients;

    private AllTreatmentAdvices allTreatmentAdvices;

    @Autowired
    public WeeklyAdherenceMapper(AllWeeklyAdherenceLogs allWeeklyAdherenceLogs, AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices) {
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;

    }

    public WeeklyAdherenceLogRequest map(WeeklyAdherenceLog weeklyAdherenceLog) {
        Patient patient = allPatients.findByPatientId(weeklyAdherenceLog.getPatientId());
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(weeklyAdherenceLog.getPatientId());
        WeeklyAdherenceLogRequest weeklyAdherenceLogRequest = new WeeklyAdherenceLogRequest();
        weeklyAdherenceLogRequest.setPatientId(weeklyAdherenceLog.getPatientId());
        weeklyAdherenceLogRequest.setClinicName(allPatients.findClinicFor(patient));
        weeklyAdherenceLogRequest.setArtStartDate(patient.getActivationDate().toDate());
        weeklyAdherenceLogRequest.setTreatmentAdviceId(weeklyAdherenceLog.getTreatmentAdviceId());
        weeklyAdherenceLogRequest.setStartDate(treatmentAdvice.getStartDate());
        weeklyAdherenceLogRequest.setWeekStartDate(weeklyAdherenceLog.getWeekStartDate().toDate());
        weeklyAdherenceLogRequest.setAdherenceLoggedDate(weeklyAdherenceLog.getLogDate().toDate());
        weeklyAdherenceLogRequest.setNumberOfDaysMissed(weeklyAdherenceLog.getNumberOfDaysMissed());
        return weeklyAdherenceLogRequest;
    }
}
