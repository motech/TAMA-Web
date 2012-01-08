package org.motechproject.tama.patient.domain;

import ch.lambdaj.Lambda;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.util.DateUtil;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.*;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.hasItem;

@TypeDiscriminator("doc.documentType == 'TreatmentAdvice'")
public class TreatmentAdvice extends CouchEntity implements Comparable<TreatmentAdvice> {
    @NotNull
    private String patientId;

    @NotNull
    private String regimenId;

    @NotNull
    private String drugCompositionId;

    @NotNull
    private String drugCompositionGroupId;

    @NotNull
    private String reasonForDiscontinuing;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<DrugDosage> drugDosages = new ArrayList<DrugDosage>();

    public String getPatientId() {
        return this.patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getRegimenId() {
        return this.regimenId;
    }

    public void setRegimenId(String regimenId) {
        this.regimenId = regimenId;
    }

    public String getDrugCompositionId() {
        return this.drugCompositionId;
    }

    public void setDrugCompositionId(String drugCompositionId) {
        this.drugCompositionId = drugCompositionId;
    }

    public List<DrugDosage> getDrugDosages() {
        return this.drugDosages;
    }

    public void setDrugDosages(List<DrugDosage> drugDosages) {
        this.drugDosages = drugDosages;
    }

    public void addDrugDosage(DrugDosage drugDosage) {
        this.drugDosages.add(drugDosage);
    }

    public String getDrugCompositionGroupId() {
        return drugCompositionGroupId;
    }

    public void setDrugCompositionGroupId(String drugCompositionGroupId) {
        this.drugCompositionGroupId = drugCompositionGroupId;
    }

    public String getReasonForDiscontinuing() {
        return reasonForDiscontinuing;
    }

    public void setReasonForDiscontinuing(String reasonForDiscontinuing) {
        this.reasonForDiscontinuing = reasonForDiscontinuing;
    }

    public static TreatmentAdvice newDefault() {
        TreatmentAdvice advice = new TreatmentAdvice();
        advice.setDrugDosages(Arrays.asList(DrugDosage.dosageStartingToday(), DrugDosage.dosageStartingToday()));
        return advice;
    }

    public void endTheRegimen(String discontinuationReason) {
        setReasonForDiscontinuing(discontinuationReason);
        for (DrugDosage dosage : getDrugDosages()) {
            dosage.setEndDate(DateUtil.today());
        }
    }

    public boolean hasMultipleDosages() {
        LocalDate today = DateUtil.today();
        int count = 0;

        Map<String, List<DrugDosage>> groupedDosagesByTime = groupDosagesByTime();
        for (String key : groupedDosagesByTime.keySet()) {
            boolean isDoseValidToday = false;
            for (DrugDosage dosage : groupedDosagesByTime.get(key)) {
                int offsetDays = key.equals(dosage.getEveningTime()) ? dosage.getOffsetDays() : 0;
                if (!today.isBefore(dosage.getStartDate().plusDays(offsetDays))) {
                    isDoseValidToday = true;
                    break;
                }
            }
            if (isDoseValidToday) count++;
        }
        return (count > 1);
    }

    @JsonIgnore
    public Date getEndDate() {
        DrugDosage dosageWithMaxEndDate = Collections.max(getDrugDosages(), new DrugDosage.EndDateBasedComparator());
        return dosageWithMaxEndDate.getEndDateAsDate();
    }

    @JsonIgnore
    public Date getStartDate() {
        DrugDosage dosageWithMinStartDate = Collections.min(getDrugDosages(), new DrugDosage.StartDateBasedComparator());
        return dosageWithMinStartDate.getStartDateAsDate();
    }

    @JsonIgnore
    public Map<String, List<DrugDosage>> groupDosagesByTime() {
        Map<String, List<DrugDosage>> drugDosagesGroupedAccordingToTime = new HashMap<String, List<DrugDosage>>();
        Collection<String> distinctTimes = selectDistinct(Lambda.<String>flatten(extract(getDrugDosages(), on(DrugDosage.class).getNonEmptyDosageSchedules())));
        for (String time : distinctTimes) {
            drugDosagesGroupedAccordingToTime.put(time, select(getDrugDosages(), having(on(DrugDosage.class).getNonEmptyDosageSchedules(), hasItem(time))));
        }
        return drugDosagesGroupedAccordingToTime;
    }

    @Override
    public int compareTo(TreatmentAdvice treatmentAdvice) {
        return getStartDate().compareTo(treatmentAdvice.getStartDate());
    }

    public LocalDate getStartDateForWeek(LocalDate date, Patient patient) {
        DayOfWeek preferredCallDay = patient.getPatientPreferences().getDayOfWeeklyCall();
        int retryDayCount = 0;
        boolean isRetry = date.getDayOfWeek() != preferredCallDay.getValue();
        if (isRetry) retryDayCount = DateUtil.daysPast(date, preferredCallDay);

        DayOfWeek treatmentAdviceStartDay = DayOfWeek.getDayOfWeek(DateUtil.newDate(getStartDate()));
        return dateWith(treatmentAdviceStartDay, PatientPreferences.DAYS_TO_RECALL, date.minusDays(retryDayCount));
    }

    private LocalDate dateWith(DayOfWeek dayOfWeek, int minNumberOfDaysAgo, LocalDate maxDate) {
        LocalDate date = dateWith(dayOfWeek, maxDate);

        Period period = new Period(date, maxDate, PeriodType.days());
        if (period.getDays() >= minNumberOfDaysAgo) return date;

        return dateWith(dayOfWeek, date);
    }

    private LocalDate dateWith(DayOfWeek dayOfWeek, LocalDate maxDate) {
        LocalDate returnDate = maxDate.withDayOfWeek(dayOfWeek.getValue());
        boolean dateAfterMaxDate = returnDate.compareTo(maxDate) >= 0;
        if (dateAfterMaxDate) {
            returnDate = returnDate.minusWeeks(1);
        }
        return returnDate;
    }
}