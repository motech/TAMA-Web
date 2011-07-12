package org.motechproject.tama.builder;

import org.motechproject.tama.domain.Regimen;
import org.motechproject.tama.domain.RegimenComposition;

import java.util.HashSet;
import java.util.Set;

public class RegimenBuilder {

    private Regimen regimen = new Regimen();

    public RegimenBuilder withId(String id){
        this.regimen.setId(id);
        return this;
    }

    public RegimenBuilder withRegimenCompositions(Set<RegimenComposition> compositions){
        this.regimen.setCompositions(compositions);
        return this;
    }

    public RegimenBuilder withName(String name){
        this.regimen.setName(name);
        this.regimen.setRegimenDisplayName(name);
        return this;
    }

    public Regimen build() {
        return this.regimen;
    }

    public static RegimenBuilder startRecording() {
        return new RegimenBuilder();
    }

    public RegimenBuilder withDefaults(){
        HashSet<RegimenComposition> compositions = new HashSet<RegimenComposition>();
        compositions.add(RegimenCompositionBuilder.startRecording().withDefaults().build());
        return this.withId("555555").withName("regimen").withRegimenCompositions(compositions);
    }
}
