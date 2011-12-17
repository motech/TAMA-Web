package org.motechproject.tama.patient.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.domain.BaseEntity;
import org.motechproject.util.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.*;

public class DrugDosage extends BaseEntity {

    @NotNull
    private String drugId;

    @JsonIgnore
    private String drugName;

    @NotNull
    private String brandId;

    @NotNull
    private String dosageTypeId;

    private Integer offsetDays = 0;

    @NotNull
    private String morningTime;

    @NotNull
    private String eveningTime;

    @NotNull
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    private Date startDateAsDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
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

    @JsonIgnore
    public List<String> getDosageSchedules() {
        return Arrays.asList(morningTime, eveningTime);
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

    public Integer getOffsetDays() {
        return offsetDays;
    }

    public void setOffsetDays(Integer offsetDays) {
        this.offsetDays = offsetDays;
    }

    @JsonIgnore
    public List<String> getNonEmptyDosageSchedules() {
        return select(getDosageSchedules(), having(on(String.class), allOf(notNullValue(),
                not(equalToIgnoringWhiteSpace(StringUtils.EMPTY)))));
    }

    public static DrugDosage dosageStartingToday() {
        final DrugDosage dosage = new DrugDosage();
        dosage.setStartDate(DateUtil.today());
        return dosage;
    }

    public static class EndDateBasedComparator implements Comparator<DrugDosage> {
        @Override
        public int compare(DrugDosage o1, DrugDosage o2) {
            if (o1.getEndDate() == null) {
                return 1;
            }
            if (o2.getEndDate() == null) {
                return -1;
            }
            return o1.getEndDate().compareTo(o2.getEndDate());
        }
    }

    public static class StartDateBasedComparator implements Comparator<DrugDosage> {
        @Override
        public int compare(DrugDosage o1, DrugDosage o2) {
            if (o1.getStartDate() == null) {
                return 1;
            }
            if (o2.getStartDate() == null) {
                return -1;
            }
            return o1.getStartDate().compareTo(o2.getStartDate());
        }
    }
}
