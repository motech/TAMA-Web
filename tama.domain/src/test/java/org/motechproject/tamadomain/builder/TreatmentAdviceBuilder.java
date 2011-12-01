package org.motechproject.tamadomain.builder;

import org.joda.time.LocalDate;
import org.motechproject.tamacommon.util.UUIDUtil;
import org.motechproject.tamadomain.domain.DrugDosage;
import org.motechproject.tamadomain.domain.TreatmentAdvice;

import java.util.Arrays;

public class TreatmentAdviceBuilder {

    private TreatmentAdvice treatmentAdvice = new TreatmentAdvice();

    public TreatmentAdviceBuilder withId(String id){
        this.treatmentAdvice.setId(id);
        return this;
    }

    public TreatmentAdviceBuilder withDrugCompositionGroupId(String regimenCompositionId){
        this.treatmentAdvice.setDrugCompositionGroupId(regimenCompositionId);
        return this;
    }

    public TreatmentAdviceBuilder withRegimenId(String regimenId){
        this.treatmentAdvice.setRegimenId(regimenId);
        return this;
    }

    public TreatmentAdviceBuilder withPatientId(String patientId){
        this.treatmentAdvice.setPatientId(patientId);
        return this;
    }

    public TreatmentAdviceBuilder withStartDate(LocalDate startDate){
        DrugDosage drugDosage = new DrugDosage();
        drugDosage.setStartDate(startDate);
        this.treatmentAdvice.setDrugDosages(Arrays.asList(drugDosage));
        return this;
    }

    public TreatmentAdvice build() {
        return this.treatmentAdvice;
    }

    public static TreatmentAdviceBuilder startRecording() {
        return new TreatmentAdviceBuilder();
    }

    public TreatmentAdviceBuilder withDefaults(){
        return this.withId(UUIDUtil.newUUID()).withPatientId("patientId").withDrugCompositionGroupId("regimenCompositionId").withRegimenId("regimenId");
    }
}
