package org.motechproject.tama.domain;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.motechproject.util.DateUtil;

public class MedicalCondition {
    private String regimenName;
    private String gender;
    private int age;
    private int cd4Count;
    private boolean diabetic;
    private boolean hyperTensic;
    private boolean nephrotoxic;
    private LocalDate treatmentStartDate;
    private boolean lowBaselineHBCount;
    private boolean psychiatricIllness;
    private boolean alcoholic;
    private boolean tuberculosis;
    private double bmi;

    public String gender() {
        return gender;
    }

    public MedicalCondition gender(String gender) {
        this.gender = gender;
        return this;
    }

    public int age() {
        return age;
    }

    public MedicalCondition age(int age) {
        this.age = age;
        return this;
    }

    public int cd4Count() {
        return cd4Count;
    }

    public MedicalCondition cd4Count(int cd4Count) {
        this.cd4Count = cd4Count;
        return this;
    }

    public String regimenName() {
        return regimenName;
    }

    public MedicalCondition regimenName(String regimenId) {
        this.regimenName = regimenId;
        return this;
    }

    public boolean isDiabetic() {
        return diabetic;
    }

    public MedicalCondition diabetic(boolean diabetic) {
        this.diabetic = diabetic;
        return this;
    }

    public boolean isHyperTensic() {
        return hyperTensic;
    }

    public MedicalCondition hyperTensic(boolean hyperTensic) {
        this.hyperTensic = hyperTensic;
        return this;
    }

    public boolean isNephrotoxic() {
        return nephrotoxic;
    }

    public MedicalCondition nephrotoxic(boolean nephrotoxic) {
        this.nephrotoxic = nephrotoxic;
        return this;
    }

    public LocalDate treatmentStartDate() {
        return treatmentStartDate;
    }

    public MedicalCondition treatmentStartDate(LocalDate treatmentStartDate) {
        this.treatmentStartDate = treatmentStartDate;
        return this;
    }

    public int numberOfMonthsSinceTreatmentStarted() {
        return new Period(treatmentStartDate(), DateUtil.today(), PeriodType.months()).getMonths();
    }

    public boolean lowBaselineHBCount() {
        return lowBaselineHBCount;
    }

    public MedicalCondition lowBaselineHBCount(boolean lowBaselineHBCount) {
        this.lowBaselineHBCount = lowBaselineHBCount;
        return this;
    }

    public boolean psychiatricIllness() {
        return psychiatricIllness;
    }

    public MedicalCondition psychiatricIllness(boolean hasPsychiatricIllness) {
        this.psychiatricIllness = hasPsychiatricIllness;
        return this;
    }

    public boolean isAlcoholic() {
        return alcoholic;
    }

    public MedicalCondition alcoholic(boolean isAlcoholic) {
        this.alcoholic = isAlcoholic;
        return this;
    }

    public boolean isTuberculosis() {
        return tuberculosis;
    }

    public MedicalCondition tuberculosis(boolean hasTuberculosis) {
        this.tuberculosis = hasTuberculosis;
        return this;
    }

    public double bmi() {
        return bmi;
    }

    public MedicalCondition bmi(double bmi) {
        this.bmi = bmi;
        return this;
    }
}