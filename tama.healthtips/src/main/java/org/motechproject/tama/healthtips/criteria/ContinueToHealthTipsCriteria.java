package org.motechproject.tama.healthtips.criteria;

import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContinueToHealthTipsCriteria {

    private AllLabResults allLabResults;
    private AllVitalStatistics allVitalStatistics;
    private AllClinicVisits allClinicVisits;

    @Autowired
    public ContinueToHealthTipsCriteria(AllLabResults allLabResults, AllVitalStatistics allVitalStatistics, AllClinicVisits allClinicVisits) {
        this.allLabResults = allLabResults;
        this.allVitalStatistics = allVitalStatistics;
        this.allClinicVisits = allClinicVisits;
    }

    public boolean shouldContinue(String patientId) {
        LabResults results = allLabResults.allLabResults(patientId);
        boolean isResultsEmpty = !(results == null || results.isEmpty());
        return isResultsEmpty && hasCD4Result(results);
    }

    private boolean hasCD4Result(LabResults results) {
        return results.latestCD4Count() != LabResult.INVALID_CD4_COUNT;
    }
}
