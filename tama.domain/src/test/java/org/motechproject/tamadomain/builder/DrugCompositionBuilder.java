package org.motechproject.tamadomain.builder;

import org.motechproject.tamadomain.domain.DrugComposition;

import java.util.HashSet;
import java.util.Set;

public class DrugCompositionBuilder {

    private DrugComposition drugComposition = new DrugComposition();

    public DrugCompositionBuilder withId(String id){
        this.drugComposition.setId(id);
        return this;
    }

    public DrugCompositionBuilder withDrugIds(Set<String> drugIds){
        this.drugComposition.setDrugIds(drugIds);
        return this;
    }

    public DrugCompositionBuilder withDisplayName(String displayName){
        this.drugComposition.setDisplayName(displayName);
        return this;
    }

    public DrugComposition build() {
        return this.drugComposition;
    }

    public static DrugCompositionBuilder startRecording() {
        return new DrugCompositionBuilder();
    }

    public DrugCompositionBuilder withDefaults(){
        HashSet<String> drugIds = new HashSet<String>();
        drugIds.add("drugId1");
        drugIds.add("drugId2");
        return this.withId("regimenCompositionId").withDrugIds(drugIds).withDisplayName("drugDisplayName");
    }
}
