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
    private LocalDate artRegimenStartDate;

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

    public LocalDate artRegimenStartDate() {
        return artRegimenStartDate;
    }

    public MedicalCondition artRegimenStartDate(LocalDate artRegimenStartDate) {
        this.artRegimenStartDate = artRegimenStartDate;
        return this;
    }

    public int numberOfMonthsSinceRegimenStarted() {
        return new Period(artRegimenStartDate(), DateUtil.today(), PeriodType.months()).getMonths();
    }
}