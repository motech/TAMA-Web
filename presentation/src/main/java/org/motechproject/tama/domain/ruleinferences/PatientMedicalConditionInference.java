package org.motechproject.tama.domain.ruleinferences;

import org.motechproject.tama.domain.MedicalCondition;

public abstract class PatientMedicalConditionInference {
    private MedicalCondition condition;

    public PatientMedicalConditionInference(MedicalCondition condition) {
        this.condition = condition;
    }

    public MedicalCondition getCondition() {
        return condition;
    }

    public void setCondition(MedicalCondition condition) {
        this.condition = condition;
    }
}
