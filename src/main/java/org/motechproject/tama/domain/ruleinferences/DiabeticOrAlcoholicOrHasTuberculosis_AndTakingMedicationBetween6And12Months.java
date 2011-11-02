package org.motechproject.tama.domain.ruleinferences;

import org.motechproject.tama.domain.MedicalCondition;

public class DiabeticOrAlcoholicOrHasTuberculosis_AndTakingMedicationBetween6And12Months extends PatientMedicalConditionInference {
    public DiabeticOrAlcoholicOrHasTuberculosis_AndTakingMedicationBetween6And12Months(MedicalCondition condition) {
        super(condition);
    }
}
