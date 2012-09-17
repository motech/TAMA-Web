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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class IncompletePatientDataWarning {

    private class RequiredPatientDetail {
        public Object container;
        public String containerName;
        private Set<String> requiredFor;

        public RequiredPatientDetail(Object container, String containerName, Set<String> requiredFor) {
            this.container = container;
            this.containerName = containerName;
            this.requiredFor = requiredFor;
        }
    }

    private Patient patient;
    private AllVitalStatistics allVitalStatistics;
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllLabResults allLabResults;
    private ArrayList<RequiredPatientDetail> requiredPatientDetails = new ArrayList<RequiredPatientDetail>();

    public IncompletePatientDataWarning(Patient patient, AllVitalStatistics allVitalStatistics, AllTreatmentAdvices allTreatmentAdvices, AllLabResults allLabResults) {
        this.patient = patient;
        this.allVitalStatistics = allVitalStatistics;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allLabResults = allLabResults;
    }

    private void findAllRequiredInfo() {
        findRequiredVitalStatistics();
        findRequiredTreatmentAdvice();
        findRequiredLabResults();
    }

    @Override
    public String toString() {
        findAllRequiredInfo();
        if (patient.getStatus().isInactive())
            return "Patient has not been Activated";
        ArrayList<String> pendingPatientDetailsArray = new ArrayList<String>();
        Set<String> pendingDetailsRequiredFor = new HashSet<String>();

        for (RequiredPatientDetail detail : this.requiredPatientDetails) {
            if (detail.container == null) {
                pendingPatientDetailsArray.add(detail.containerName);
                pendingDetailsRequiredFor.addAll(detail.requiredFor);
            }
        }

        if (pendingPatientDetailsArray.isEmpty())
            return null;
        return "The " + StringUtils.join(pendingPatientDetailsArray, ", ") + " need to be filled so that the patient can access "
                + StringUtils.join(pendingDetailsRequiredFor.toArray(), " and ");
    }

    private void findRequiredLabResults() {
        LabResults labResults = allLabResults.allLabResults(patient.getId());
        boolean validBaselineCD4Count = labResults.baselineCD4Count() == LabResult.INVALID_CD4_COUNT;
        labResults = validBaselineCD4Count ? null : labResults;
        HashSet<String> requiredFor = new HashSet<String>(Arrays.asList("Symptoms Reporting", "Health Tips"));
        requiredPatientDetails.add(new RequiredPatientDetail(labResults, "Lab Results(CD4 count)", requiredFor));
    }

    private void findRequiredTreatmentAdvice() {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());
        HashSet<String> requiredFor = new HashSet<String>(Arrays.asList("Symptoms Reporting", "Health Tips"));
        requiredPatientDetails.add(new RequiredPatientDetail(treatmentAdvice, "Regimen details", requiredFor));
    }

    private void findRequiredVitalStatistics() {
        VitalStatistics vitalStats = allVitalStatistics.findLatestVitalStatisticByPatientId(patient.getId());
        vitalStats = (vitalStats != null && (vitalStats.getWeightInKg() != null && vitalStats.getHeightInCm() != null)) ? vitalStats : null;
        HashSet<String> requiredFor = new HashSet<String>(Arrays.asList("Symptoms Reporting"));
        requiredPatientDetails.add(new RequiredPatientDetail(vitalStats, "Vital Statistics(Height, Weight)", requiredFor));
    }
}
