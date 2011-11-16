package org.motechproject.tama.web.model;

import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;

import java.util.Date;

public class DrugDosageView extends TamaView {

    private String drugName;

    private String brandName;

    private String dosageType;

    private String morningTime;
    
    private String eveningTime;

    private Date startDateAsDate;

    private String advice;

    private String mealAdviceType;
    
    private Integer offsetDays;

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

	public Integer getOffsetDays() {
		return offsetDays;
	}

	public void setOffsetDays(Integer offsetDays) {
		this.offsetDays = offsetDays;
	}

	public String getMorningTime() {
		return morningTime;
	}

	public void setMorningTime(String morningTime) {
		this.morningTime = morningTime;
	}

	public String getEveningTime() {
		return eveningTime;
	}

	public void setEveningTime(String eveningTime) {
		this.eveningTime = eveningTime;
	}
}
