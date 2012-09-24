package org.motechproject.tama.web.model;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllVitalStatistics;

import java.util.*;

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
    private AllClinicVisits allClinicVisits;
    private ArrayList<RequiredPatientDetail> requiredPatientDetails = new ArrayList<RequiredPatientDetail>();

    public IncompletePatientDataWarning(Patient patient,
                                        AllVitalStatistics allVitalStatistics,
                                        AllTreatmentAdvices allTreatmentAdvices,
                                        AllLabResults allLabResults,
                                        AllClinicVisits allClinicVisits) {
        this.patient = patient;
        this.allVitalStatistics = allVitalStatistics;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allLabResults = allLabResults;
        this.allClinicVisits = allClinicVisits;
    }

    private void findAllRequiredInfo() {
        if (patient.getStatus().isActive()) {
            findRequiredVitalStatistics();
            findRequiredTreatmentAdvice();
            findRequiredLabResults();
        }
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
        ClinicVisit baselineVisit = allClinicVisits.getBaselineVisit(patient.getId());
        HashSet<String> requiredFor = new HashSet<String>(Arrays.asList("Symptoms Reporting", "Health Tips"));
        LabResult cd4Result = null;
        if (baselineVisit != null) {
            List<LabResult> labResults = allLabResults.withIds(baselineVisit.getLabResultIds());
            cd4Result = new LabResults(labResults).latestCD4Result();
        } else {
            cd4Result = null;
        }
        requiredPatientDetails.add(new RequiredPatientDetail(cd4Result, "Lab Results(CD4 count)", requiredFor));
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
