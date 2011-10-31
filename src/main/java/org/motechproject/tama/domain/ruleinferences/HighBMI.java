package org.motechproject.tama.domain.ruleinferences;

import org.motechproject.tama.domain.MedicalCondition;

public class HighBMI extends PatientMedicalConditionInference {
    public HighBMI(MedicalCondition medicalCondition) {
        super(medicalCondition);
    }
}
