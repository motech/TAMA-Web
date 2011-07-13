package org.motechproject.tama.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.*;

public class DrugDosage extends BaseEntity {

    @NotNull
    private String drugId;

    @JsonIgnore
    private String drugName;

    @JsonIgnore
    private Set<Brand> brands;

    @NotNull
    private String brandId;

    @NotNull
    private String dosageTypeId;

    @NotNull
    private List<String> dosageSchedules = new ArrayList<String>();

    @NotNull
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = "dd/MM/yyyy")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = "dd/MM/yyyy")
    private Date endDate;

    private String advice;

    @NotNull
    private String mealAdviceId;

    public String getDrugId() {
        return this.drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

    public String getDrugName() {
        return this.drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public Set<Brand> getBrands() {
        return this.brands;
    }

    public void setBrands(Set<Brand> brands) {
        this.brands = brands;
    }

    public String getBrandId() {
        return this.brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getDosageTypeId() {
        return this.dosageTypeId;
    }

    public void setDosageTypeId(String dosageTypeId) {
        this.dosageTypeId = dosageTypeId;
    }

    public List<String> getDosageSchedules() {
        return dosageSchedules;
    }

    public void setDosageSchedules(List<String> dosageSchedules) {
        this.dosageSchedules = dosageSchedules;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        if (endDate == null) {
            Calendar instance = Calendar.getInstance();
            instance.setTime(startDate);
            instance.add(Calendar.YEAR, 1);
            endDate = instance.getTime();
        }
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getAdvice() {
        return this.advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public String getMealAdviceId() {
        return this.mealAdviceId;
    }

    public void setMealAdviceId(String mealAdviceId) {
        this.mealAdviceId = mealAdviceId;
    }
}
