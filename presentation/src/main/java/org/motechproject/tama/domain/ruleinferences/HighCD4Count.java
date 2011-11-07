package org.motechproject.tama.domain.ruleinferences;

import org.motechproject.tama.domain.MedicalCondition;

public class HighCD4Count extends PatientMedicalConditionInference {
    public HighCD4Count(MedicalCondition medicalCondition) {
        super(medicalCondition);
    }
}
