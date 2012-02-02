package org.motechproject.tama.patient.service;

import org.joda.time.DateTime;
import org.motechproject.tama.patient.domain.ClinicVisit;
import org.motechproject.tama.patient.repository.AllClinicVisits;
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

    public String createVisit(DateTime now, String patientId, String treatmentAdviceId, List<String> labResultIds, String vitalStatisticsId) {
        ClinicVisit clinicVisit = new ClinicVisit();
        clinicVisit.setPatientId(patientId);
        clinicVisit.setTreatmentAdviceId(treatmentAdviceId);
        clinicVisit.setLabResultIds(labResultIds);
        clinicVisit.setVitalStatisticsId(vitalStatisticsId);
        clinicVisit.setVisitDate(now);
        allClinicVisits.add(clinicVisit);
        return clinicVisit.getId();
    }

    //TODO: Can sorting be moved to repository layer?
    public ClinicVisit visitZero(String patientId) {
        List<ClinicVisit> clinicVisits = allClinicVisits.find_by_patient_id(patientId);
        Collections.sort(clinicVisits);
        return (clinicVisits.isEmpty()) ? null : clinicVisits.get(0);
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
}