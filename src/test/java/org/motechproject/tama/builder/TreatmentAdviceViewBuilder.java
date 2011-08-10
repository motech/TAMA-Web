package org.motechproject.tama.builder;

import org.motechproject.tama.web.model.DrugDosageView;
import org.motechproject.tama.web.model.TreatmentAdviceView;

import java.util.ArrayList;
import java.util.List;

public class TreatmentAdviceViewBuilder {

    private TreatmentAdviceView treatmentAdvice = new TreatmentAdviceView();

    public TreatmentAdviceViewBuilder withRegimenName(String regimenName){
        this.treatmentAdvice.setRegimenName(regimenName);
        return this;
    }

    public TreatmentAdviceViewBuilder withDrugCompositionGroupName(String drugCompositionGroupName){
        this.treatmentAdvice.setDrugCompositionName(drugCompositionGroupName);
        return this;
    }

    public TreatmentAdviceViewBuilder withDrugDosages(List<DrugDosageView> drugDosages){
        this.treatmentAdvice.setDrugDosages(drugDosages);
        return this;
    }

    public TreatmentAdviceViewBuilder withPatientId(String patientId){
        this.treatmentAdvice.setPatientId(patientId);
        return this;
    }

    public TreatmentAdviceView build() {
        return this.treatmentAdvice;
    }

    public static TreatmentAdviceViewBuilder startRecording() {
        return new TreatmentAdviceViewBuilder();
    }

    public TreatmentAdviceViewBuilder withDefaults(){
        List<DrugDosageView> drugDosages = new ArrayList<DrugDosageView>();
        drugDosages.add(DrugDosageViewBuilder.startRecording().withDefaults().build());
        drugDosages.add(DrugDosageViewBuilder.startRecording().withDefaults().withBrandName("Combivir").build());
        return this.withPatientId("1234").withRegimenName("AZT + 3TC + EFV").withDrugCompositionGroupName("AZT+3TC+EFV").withDrugDosages(drugDosages);
    }
}
