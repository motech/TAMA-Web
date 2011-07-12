package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@RooJavaBean
@RooEntity
@TypeDiscriminator("doc.documentType == 'Regimen'")
public class Regimen extends CouchEntity {
	
    @NotNull
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<RegimenComposition> compositions = new HashSet<RegimenComposition>();

    @NotNull
    private String regimenDisplayName;

    protected Regimen() {
	}

	public Regimen(String name, String displayName) {
		this.name = name;
		this.regimenDisplayName = displayName;
	}

	public void addComposition(RegimenComposition regimenComposition) {
		compositions.add(regimenComposition);
	}

    public RegimenComposition getCompositionsFor(String regimenCompositionId) {
        for (RegimenComposition regimenComposition : compositions) {
            if (regimenComposition.getRegimenCompositionId().equals(regimenCompositionId))
                return regimenComposition;
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<RegimenComposition> getCompositions() {
        return this.compositions;
    }

    public void setCompositions(Set<RegimenComposition> compositions) {
        this.compositions = compositions;
    }

    public String getRegimenDisplayName() {
        return this.regimenDisplayName;
    }

    public void setRegimenDisplayName(String regimenDisplayName) {
        this.regimenDisplayName = regimenDisplayName;
    }
}
