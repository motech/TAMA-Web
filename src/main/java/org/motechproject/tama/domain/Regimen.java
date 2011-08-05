package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@TypeDiscriminator("doc.documentType == 'Regimen'")
public class Regimen extends CouchEntity {
	
    @NotNull
    private String name;

    @NotNull
    private String displayName;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<DrugCompositionGroup> drugCompositionGroups = new HashSet<DrugCompositionGroup>();

    public Regimen() {
	}

	public Regimen(String name, String displayName) {
		this.name = name;
		this.displayName = displayName;
	}

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void addCompositionGroup(DrugCompositionGroup... drugCompositionGroups) {
        for (DrugCompositionGroup group : drugCompositionGroups)
		    this.drugCompositionGroups.add(group);
	}

    public DrugComposition getDrugCompositionFor(String drugCompositionGroupId) {
        for (DrugCompositionGroup drugCompositionGroup : drugCompositionGroups) {
            for (DrugComposition drugComposition : drugCompositionGroup.getDrugCompositions())
                if (drugComposition.getId().equals(drugCompositionGroupId))
                    return drugComposition;
        }
        return null;
    }

    public Set<DrugCompositionGroup> getDrugCompositionGroups() {
        return this.drugCompositionGroups;
    }

    public void setDrugCompositionGroups(Set<DrugCompositionGroup> drugCompositionGroups) {
        this.drugCompositionGroups = drugCompositionGroups;
    }

    public Set<DrugComposition> getDrugCompositions() {
        Set<DrugComposition> drugCompositions = new HashSet<DrugComposition>();
        for (DrugCompositionGroup drugCompositionGroup : drugCompositionGroups)
            drugCompositions.addAll(drugCompositionGroup.getDrugCompositions());
        return drugCompositions;
    }
}
