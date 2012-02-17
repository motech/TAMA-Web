package org.motechproject.tama.web.model;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.patient.domain.*;
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
        VitalStatistics vitalStatistics = allVitalStatistics.findLatestVitalStatisticByPatientId(patient.getId());
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());
        LabResults labResults = allLabResults.findLatestLabResultsByPatientId(patient.getId());
        labResults = labResults.isEmpty() ? null: labResults;

        requiredPatientDetails = new ArrayList<RequiredPatientDetail>();
        requiredPatientDetails.add(new RequiredPatientDetail(vitalStatistics, "Vital Statistics"));
        requiredPatientDetails.add(new RequiredPatientDetail(treatmentAdvice, "Regimen details"));
        requiredPatientDetails.add(new RequiredPatientDetail(labResults, "Lab Results"));
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
}
