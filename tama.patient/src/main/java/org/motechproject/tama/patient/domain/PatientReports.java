package org.motechproject.tama.patient.domain;

import lombok.EqualsAndHashCode;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
public class PatientReports {

    private List<PatientReport> patientReports;

    public PatientReports() {
        patientReports = new ArrayList<>();
    }

    public PatientReports(List<PatientReport> patientReports) {
        this.patientReports = new ArrayList<>(patientReports);
    }

    public void add(PatientReport patientReport) {
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
}
