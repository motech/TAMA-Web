package org.motechproject.tama.patient.builder;

import org.joda.time.LocalDate;
import org.motechproject.tama.common.util.UUIDUtil;
import org.motechproject.tama.patient.domain.DrugDosage;
import org.motechproject.tama.patient.domain.TreatmentAdvice;

import java.util.Arrays;

public class TreatmentAdviceBuilder {

    private TreatmentAdvice treatmentAdvice = new TreatmentAdvice();

    public TreatmentAdviceBuilder withId(String id) {
        this.treatmentAdvice.setId(id);
        return this;
    }

    public TreatmentAdviceBuilder withDrugCompositionGroupId(String regimenCompositionId) {
        this.treatmentAdvice.setDrugCompositionGroupId(regimenCompositionId);
        return this;
    }

    public TreatmentAdviceBuilder withRegimenId(String regimenId) {
        this.treatmentAdvice.setRegimenId(regimenId);
        return this;
    }

    public TreatmentAdviceBuilder withPatientId(String patientId) {
        this.treatmentAdvice.setPatientId(patientId);
        return this;
    }

    public TreatmentAdviceBuilder withStartDate(LocalDate startDate) {
        DrugDosage drugDosage = new DrugDosage();
        drugDosage.setStartDate(startDate);
        this.treatmentAdvice.setDrugDosages(Arrays.asList(drugDosage));
        return this;
    }

    public TreatmentAdviceBuilder withDrugDosages(String drug1Time) {
        DrugDosage drugDosage1 = new DrugDosage();
        drugDosage1.setMorningTime(drug1Time);
        this.treatmentAdvice.setDrugDosages(Arrays.asList(drugDosage1));
        return this;
    }

    public TreatmentAdviceBuilder withDrugDosages(String drug1Time, String drug2Time) {
        DrugDosage drugDosage1 = new DrugDosage();
        drugDosage1.setMorningTime(drug1Time);
        DrugDosage drugDosage2 = new DrugDosage();
        drugDosage2.setEveningTime(drug2Time);
        this.treatmentAdvice.setDrugDosages(Arrays.asList(drugDosage1, drugDosage2));
        return this;
    }

    public TreatmentAdviceBuilder withDrugDosages(DrugDosage drugDosage) {
        this.treatmentAdvice.setDrugDosages(Arrays.asList(drugDosage));
        return this;
    }

    public TreatmentAdvice build() {
        return this.treatmentAdvice;
    }

    public static TreatmentAdviceBuilder startRecording() {
        return new TreatmentAdviceBuilder();
    }

    public TreatmentAdviceBuilder withDefaults() {
        return this.withId(UUIDUtil.newUUID()).withPatientId("patientId").withDrugCompositionGroupId("regimenCompositionId").withRegimenId("regimenId");
    }
}
