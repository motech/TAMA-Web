package org.motechproject.tama.refdata.builder;

import org.motechproject.tama.refdata.domain.DrugCompositionGroup;
import org.motechproject.tama.refdata.domain.Regimen;

import java.util.HashSet;
import java.util.Set;

public class RegimenBuilder {

    private Regimen regimen = new Regimen();

    public RegimenBuilder withId(String id) {
        this.regimen.setId(id);
        return this;
    }

    public RegimenBuilder withDrugCompositionGroups(Set<DrugCompositionGroup> compositions) {
        this.regimen.setDrugCompositionGroups(compositions);
        return this;
    }

    public RegimenBuilder withName(String name) {
        this.regimen.setName(name);
        this.regimen.setDisplayName(name);
        return this;
    }

    public Regimen build() {
        return this.regimen;
    }

    public static RegimenBuilder startRecording() {
        return new RegimenBuilder();
    }

    public RegimenBuilder withDefaults() {
        HashSet<DrugCompositionGroup> groups = new HashSet<DrugCompositionGroup>();
        groups.add(DrugCompositionGroupBuilder.startRecording().withDefaults().build());
        return this.withId("regimenId").withName("regimenName").withDrugCompositionGroups(groups);
    }
}
