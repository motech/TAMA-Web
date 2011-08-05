package org.motechproject.tama.builder;

import org.motechproject.tama.domain.DrugComposition;

import java.util.HashSet;
import java.util.Set;

public class RegimenCompositionBuilder {

    private DrugComposition drugComposition = new DrugComposition();

    public RegimenCompositionBuilder withId(String id){
        this.drugComposition.setDrugCompositionId(id);
        return this;
    }

    public RegimenCompositionBuilder withDrugIds(Set<String> drugIds){
        this.drugComposition.setDrugIds(drugIds);
        return this;
    }

    public RegimenCompositionBuilder withDisplayName(String displayName){
        this.drugComposition.setDisplayName(displayName);
        return this;
    }

    public DrugComposition build() {
        return this.drugComposition;
    }

    public static RegimenCompositionBuilder startRecording() {
        return new RegimenCompositionBuilder();
    }

    public RegimenCompositionBuilder withDefaults(){
        HashSet<String> drugIds = new HashSet<String>();
        drugIds.add("drugId1");
        drugIds.add("drugId2");
        return this.withId("regimenCompositionId").withDrugIds(drugIds).withDisplayName("drugDisplayName");
    }
}
