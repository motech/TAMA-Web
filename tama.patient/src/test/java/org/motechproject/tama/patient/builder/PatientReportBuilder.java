package org.motechproject.tama.patient.builder;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.refdata.domain.Regimen;

public class PatientReportBuilder {

    private PatientReport patientReport;
    private Patient patient;
    private TreatmentAdvice earliestTreatmentAdvice;
    private TreatmentAdvice currentTreatmentAdvice;
    private Regimen regimen;

    public PatientReportBuilder() {
        patient = new Patient();
        earliestTreatmentAdvice = new TreatmentAdvice();
        currentTreatmentAdvice = new TreatmentAdvice();
        regimen = new Regimen();
        patientReport = new PatientReport(patient, earliestTreatmentAdvice, currentTreatmentAdvice, regimen);
    }

    public static PatientReportBuilder newPatientReport() {
        return new PatientReportBuilder();
    }

    public PatientReportBuilder withPatientId(String patientId) {
        patientReport.getPatient().setPatientId(patientId);
        return this;
    }

    public PatientReportBuilder withPatientDocumentId(String patientDocumentId) {
        patientReport.getPatient().setId(patientDocumentId);
        return this;
    }

    public TreatmentAdvice earliestTreatmentAdvice() {
        return earliestTreatmentAdvice;
    }

    public TreatmentAdvice currentTreatmentAdvice() {
        return currentTreatmentAdvice;
    }

    public Regimen regimen() {
        return regimen;
    }

    public PatientReport build() {
        return patientReport;
    }
}
