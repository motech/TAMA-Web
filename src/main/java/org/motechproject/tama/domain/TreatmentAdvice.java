package org.motechproject.tama.domain;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.selectDistinct;
import static org.hamcrest.Matchers.hasItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;

import ch.lambdaj.Lambda;

@TypeDiscriminator("doc.documentType == 'TreatmentAdvice'")
public class TreatmentAdvice extends CouchEntity {
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

    public void endTheRegimen() {
        for (DrugDosage dosage : getDrugDosages()) {
            dosage.setEndDate(DateUtil.today());
        }
    }

    public boolean hasMultipleDosages() {
    	LocalDate today = DateUtil.today();
    	int count = 0;
    	
        Map<String, List<DrugDosage>> groupedDosagesByTime = groupDosagesByTime();
    	for (String key : groupedDosagesByTime.keySet()){
    		boolean isDoseValidToday = false;
    		for (DrugDosage dosage :groupedDosagesByTime.get(key)) {
    			int offsetDays = key.equals(dosage.getEveningTime())?dosage.getOffsetDays():0;
    			if (!today.isBefore(dosage.getStartDate().plusDays(offsetDays))) { isDoseValidToday = true; break;}
    		}
    		if (isDoseValidToday) count++;
    	} 
		return (count > 1);
    }

//    public Map<String, List<DrugDosage>> dosagesMap() {
//        Map<String, List<DrugDosage>> drugDosagesMap = new HashMap<String, List<DrugDosage>>();
//        for (DrugDosage drug : getDrugDosages()) {
//            List<String> dosageSchedules = drug.getDosageSchedules();
//            filter(dosageSchedules, new Predicate() {
//                @Override
//                public boolean evaluate(Object o) {
//                    String dosageSchedule = (String) o;
//                    return StringUtils.isNotBlank(dosageSchedule);
//                }
//            });
//            for (String dosageSchedule : dosageSchedules) {
//                List<DrugDosage> drugList = getExistingDrugsForDosageSchedule(drugDosagesMap, dosageSchedule);
//                drugList.add(drug);
//                drugDosagesMap.put(dosageSchedule, drugList);
//            }
//        }
//        return drugDosagesMap;
//    }

//    private List<DrugDosage> getExistingDrugsForDosageSchedule(Map<String, List<DrugDosage>> drugDosagesMap, String dosageSchedule) {
//        List<DrugDosage> drugList = drugDosagesMap.get(dosageSchedule);
//        return drugList == null ? new ArrayList<DrugDosage>() : drugList;
//    }


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
}