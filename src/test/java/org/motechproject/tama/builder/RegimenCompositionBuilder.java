package org.motechproject.tama.builder;

import org.motechproject.tama.domain.RegimenComposition;

import java.util.HashSet;
import java.util.Set;

public class RegimenCompositionBuilder {

    private RegimenComposition regimenComposition = new RegimenComposition();

    public RegimenCompositionBuilder withId(String id){
        this.regimenComposition.setRegimenCompositionId(id);
        return this;
    }

    public RegimenCompositionBuilder withDrugIds(Set<String> drugIds){
        this.regimenComposition.setDrugIds(drugIds);
        return this;
    }

    public RegimenComposition build() {
        return this.regimenComposition;
    }

    public static RegimenCompositionBuilder startRecording() {
        return new RegimenCompositionBuilder();
    }

    public RegimenCompositionBuilder withDefaults(){
        HashSet<String> drugIds = new HashSet<String>();
        drugIds.add("888");
        drugIds.add("999");
        return this.withId("9999999").withDrugIds(drugIds);
    }
}
