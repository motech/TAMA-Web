package org.motechproject.tama.clinicvisits.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ClinicVisitService {

    private AllClinicVisits allClinicVisits;

    @Autowired
    public ClinicVisitService(AllClinicVisits allClinicVisits) {
        this.allClinicVisits = allClinicVisits;
    }

    public String updateVisit(String visitId, DateTime visitDate, String patientId, String treatmentAdviceId, List<String> labResultIds, String vitalStatisticsId) {
        ClinicVisit clinicVisit = allClinicVisits.get(visitId);
        clinicVisit.setPatientId(patientId);
        clinicVisit.setTreatmentAdviceId(treatmentAdviceId);
        clinicVisit.setLabResultIds(labResultIds);
        clinicVisit.setVitalStatisticsId(vitalStatisticsId);
        clinicVisit.setVisitDate(visitDate);
        allClinicVisits.update(clinicVisit);
        return clinicVisit.getId();
    }

    public void createExpectedVisit(DateTime expectedVisitTime, int week, String patientId) {
        ClinicVisit clinicVisit = ClinicVisit.createExpectedVisit(expectedVisitTime, week, patientId);
        allClinicVisits.add(clinicVisit);
    }

    public ClinicVisit baselineVisit(String patientDocumentId) {
        return allClinicVisits.getBaselineVisit(patientDocumentId);
    }

    public void changeRegimen(String clinicVisitId, String newTreatmentAdviceId) {
        final ClinicVisit clinicVisit = allClinicVisits.get(clinicVisitId);
        clinicVisit.setTreatmentAdviceId(newTreatmentAdviceId);
        allClinicVisits.update(clinicVisit);
    }

    public void updateLabResults(String clinicVisitId, List<String> labResultIds) {
        final ClinicVisit clinicVisit = allClinicVisits.get(clinicVisitId);
        clinicVisit.setLabResultIds(labResultIds);
        allClinicVisits.update(clinicVisit);
    }

    public void updateVitalStatistics(String clinicVisitId, String vitalStatisticsId) {
        final ClinicVisit clinicVisit = allClinicVisits.get(clinicVisitId);
        clinicVisit.setVitalStatisticsId(vitalStatisticsId);
        allClinicVisits.update(clinicVisit);
    }

    public List<ClinicVisit> getClinicVisits(String patientId) {
        return allClinicVisits.find_by_patient_id(patientId);
    }

    public ClinicVisit getClinicVisit(String clinicVisitId) {
        return allClinicVisits.get(clinicVisitId);
    }

    public void confirmVisitDate(String clinicVisitId, DateTime confirmedVisitDate) {
        ClinicVisit clinicVisit = allClinicVisits.get(clinicVisitId);
        clinicVisit.setConfirmedVisitDate(confirmedVisitDate);
        allClinicVisits.update(clinicVisit);
    }

    public void adjustDueDate(String clinicVisitId, LocalDate adjustedDueDate) {
        ClinicVisit clinicVisit = allClinicVisits.get(clinicVisitId);
        clinicVisit.setAdjustedDueDate(adjustedDueDate);
        allClinicVisits.update(clinicVisit);
    }

    public void markAsMissed(String clinicVisitId) {
        ClinicVisit clinicVisit = allClinicVisits.get(clinicVisitId);
        clinicVisit.setMissed(true);
        allClinicVisits.update(clinicVisit);
    }

    public void setVisitDate(String clinicVisitId, DateTime visitDate) {
        ClinicVisit clinicVisit = allClinicVisits.get(clinicVisitId);
        clinicVisit.setVisitDate(visitDate);
        allClinicVisits.update(clinicVisit);
    }
}