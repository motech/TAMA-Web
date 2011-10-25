package org.motechproject.tama.mapper;

import org.motechproject.tama.domain.*;
import org.motechproject.util.DateUtil;

public class PatientMedicalConditionsMapper {
    private Patient patient;
    private LabResults labResults;
    private TreatmentAdvice treatmentAdvice;
    private Regimen regimen;

    public PatientMedicalConditionsMapper(Patient patient, LabResults labResults, TreatmentAdvice treatmentAdvice, Regimen regimen) {
        this.patient = patient;
        this.labResults = labResults;
        this.treatmentAdvice = treatmentAdvice;
        this.regimen = regimen;
    }

    public PatientMedicalConditions map() {
        PatientMedicalConditions patientMedicalConditions = new PatientMedicalConditions();

        patientMedicalConditions.setRegimenName(regimen.getName());
        patientMedicalConditions.setGender(patient.getGender().getType());
        patientMedicalConditions.setAge(patient.getAge());
        patientMedicalConditions.setCd4Count(labResults.latestCD4Count());
        patientMedicalConditions.setDiabetic(hasHistoryOfOtherSystemCategoryAilment(AilmentDefinition.Diabetes));
        patientMedicalConditions.setHyperTensic(hasHistoryOfOtherSystemCategoryAilment(AilmentDefinition.Hypertension));
        patientMedicalConditions.setNephrotoxic(hasHistoryOfOtherSystemCategoryAilment(AilmentDefinition.Nephrotoxicity));
        patientMedicalConditions.setArtRegimenStartDate(DateUtil.newDate(treatmentAdvice.getStartDate()));

        return patientMedicalConditions;
    }

    private boolean hasHistoryOfOtherSystemCategoryAilment(AilmentDefinition ailmentDefinition) {
        Ailments otherSystemCategoryAilments = patient.getMedicalHistory().getNonHivMedicalHistory().getAilments(SystemCategoryDefiniton.Other);
        return otherSystemCategoryAilments.getAilment(ailmentDefinition).everHadTheAilment();
    }
}
