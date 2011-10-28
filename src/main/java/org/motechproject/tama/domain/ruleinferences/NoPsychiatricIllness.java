package org.motechproject.tama.domain.ruleinferences;

import org.motechproject.tama.domain.MedicalCondition;

public class NoPsychiatricIllness extends PatientMedicalConditionInference {
    public NoPsychiatricIllness(MedicalCondition medicalCondition) {
        super(medicalCondition);
    }
}
