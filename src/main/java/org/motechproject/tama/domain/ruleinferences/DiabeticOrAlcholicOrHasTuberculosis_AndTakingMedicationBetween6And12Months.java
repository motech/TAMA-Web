package org.motechproject.tama.domain.ruleinferences;

import org.motechproject.tama.domain.MedicalCondition;

public class DiabeticOrAlcholicOrHasTuberculosis_AndTakingMedicationBetween6And12Months extends PatientMedicalConditionInference {
    public DiabeticOrAlcholicOrHasTuberculosis_AndTakingMedicationBetween6And12Months(MedicalCondition condition) {
        super(condition);
    }
}
