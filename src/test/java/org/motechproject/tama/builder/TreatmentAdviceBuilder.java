package org.motechproject.tama.builder;

import org.motechproject.tama.domain.TreatmentAdvice;

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

    public TreatmentAdvice build() {
        return this.treatmentAdvice;
    }

    public static TreatmentAdviceBuilder startRecording() {
        return new TreatmentAdviceBuilder();
    }

    public TreatmentAdviceBuilder withDefaults(){
        return this.withId("treatmentAdviceId").withPatientId("patientId").withDrugCompositionGroupId("regimenCompositionId").withRegimenId("regimenId");
    }
}
