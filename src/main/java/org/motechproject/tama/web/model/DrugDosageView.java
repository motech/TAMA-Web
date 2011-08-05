package org.motechproject.tama.web.model;

import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DrugDosageView extends TamaView {

    private String drugName;

    private String brandName;

    private String dosageType;

    private List<String> dosageSchedules = new ArrayList<String>();

    private Date startDateAsDate;

    private Date endDateAsDate;

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

    public LocalDate getStartDate() {
        return DateUtil.newDate(startDateAsDate);
    }

    public void setStartDate(LocalDate startDate) {
        this.startDateAsDate = toDate(startDate);
    }

    public Date getStartDateAsDate() {
        return startDateAsDate;
    }

    public void setStartDateAsDate(Date startDate) {
        this.startDateAsDate = startDate;
    }

    public LocalDate getEndDate() {
        return DateUtil.newDate(endDateAsDate);
    }

    public void setEndDate(LocalDate endDate) {
        this.endDateAsDate = toDate(endDate);
    }

    public Date getEndDateAsDate() {
        return endDateAsDate;
    }

    public void setEndDateAsDate(Date endDate) {
        this.endDateAsDate = endDate;
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
