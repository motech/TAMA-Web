package org.motechproject.tama.refdata.builder;

import org.motechproject.tama.refdata.domain.DrugComposition;
import org.motechproject.tama.refdata.domain.DrugCompositionGroup;

import java.util.HashSet;
import java.util.Set;

public class DrugCompositionGroupBuilder {

    private DrugCompositionGroup drugCompositionGroup = new DrugCompositionGroup();

    public DrugCompositionGroupBuilder withDrugCompositions(Set<DrugComposition> drugCompositions) {
        this.drugCompositionGroup.setDrugCompositions(drugCompositions);
        return this;
    }

    public DrugCompositionGroupBuilder withName(String name) {
        this.drugCompositionGroup.setName(name);
        return this;
    }

    public DrugCompositionGroup build() {
        return this.drugCompositionGroup;
    }

    public static DrugCompositionGroupBuilder startRecording() {
        return new DrugCompositionGroupBuilder();
    }

    public DrugCompositionGroupBuilder withDefaults() {
        Set<DrugComposition> drugCompositions = new HashSet<DrugComposition>();
        drugCompositions.add(DrugCompositionBuilder.startRecording().withDefaults().build());
        return this.withName("drugCompositionGroupName").withDrugCompositions(drugCompositions);
    }
}
