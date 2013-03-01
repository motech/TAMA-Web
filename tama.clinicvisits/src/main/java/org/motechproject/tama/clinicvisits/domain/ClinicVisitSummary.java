package org.motechproject.tama.clinicvisits.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;
import org.motechproject.tama.patient.contract.DrugDosageContract;
import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.refdata.domain.Regimen;

@EqualsAndHashCode
@Data
public class ClinicVisitSummary {
    private PatientReport patientReport;
    private DateTime visitDate;
    private String drugCompositonGroupName;
    private DrugDosageContract drugDosageOne;
    private DrugDosageContract drugDosageTwo;
    private LabResults labResults;
    private VitalStatistics vitalStatistics;
    private String reportedOpportunisticInfections;
    private Regimen regimen;

    public ClinicVisitSummary(PatientReport patientReport, DateTime visitDate, LabResults labResults, VitalStatistics vitalStatistics, String reportedOpportunisticInfection, Regimen regimen, String drugCompositonGroupName, DrugDosageContract drugDosageOne, DrugDosageContract drugDosageTwo) {
        this.patientReport = patientReport;
        this.visitDate = visitDate;
        this.labResults = labResults;
        this.vitalStatistics = vitalStatistics;
        this.reportedOpportunisticInfections = reportedOpportunisticInfection;
        this.regimen = regimen;
        this.drugCompositonGroupName = drugCompositonGroupName;
        this.drugDosageOne = drugDosageOne;
        this.drugDosageTwo = drugDosageTwo;
    }
}

