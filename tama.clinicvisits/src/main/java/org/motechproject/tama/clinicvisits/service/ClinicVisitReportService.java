package org.motechproject.tama.clinicvisits.service;

import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisitSummary;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllReportedOpportunisticInfections;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClinicVisitReportService {

    private final PatientService patientService;
    private final AllClinicVisits allClinicVisits;
    private final AllTreatmentAdvices allTreatmentAdvices;
    private final AllLabResults allLabResults;
    private final AllVitalStatistics allVitalStatistics;
    private final AllReportedOpportunisticInfections allReportedOpportunisticInfections;
    private final AllRegimens allRegimens;

    @Autowired
    public ClinicVisitReportService(PatientService patientService, AllClinicVisits allClinicVisits, AllTreatmentAdvices allTreatmentAdvices, AllLabResults allLabResults, AllVitalStatistics allVitalStatistics, AllReportedOpportunisticInfections allReportedOpportunisticInfections, AllRegimens allRegimens) {
        this.patientService = patientService;
        this.allClinicVisits = allClinicVisits;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allLabResults = allLabResults;
        this.allVitalStatistics = allVitalStatistics;
        this.allReportedOpportunisticInfections = allReportedOpportunisticInfections;
        this.allRegimens = allRegimens;
    }

    public List<ClinicVisitSummary> getClinicVisitReport(String patientId){

        List<ClinicVisitSummary> clinicVisitSummaries = new ArrayList<>();

        PatientReports patientReports = patientService.getPatientReports(patientId);

        for (String patientDocId : patientReports.getPatientDocIds()) {
            PatientReport patientReport = patientReports.getPatientReport(patientDocId);
            ClinicVisits clinicVisits = allClinicVisits.clinicVisits(patientDocId);
            TreatmentAdvice treatmentAdvice = allTreatmentAdvices.earliestTreatmentAdvice(patientDocId);
            String earlierTreatmentAdviceId = treatmentAdvice == null ? null : treatmentAdvice.getId();

            for(ClinicVisit clinicVisit: clinicVisits){

                if(clinicVisit.getVisitDate() == null)
                    continue;

                String currentTreatmentAdviceId = clinicVisit.getTreatmentAdviceId();

                if(currentTreatmentAdviceId == null)
                    currentTreatmentAdviceId = earlierTreatmentAdviceId;
                else
                    earlierTreatmentAdviceId = currentTreatmentAdviceId;

                if(currentTreatmentAdviceId == null)
                    continue;

                TreatmentAdvice treatmentAdvices = allTreatmentAdvices.get(currentTreatmentAdviceId);
                LabResults labResults = new LabResults(allLabResults.withIds(clinicVisit.getLabResultIds()));
                VitalStatistics vitalStatistics = allVitalStatistics.get(clinicVisit.getVitalStatisticsId());
                ReportedOpportunisticInfections reportedOpportunisticInfections = allReportedOpportunisticInfections.get(clinicVisit.getReportedOpportunisticInfectionsId());
                Regimen regimen = allRegimens.get(treatmentAdvices.getRegimenId());

                ClinicVisitSummary clinicVisitSummary = new ClinicVisitSummary(patientReport, clinicVisit, treatmentAdvices, labResults, vitalStatistics, reportedOpportunisticInfections, regimen);
                clinicVisitSummaries.add(clinicVisitSummary);
            }
        }

        return clinicVisitSummaries;
    }
}
