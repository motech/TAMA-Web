package org.motechproject.tama.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

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
    private Set<Brand> brands = new TreeSet<Brand>();

    @NotNull
    private String brandId;

    @NotNull
    private String dosageTypeId;

    @NotNull
    private List<String> dosageSchedules = new ArrayList<String>();

    @NotNull
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = "dd/MM/yyyy")
    private Date startDateAsDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = "dd/MM/yyyy")
    private Date endDateAsDate;

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
        this.brands = new TreeSet<Brand>(brands);
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

    public LocalDate getStartDate() {
        return DateUtil.newDate(startDateAsDate);
    }

    public void setStartDate(LocalDate startDate) {
        this.startDateAsDate = toDate(startDate);
    }

    @JsonIgnore
    public Date getStartDateAsDate() {
        if (startDateAsDate == null) {
            this.startDateAsDate = toDate(DateUtil.today());
        }
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

    @JsonIgnore
    public Date getEndDateAsDate() {
        return endDateAsDate;
    }

    public void setEndDateAsDate(Date endDate) {
        this.endDateAsDate = endDate;
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
