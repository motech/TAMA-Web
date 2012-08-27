package org.motechproject.tama.symptomreporting.criteria;

import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.appointments.api.service.contract.VisitResponse;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContinueToSymptomsTreeCriteria {

    private AllLabResults allLabResults;
    private AllVitalStatistics allVitalStatistics;
    private AllClinicVisits allClinicVisits;

    @Autowired
    public ContinueToSymptomsTreeCriteria(AllLabResults allLabResults, AllVitalStatistics allVitalStatistics, AllClinicVisits allClinicVisits) {
        this.allLabResults = allLabResults;
        this.allVitalStatistics = allVitalStatistics;
        this.allClinicVisits = allClinicVisits;
    }

    public boolean shouldContinue(String patientId) {
        VitalStatistics vitalStatistics = allVitalStatistics.findLatestVitalStatisticByPatientId(patientId);
        if (vitalStatistics != null) {
            final ClinicVisit baselineVisit = allClinicVisits.getBaselineVisit(patientId);
            final List<String> labResultIds =  baselineVisit.getLabResultIds();
            for (String key : labResultIds) {
                final LabResult labResult = allLabResults.get(key);
                if (labResult != null && labResult.isCD4()) {
                    return true;
                }
            }
        }
        return false;
    }
}
