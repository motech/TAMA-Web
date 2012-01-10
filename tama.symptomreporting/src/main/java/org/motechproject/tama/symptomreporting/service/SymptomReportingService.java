package org.motechproject.tama.symptomreporting.service;

import org.motechproject.tama.ivr.decisiontree.domain.MedicalCondition;
import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.*;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.motechproject.tama.symptomreporting.mapper.MedicalConditionsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SymptomReportingService {
    private AllPatients allPatients;
    private AllVitalStatistics allVitalStatistics;
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllLabResults allLabResults;
    private AllRegimens allRegimens;

    @Autowired
    public SymptomReportingService(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, AllLabResults allLabResults, AllRegimens allRegimens, AllUniquePatientFields allUniquePatientFields, AllVitalStatistics allVitalStatistics) {
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allLabResults = allLabResults;
        this.allRegimens = allRegimens;
        this.allVitalStatistics = allVitalStatistics;
    }

    public MedicalCondition getPatientMedicalConditions(String patientId) {
        Patient patient = allPatients.get(patientId);
        LabResults labResults = allLabResults.findLatestLabResultsByPatientId(patientId);
        VitalStatistics vitalStatistics = allVitalStatistics.findLatestVitalStatisticByPatientId(patientId);
        TreatmentAdvice earliestTreatmentAdvice = allTreatmentAdvices.earliestTreatmentAdvice(patientId);
        TreatmentAdvice currentTreatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        Regimen currentRegimen = allRegimens.get(currentTreatmentAdvice.getRegimenId());

        return new MedicalConditionsMapper(patient, labResults, vitalStatistics, earliestTreatmentAdvice, currentRegimen).map();
    }
}
