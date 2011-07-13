package org.motechproject.tama.web.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DrugDosageView {

    private String drugName;

    private String brandName;

    private String dosageType;

    private List<String> dosageSchedules = new ArrayList<String>();

    private Date startDate;

    private Date endDate;

    private String advice;

    private String mealAdviceType;

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getDosageType() {
        return dosageType;
    }

    public void setDosageType(String dosageType) {
        this.dosageType = dosageType;
    }

    public List<String> getDosageSchedules() {
        return dosageSchedules;
    }

    public void setDosageSchedules(List<String> dosageSchedules) {
        this.dosageSchedules = dosageSchedules;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public String getMealAdviceType() {
        return mealAdviceType;
    }

    public void setMealAdviceType(String mealAdviceType) {
        this.mealAdviceType = mealAdviceType;
    }
}
