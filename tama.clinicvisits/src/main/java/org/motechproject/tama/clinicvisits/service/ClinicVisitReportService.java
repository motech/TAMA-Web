package org.motechproject.tama.clinicvisits.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisitSummary;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.contract.DrugDosageContract;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllReportedOpportunisticInfections;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.refdata.domain.DrugCompositionGroup;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private final AllDosageTypes allDosageTypes;
    private final AllMealAdviceTypes allMealAdviceTypes;
    private final AllOpportunisticInfections allOpportunisticInfections;
    private final AllDrugs allDrugs;

    @Autowired
    public ClinicVisitReportService(PatientService patientService, AllClinicVisits allClinicVisits, AllTreatmentAdvices allTreatmentAdvices, AllLabResults allLabResults, AllVitalStatistics allVitalStatistics, AllReportedOpportunisticInfections allReportedOpportunisticInfections, AllRegimens allRegimens, AllDosageTypes allDosageTypes, AllMealAdviceTypes allMealAdviceTypes, AllOpportunisticInfections allOpportunisticInfections, AllDrugs allDrugs) {
        this.patientService = patientService;
        this.allClinicVisits = allClinicVisits;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allLabResults = allLabResults;
        this.allVitalStatistics = allVitalStatistics;
        this.allReportedOpportunisticInfections = allReportedOpportunisticInfections;
        this.allRegimens = allRegimens;
        this.allDosageTypes = allDosageTypes;
        this.allMealAdviceTypes = allMealAdviceTypes;
        this.allOpportunisticInfections = allOpportunisticInfections;
        this.allDrugs = allDrugs;
    }

    public List<ClinicVisitSummary> getClinicVisitReport(String patientId){

        List<ClinicVisitSummary> clinicVisitSummaries = new ArrayList<>();

        PatientReports patientReports = patientService.getPatientReports(patientId);

        for (String patientDocId : patientReports.getPatientDocIds()) {
            PatientReport patientReport = patientReports.getPatientReport(patientDocId);
            ClinicVisits clinicVisits = allClinicVisits.clinicVisits(patientDocId);
            TreatmentAdvice treatmentAdvice = allTreatmentAdvices.earliestTreatmentAdvice(patientDocId);
            String previousTreatmentAdviceId = treatmentAdvice == null ? null : treatmentAdvice.getId();

            clinicVisits = removeUnvisitedClinicVisits(clinicVisits);
            sortClinicVisitsByVisitDate(clinicVisits);

            for(ClinicVisit clinicVisit: clinicVisits){
                DateTime visitDate = clinicVisit.getVisitDate();

                String currentTreatmentAdviceId = clinicVisit.getTreatmentAdviceId();

                if(currentTreatmentAdviceId == null && previousTreatmentAdviceId == null)
                    continue;

                if(currentTreatmentAdviceId == null)
                    currentTreatmentAdviceId = previousTreatmentAdviceId;

                previousTreatmentAdviceId = currentTreatmentAdviceId;


                TreatmentAdvice currentTreatmentAdvice = allTreatmentAdvices.get(currentTreatmentAdviceId);
                LabResults labResults = clinicVisit.getLabResultIds() == null ? null : new LabResults(allLabResults.withIds(clinicVisit.getLabResultIds()));
                VitalStatistics vitalStatistics = clinicVisit.getVitalStatisticsId() == null ? null : allVitalStatistics.get(clinicVisit.getVitalStatisticsId());
                ReportedOpportunisticInfections reportedOpportunisticInfections = clinicVisit.getReportedOpportunisticInfectionsId() == null ? null :allReportedOpportunisticInfections.get(clinicVisit.getReportedOpportunisticInfectionsId());
                Regimen regimen = allRegimens.get(currentTreatmentAdvice.getRegimenId());

                DrugCompositionGroup drugCompositionGroup = regimen.getDrugCompositionGroupFor(currentTreatmentAdvice.getDrugCompositionGroupId());
                String drugCompositionGroupName = drugCompositionGroup.getName();

                List<DrugDosageContract> dosages = new ArrayList<>();
                for (DrugDosage dosage : currentTreatmentAdvice.getDrugDosages()) {
                    DrugDosageContract drugDosage = getDrugDosage(dosage);
                    dosages.add(drugDosage);
                }

                DrugDosageContract dosage1 = dosages.size() > 0 ? dosages.get(0) : null;
                DrugDosageContract dosage2 = dosages.size() > 1 ? dosages.get(1) : null;

                String opportunisticInfections = getOpportunisticInfections(reportedOpportunisticInfections);


                ClinicVisitSummary clinicVisitSummary = new ClinicVisitSummary(patientReport, visitDate, labResults, vitalStatistics, opportunisticInfections, regimen, drugCompositionGroupName, dosage1, dosage2);
                clinicVisitSummaries.add(clinicVisitSummary);
            }
        }

        return clinicVisitSummaries;
    }

    private void sortClinicVisitsByVisitDate(ClinicVisits clinicVisits) {
        Collections.sort(clinicVisits, new Comparator<ClinicVisit>() {
            public int compare(ClinicVisit cv1, ClinicVisit cv2) {
                return cv1.getVisitDate().compareTo(cv2.getVisitDate());
            }
        });
    }

    private ClinicVisits removeUnvisitedClinicVisits(ClinicVisits clinicVisits) {

        ClinicVisits filteredClinicVisits = new ClinicVisits();
        for(ClinicVisit clinicVisit: clinicVisits){
            DateTime visitDate = clinicVisit.getVisitDate();
            if(visitDate == null)
                continue;
            else
                filteredClinicVisits.add(clinicVisit);
        }
        return filteredClinicVisits;
    }

    private DrugDosageContract getDrugDosage(DrugDosage dosage) {
        DrugDosageContract drugDosage = new DrugDosageContract();
        drugDosage.setDrugName(allDrugs.get(dosage.getDrugId()).getName());
        drugDosage.setDosageType(allDosageTypes.get(dosage.getDosageTypeId()).getType());
        drugDosage.setMorningTime(dosage.getMorningTime());
        drugDosage.setEveningTime(dosage.getEveningTime());
        drugDosage.setOffsetDays(dosage.getOffsetDays());
        drugDosage.setStartDate(dosage.getStartDateAsDate());
        drugDosage.setAdvice(dosage.getAdvice());
        drugDosage.setMealAdvice(allMealAdviceTypes.get(dosage.getMealAdviceId()).getType());
        return drugDosage;
    }

    private String getOpportunisticInfections(ReportedOpportunisticInfections reportedOpportunisticInfections) {
        if(reportedOpportunisticInfections == null)
            return StringUtils.EMPTY;

        List<String> opportunisticInfections = new ArrayList<>();
        for (String infectionId : reportedOpportunisticInfections.getOpportunisticInfectionIds()) {
            opportunisticInfections.add(allOpportunisticInfections.get(infectionId).getName());
        }
        String infections = StringUtils.join(opportunisticInfections, ",");

        String otherOpportunisticInfectionDetails = reportedOpportunisticInfections.getOtherOpportunisticInfectionDetails();
        if(StringUtils.isNotBlank(otherOpportunisticInfectionDetails)){
            infections = infections + " (" + otherOpportunisticInfectionDetails + ")";
        }
        return infections;
    }
}
