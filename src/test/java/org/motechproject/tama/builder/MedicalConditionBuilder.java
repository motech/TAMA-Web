package org.motechproject.tama.builder;

import org.motechproject.tama.domain.MedicalCondition;
import org.motechproject.util.DateUtil;

public class MedicalConditionBuilder {

    private MedicalCondition medicalCondition;

    private MedicalConditionBuilder() {
        this.medicalCondition = new MedicalCondition();
    }

    public static MedicalConditionBuilder startRecording(){
        return new MedicalConditionBuilder();
    }

    public MedicalConditionBuilder ForRegimen5(){
        medicalCondition.regimenName("Regimen V");
        return this;
    }

    public MedicalConditionBuilder FemaleWithHighBMI(){
        Female();
        HighBMI();
        return this;
    }

    public MedicalConditionBuilder Male(){
        medicalCondition.gender("Male");
        return this;
    }

    public MedicalConditionBuilder Female(){
        medicalCondition.gender("Female");
        return this;
    }

    public MedicalConditionBuilder HighBMI(){
        medicalCondition.bmi(60);
        return this;
    }

    public MedicalConditionBuilder LowBMI(){
        medicalCondition.bmi(10);
        return this;
    }

    public MedicalConditionBuilder NoHistoryOfMedicalConditions(){
        medicalCondition.tuberculosis(false).diabetic(false).alcoholic(false);
        return this;
    }

    public MedicalConditionBuilder HistoryOfTuberculosis(){
        medicalCondition.tuberculosis(true).diabetic(false).alcoholic(false);
        return this;
    }

    public MedicalConditionBuilder AdviceIsNotWithin6And12Months(){
        medicalCondition.treatmentStartDate(DateUtil.today().minusMonths(1));
        return this;
    }

    public MedicalConditionBuilder AdviceIsWithin6And12Months(){
        medicalCondition.treatmentStartDate(DateUtil.today().minusMonths(7));
        return this;
    }

    public MedicalCondition build() {
        return medicalCondition;
    }
}
