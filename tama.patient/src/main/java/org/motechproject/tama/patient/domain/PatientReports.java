package org.motechproject.tama.patient.domain;

import lombok.EqualsAndHashCode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode
public class PatientReports  {

    public List<PatientReport> patientReports;

    public PatientReports() {
        patientReports = new ArrayList<>();
    }

    public PatientReports(List<PatientReport> patientReports) {
        this.patientReports = new ArrayList<>(patientReports);
    }

   public void addReport(PatientReport patientReport) {
         patientReports.add(patientReport);
   }

    public List<String> getPatientDocIds() {
        List<String> docIds = new ArrayList<>();
        for (PatientReport patientReport : patientReports) {
            docIds.add(patientReport.getPatientDocId());
        }
        return docIds;
    }

    public PatientReport getPatientReport(String patientDocumentId) {
        for (PatientReport patientReport : patientReports) {
            if (StringUtils.equals(patientReport.getPatientDocId(), patientDocumentId)) {
                return patientReport;
            }
        }
        return null;
    }

    public PatientReports filterByClinic(String clinicId,List<PatientReport> reports) {
        ArrayList<PatientReport> filteredAlerts = new ArrayList<PatientReport>();
        CollectionUtils.select(reports, getSelectorForClinicId(clinicId), filteredAlerts);
        return new PatientReports(filteredAlerts);
    }


    private org.apache.commons.collections.Predicate getSelectorForClinicId(final String clinicId) {
        return new org.apache.commons.collections.Predicate() {
            @Override
            public boolean evaluate(Object o) {
                PatientReport patientReport = (PatientReport) o;
                return patientReport.getPatient().getClinic_id().equals(clinicId);
            }
        };
    }
}
