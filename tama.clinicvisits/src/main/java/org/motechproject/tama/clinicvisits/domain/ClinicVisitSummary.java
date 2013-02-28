package org.motechproject.tama.clinicvisits.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.refdata.domain.Regimen;

@EqualsAndHashCode
@Data
public class ClinicVisitSummary {
    private PatientReport patientReport;
    private ClinicVisit clinicVisit;
    private TreatmentAdvice treatmentAdvice;
    private LabResults labResults;
    private VitalStatistics vitalStatistics;
    private ReportedOpportunisticInfections reportedOpportunisticInfections;
    private Regimen regimen;


    public ClinicVisitSummary(PatientReport patientReport, ClinicVisit clinicVisit, TreatmentAdvice treatmentAdvice, LabResults labResults, VitalStatistics vitalStatistics, ReportedOpportunisticInfections reportedOpportunisticInfection, Regimen regimen) {
        this.patientReport = patientReport;
        this.clinicVisit = clinicVisit;
        this.treatmentAdvice = treatmentAdvice;
        this.labResults = labResults;
        this.vitalStatistics = vitalStatistics;
        this.reportedOpportunisticInfections = reportedOpportunisticInfection;
        this.regimen = regimen;
    }
}

