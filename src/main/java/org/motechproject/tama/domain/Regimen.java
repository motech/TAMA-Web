package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@TypeDiscriminator("doc.documentType == 'Regimen'")
public class Regimen extends CouchEntity {
	
    @NotNull
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<DrugComposition> drugCompositions = new HashSet<DrugComposition>();

    private Set<DrugComposition> drugCompositionGroups = new HashSet<DrugComposition>();

    @NotNull
    private String regimenDisplayName;

    public Regimen() {
	}

	public Regimen(String name, String displayName) {
		this.name = name;
		this.regimenDisplayName = displayName;
	}

	public void addComposition(DrugComposition... drugCompositions) {
		this.drugCompositions.addAll(Arrays.asList(drugCompositions));
	}

    public DrugComposition getCompositionsFor(String regimenCompositionId) {
        for (DrugComposition drugComposition : drugCompositions) {
            if (drugComposition.getDrugCompositionId().equals(regimenCompositionId))
                return drugComposition;
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<DrugComposition> getDrugCompositions() {
        return this.drugCompositions;
    }

    public void setDrugCompositions(Set<DrugComposition> drugCompositions) {
        this.drugCompositions = drugCompositions;
    }

    public String getRegimenDisplayName() {
        return this.regimenDisplayName;
    }

    public void setRegimenDisplayName(String regimenDisplayName) {
        this.regimenDisplayName = regimenDisplayName;
    }
}
