package org.motechproject.tamadomain.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamacommon.domain.BaseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class NonHIVMedicalHistory extends BaseEntity {

    private List<AllergyHistory> allergiesHistory = new LinkedList<AllergyHistory>();

    private List<TAMAConstants.NNRTIRash> rashes = new ArrayList<TAMAConstants.NNRTIRash>();

    private List<SystemCategory> systemCategories = new LinkedList<SystemCategory>();

    private List<MedicalHistoryQuestion> questions = new LinkedList<MedicalHistoryQuestion>();

    public List<MedicalHistoryQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<MedicalHistoryQuestion> questions) {
        this.questions = questions;
    }

    public List<AllergyHistory> getAllergiesHistory() {
        return allergiesHistory;
    }

    public void setAllergiesHistory(List<AllergyHistory> allergiesHistory) {
        this.allergiesHistory = allergiesHistory;
    }

    public List<TAMAConstants.NNRTIRash> getRashes() {
        return rashes;
    }

    public void setRashes(List<TAMAConstants.NNRTIRash> rashes) {
        this.rashes = rashes;
    }

    public void setSystemCategories(List<SystemCategory> systemCategories) {
        this.systemCategories = systemCategories;
    }

    public List<AllergyHistory> getSpecifiedAllergies() {
        ArrayList<AllergyHistory> specifiedAllergies = new ArrayList<AllergyHistory>();
        for (AllergyHistory allergyHistory : allergiesHistory) {
            if (allergyHistory.isSpecified())
                specifiedAllergies.add(allergyHistory);
        }
        return specifiedAllergies;
    }

    public NonHIVMedicalHistory addSystemCategory(SystemCategory... systemCategory) {
        systemCategories.addAll(Arrays.asList(systemCategory));
        return this;
    }

    public List<SystemCategory> getSystemCategories() {
        return systemCategories;
    }

    @JsonIgnore
    public Ailments getAilments(SystemCategoryDefinition systemCategoryDefiniton) {
        for (SystemCategory systemCategory : systemCategories) {
            if (systemCategory.getName().equals(systemCategoryDefiniton.getCategoryName()))
                return systemCategory.getAilments();
        }
        return null;
    }
}
