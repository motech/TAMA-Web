package org.motechproject.tama.domain.ruleinferences;

import org.motechproject.tama.domain.MedicalCondition;

public class Not__DiabeticOrHypertensicOrNephrotoxicAndTakingMedication extends PatientMedicalConditionInference {
    public Not__DiabeticOrHypertensicOrNephrotoxicAndTakingMedication(MedicalCondition condition) {
        super(condition);
    }
}
