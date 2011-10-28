package org.motechproject.tama.domain.ruleinferences;

import org.motechproject.tama.domain.MedicalCondition;

public class LowCD4Count extends PatientMedicalConditionInference {
    public LowCD4Count(MedicalCondition medicalCondition) {
        super(medicalCondition);
    }
}
