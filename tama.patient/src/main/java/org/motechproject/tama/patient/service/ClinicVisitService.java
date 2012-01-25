package org.motechproject.tama.patient.service;

import org.motechproject.tama.patient.domain.ClinicVisit;
import org.motechproject.tama.patient.repository.AllClinicVisits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClinicVisitService {

    private AllClinicVisits allClinicVisits;

    @Autowired
    public ClinicVisitService(AllClinicVisits allClinicVisits) {
        this.allClinicVisits = allClinicVisits;
    }

    public String createVisit(String treatmentAdviceId, List<String> labResultIds, String vitalStatisticsId) {
        ClinicVisit clinicVisit = new ClinicVisit();
        clinicVisit.setTreatmentAdviceId(treatmentAdviceId);
        clinicVisit.setLabResultIds(labResultIds);
        clinicVisit.setVitalStatisticsId(vitalStatisticsId);
        allClinicVisits.add(clinicVisit);
        return clinicVisit.getId();
    }
}