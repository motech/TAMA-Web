package org.motechproject.tama.clinicvisits.domain;

import lombok.EqualsAndHashCode;
import org.motechproject.tama.patient.domain.*;

@EqualsAndHashCode
public class ClinicVisitSummary {
    private PatientReport patientReport;
    private ClinicVisit clinicVisit;
    private TreatmentAdvice treatmentAdvice;
    private LabResults labResults;
    private VitalStatistics vitalStatistics;
    private ReportedOpportunisticInfections reportedOpportunisticInfections;


    public ClinicVisitSummary(PatientReport patientReport, ClinicVisit clinicVisit, TreatmentAdvice treatmentAdvice, LabResults labResults, VitalStatistics vitalStatistics, ReportedOpportunisticInfections reportedOpportunisticInfection) {
        this.patientReport = patientReport;
        this.clinicVisit = clinicVisit;
        this.treatmentAdvice = treatmentAdvice;
        this.labResults = labResults;
        this.vitalStatistics = vitalStatistics;
        this.reportedOpportunisticInfections = reportedOpportunisticInfection;
    }
}

