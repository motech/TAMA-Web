package org.motechproject.tama.domain.ruleinferences;

import org.motechproject.tama.domain.MedicalCondition;

public class DiabeticOrHypertensicOrNephrotoxic_AndTakingMedication_AndAboveMiddleAge extends PatientMedicalConditionInference {
    public DiabeticOrHypertensicOrNephrotoxic_AndTakingMedication_AndAboveMiddleAge(MedicalCondition condition) {
        super(condition);
    }
}
