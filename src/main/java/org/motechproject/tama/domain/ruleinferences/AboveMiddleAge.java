package org.motechproject.tama.domain.ruleinferences;

import org.motechproject.tama.domain.MedicalCondition;

public class AboveMiddleAge extends PatientMedicalConditionInference {
    public AboveMiddleAge(MedicalCondition medicalCondition) {
        super(medicalCondition);
    }
}
