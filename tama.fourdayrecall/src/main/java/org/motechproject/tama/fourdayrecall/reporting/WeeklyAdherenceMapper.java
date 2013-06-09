package org.motechproject.tama.fourdayrecall.reporting;


import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.motechproject.tama.reports.contract.WeeklyAdherenceLogRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WeeklyAdherenceMapper {

    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;

    private AllPatients allPatients;

    private AllTreatmentAdvices allTreatmentAdvices;

    private AllRegimens allRegimens;


    @Autowired
    public WeeklyAdherenceMapper(AllWeeklyAdherenceLogs allWeeklyAdherenceLogs, AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, AllRegimens allRegimens) {
        this.allWeeklyAdherenceLogs = allWeeklyAdherenceLogs;
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allRegimens = allRegimens;

    }

    public WeeklyAdherenceLogRequest map(WeeklyAdherenceLog weeklyAdherenceLog) {
        Patient patient = allPatients.get(weeklyAdherenceLog.getPatientId());
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(weeklyAdherenceLog.getPatientId());
        Regimen regimen = allRegimens.get(treatmentAdvice.getRegimenId());
        WeeklyAdherenceLogRequest weeklyAdherenceLogRequest = new WeeklyAdherenceLogRequest();
        weeklyAdherenceLogRequest.setPatientId(weeklyAdherenceLog.getPatientId());
        weeklyAdherenceLogRequest.setClinicName(patient.getClinic().getName());
        weeklyAdherenceLogRequest.setArtStartDate(patient.getActivationDate().toDate());
        weeklyAdherenceLogRequest.setTreatmentAdviceId(regimen.getDisplayName());
        weeklyAdherenceLogRequest.setStartDate(treatmentAdvice.getStartDate());
        weeklyAdherenceLogRequest.setWeekStartDate(weeklyAdherenceLog.getWeekStartDate().toDate());
        weeklyAdherenceLogRequest.setAdherenceLoggedDate(weeklyAdherenceLog.getLogDate().toDate());
        weeklyAdherenceLogRequest.setNumberOfDaysMissed(weeklyAdherenceLog.getNumberOfDaysMissed());
        return weeklyAdherenceLogRequest;
    }
}
