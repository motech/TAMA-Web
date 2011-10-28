package org.motechproject.tama.domain.ruleinferences;

import org.motechproject.tama.domain.MedicalCondition;

public class BelowMiddleAge extends PatientMedicalConditionInference {
    public BelowMiddleAge(MedicalCondition medicalCondition) {
        super(medicalCondition);
    }
}
