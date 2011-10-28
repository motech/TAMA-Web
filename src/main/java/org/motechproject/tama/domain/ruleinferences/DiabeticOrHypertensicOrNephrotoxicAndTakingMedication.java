package org.motechproject.tama.domain.ruleinferences;

import org.motechproject.tama.domain.MedicalCondition;

public class DiabeticOrHypertensicOrNephrotoxicAndTakingMedication extends PatientMedicalConditionInference {
    public DiabeticOrHypertensicOrNephrotoxicAndTakingMedication(MedicalCondition condition) {
        super(condition);
    }
}
