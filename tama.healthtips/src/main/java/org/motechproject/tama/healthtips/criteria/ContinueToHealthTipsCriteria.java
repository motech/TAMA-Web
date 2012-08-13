package org.motechproject.tama.healthtips.criteria;

import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContinueToHealthTipsCriteria {

    private AllLabResults allLabResults;
    private AllVitalStatistics allVitalStatistics;

    @Autowired
    public ContinueToHealthTipsCriteria(AllLabResults allLabResults, AllVitalStatistics allVitalStatistics) {
        this.allLabResults = allLabResults;
        this.allVitalStatistics = allVitalStatistics;
    }

    public boolean shouldContinue(String patientId) {
        LabResults results = allLabResults.allLabResults(patientId);
        VitalStatistics vitalStatistics = allVitalStatistics.findLatestVitalStatisticByPatientId(patientId);
        return ((results != null && !results.isEmpty()) && null != vitalStatistics);
    }
}
