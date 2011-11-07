package org.motechproject.tama.domain.ruleinferences;

import org.motechproject.tama.domain.MedicalCondition;

public class LowBaselineHBCount extends PatientMedicalConditionInference {
    public LowBaselineHBCount(MedicalCondition medicalCondition) {
        super(medicalCondition);
    }
}
