package org.motechproject.tama.mapper;

import org.motechproject.tama.domain.*;
import org.motechproject.util.DateUtil;

import java.util.List;

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

    public MedicalCondition map() {
        MedicalCondition medicalCondition = new MedicalCondition();

        medicalCondition.regimenName(regimen.getName());
        medicalCondition.gender(patient.getGender().getType());
        medicalCondition.age(patient.getAge());
        medicalCondition.cd4Count(labResults.latestCD4Count());
        medicalCondition.diabetic(hasHistoryOfOtherSystemCategoryAilment(AilmentDefinition.Diabetes));
        medicalCondition.hyperTensic(hasHistoryOfOtherSystemCategoryAilment(AilmentDefinition.Hypertension));
        medicalCondition.nephrotoxic(hasHistoryOfOtherSystemCategoryAilment(AilmentDefinition.Nephrotoxicity));
        medicalCondition.artRegimenStartDate(DateUtil.newDate(treatmentAdvice.getStartDate()));
        medicalCondition.lowBaselineHBCount(hasBaselineHBLowerThan10());
        medicalCondition.psychiatricIllness(hasHistoryOfPsychiatricIllness());

        return medicalCondition;
    }

    private boolean hasHistoryOfOtherSystemCategoryAilment(AilmentDefinition ailmentDefinition) {
        Ailments otherSystemCategoryAilments = patient.getMedicalHistory().getNonHivMedicalHistory().getAilments(SystemCategoryDefinition.Other);
        return otherSystemCategoryAilments.getAilment(ailmentDefinition).everHadTheAilment();
    }

    private boolean hasHistoryOfPsychiatricIllness() {
        List<SystemCategory> systemCategories = patient.getMedicalHistory().getNonHivMedicalHistory().getSystemCategories();
        for(SystemCategory category : systemCategories) {
            if(SystemCategoryDefinition.Psychiatric.getCategoryName().equals(category.getName()))
                return category.getAilments().getOtherAilments().get(0).everHadTheAilment();
        }
        return false;
    }

    private boolean hasBaselineHBLowerThan10() {
        List<MedicalHistoryQuestion> medicalHistoryQuestions = patient.getMedicalHistory().getNonHivMedicalHistory().getQuestions();
        for(MedicalHistoryQuestion question: medicalHistoryQuestions) {
            if(MedicalHistoryQuestions.baseLinePretherapy().getQuestion().equals(question.getQuestion())) {
                return question.isHistoryPresent();
            }
        }
        return false;
    }
}
