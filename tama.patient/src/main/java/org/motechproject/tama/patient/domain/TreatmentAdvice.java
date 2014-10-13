package org.motechproject.tama.patient.domain;

import ch.lambdaj.Lambda;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.util.DateUtil;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import java.util.*;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.hasItem;
import static org.joda.time.Days.daysBetween;
import static org.motechproject.util.DateUtil.newDate;

@TypeDiscriminator("doc.documentType == 'TreatmentAdvice'")
public class TreatmentAdvice extends CouchEntity implements Comparable<TreatmentAdvice> {

    public static final int DAYS_IN_FIVE_WEEKS = 35;

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
    public boolean hasAdherenceTrend(LocalDate reference) {
        return daysBetween(newDate(this.getStartDate()), reference).getDays() >= DAYS_IN_FIVE_WEEKS;
    }

    @JsonIgnore
    public Map<String, List<DrugDosage>> groupDosagesByTime() {
        Map<String, List<DrugDosage>> drugDosagesGroupedAccordingToTime = new HashMap<String, List<DrugDosage>>();
        for (String time : distinctDrugTimes()) {
            drugDosagesGroupedAccordingToTime.put(time, select(getDrugDosages(), having(on(DrugDosage.class).getNonEmptyDosageSchedules(), hasItem(time))));
        }
        return drugDosagesGroupedAccordingToTime;
    }

    @JsonIgnore
    public List<String> distinctDrugTimes() {
        return Arrays.asList(selectDistinct(Lambda.<String>flatten(extract(getDrugDosages(), on(DrugDosage.class).getNonEmptyDosageSchedules()))).toArray(new String[]{}));
    }

    @Override
    public int compareTo(TreatmentAdvice treatmentAdvice) {
        return getStartDate().compareTo(treatmentAdvice.getStartDate());
    }
    
    public static void main(String[] args) {

    	LocalDate date1 = new LocalDate(2013, 1, 1);
    	LocalDate date2 = new LocalDate(2014, 1, 1);
    	DrugDosage dosage1 = new DrugDosage();
    	dosage1.setStartDate(date1);
    	DrugDosage dosage2 = new DrugDosage();
    	dosage2.setStartDate(date2);
    	System.out.println(date1);
    	System.out.println(date2);
    	List<DrugDosage> list = new ArrayList<>();
    	list.add(dosage1);
    	list.add(dosage2);
    	DrugDosage dosageWithMinStartDate = Collections.min(list, new DrugDosage.StartDateBasedComparator());
    	System.out.println(dosageWithMinStartDate.getStartDate());
	}
}