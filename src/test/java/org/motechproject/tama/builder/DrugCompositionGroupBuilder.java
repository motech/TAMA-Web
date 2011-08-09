package org.motechproject.tama.builder;

import org.motechproject.tama.domain.DrugComposition;
import org.motechproject.tama.domain.DrugCompositionGroup;

import java.util.HashSet;
import java.util.Set;

public class DrugCompositionGroupBuilder {

    private DrugCompositionGroup drugCompositionGroup = new DrugCompositionGroup();

    public DrugCompositionGroupBuilder withId(String id){
        this.drugCompositionGroup.setId(id);
        return this;
    }

    public DrugCompositionGroupBuilder withDrugCompositions(Set<DrugComposition> drugCompositions){
        this.drugCompositionGroup.setDrugCompositions(drugCompositions);
        return this;
    }

    public DrugCompositionGroupBuilder withName(String name){
        this.drugCompositionGroup.setName(name);
        return this;
    }

    public DrugCompositionGroup build() {
        return this.drugCompositionGroup;
    }

    public static DrugCompositionGroupBuilder startRecording() {
        return new DrugCompositionGroupBuilder();
    }

    public DrugCompositionGroupBuilder withDefaults(){
        Set<DrugComposition> drugCompositions = new HashSet<DrugComposition>();
        drugCompositions.add(DrugCompositionBuilder.startRecording().withDefaults().build());
        return this.withId("drugCompositionGroupId").withName("drugCompositionGroupName").withDrugCompositions(drugCompositions);
    }
}
