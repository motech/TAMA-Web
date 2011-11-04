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

    public MedicalConditionBuilder ForRegimen1(){
        medicalCondition.regimenName("Regimen I");
        return this;
    }

    public MedicalConditionBuilder ForRegimen3(){
        medicalCondition.regimenName("Regimen III");
        return this;
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
        medicalCondition.bmi(27.3);
        return this;
    }

    public MedicalConditionBuilder LowBMI(){
        medicalCondition.bmi(10);
        return this;
    }

    public MedicalConditionBuilder AboveMiddleAge(){
        medicalCondition.age(60);
        return this;
    }

    public MedicalConditionBuilder BelowMiddleAge(){
        medicalCondition.age(50);
        return this;
    }

    public MedicalConditionBuilder HighBaselineHBCount(){
        medicalCondition.lowBaselineHBCount(false);
        return this;
    }

    public MedicalConditionBuilder LowBaselineHBCount(){
        medicalCondition.lowBaselineHBCount(true);
        return this;
    }

    public MedicalConditionBuilder NoHistoryOfMedicalConditions(){
        medicalCondition.tuberculosis(false).diabetic(false).alcoholic(false);
        return this;
    }

    public MedicalConditionBuilder HistoryOfTuberculosis(){
        medicalCondition.tuberculosis(true);
        return this;
    }

    public MedicalConditionBuilder HistoryOfDiabetes(){
        medicalCondition.diabetic(true);
        return this;
    }

    public MedicalConditionBuilder AdviceIsWithin6Months(){
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
