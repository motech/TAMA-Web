package org.motechproject.tama.domain.ruleinferences;

import org.motechproject.tama.domain.MedicalCondition;

public class HasPsychiatricIllness extends PatientMedicalConditionInference {
    public HasPsychiatricIllness(MedicalCondition medicalCondition) {
        super(medicalCondition);
    }
}
