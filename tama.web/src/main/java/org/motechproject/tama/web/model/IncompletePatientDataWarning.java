package org.motechproject.tama.web.model;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllVitalStatistics;

import java.util.ArrayList;

public class IncompletePatientDataWarning {

    private class RequiredPatientDetail {
        public Object container;
        public String containerName;

        public RequiredPatientDetail(Object container, String containerName) {
            this.container = container;
            this.containerName = containerName;
        }
    }

    private Patient patient;
    private AllVitalStatistics allVitalStatistics;
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllLabResults allLabResults;
    private ArrayList<RequiredPatientDetail> requiredPatientDetails;

    public IncompletePatientDataWarning(Patient patient, AllVitalStatistics allVitalStatistics, AllTreatmentAdvices allTreatmentAdvices, AllLabResults allLabResults) {
        this.patient = patient;
        this.allVitalStatistics = allVitalStatistics;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allLabResults = allLabResults;
    }

    private void findAllRequiredInfo(){
        requiredPatientDetails = new ArrayList<RequiredPatientDetail>();
        requiredPatientDetails.add(requiredVitalStatistics());
        requiredPatientDetails.add(requiredTreatmentAdvice());
        requiredPatientDetails.add(requiredLabResults());
    }

    @Override
    public String toString(){
        findAllRequiredInfo();
        if(patient.getStatus().isInactive())
            return "Patient has not been Activated";
        ArrayList<String> pendingPatientDetailsArray = new ArrayList<String>();

        for(RequiredPatientDetail detail : this.requiredPatientDetails){
            if (detail.container == null)
                pendingPatientDetailsArray.add(detail.containerName);
        }

        if (pendingPatientDetailsArray.isEmpty())
            return null;
        return "The " + StringUtils.join(pendingPatientDetailsArray, ", ") + " need to be filled so that the patient can access Symptoms Reporting and Health Tips";
    }

    private RequiredPatientDetail requiredLabResults() {
        LabResults labResults = allLabResults.allLabResults(patient.getId());
        boolean validBaselineCD4Count = labResults.baselineCD4Count() == LabResult.INVALID_CD4_COUNT;
        labResults = validBaselineCD4Count ? null: labResults;
        return new RequiredPatientDetail(labResults, "Lab Results(CD4 count)");
    }

    private RequiredPatientDetail requiredTreatmentAdvice() {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());
        return new RequiredPatientDetail(treatmentAdvice, "Regimen details");
    }

    private RequiredPatientDetail requiredVitalStatistics() {
        VitalStatistics vitalStats = allVitalStatistics.findLatestVitalStatisticByPatientId(patient.getId());
        vitalStats = (vitalStats != null && (vitalStats.getWeightInKg() != null && vitalStats.getHeightInCm() != null)) ? vitalStats : null;
        return new RequiredPatientDetail(vitalStats, "Vital Statistics(Height, Weight)");
    }
}
