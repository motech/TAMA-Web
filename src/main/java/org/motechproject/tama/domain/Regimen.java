package org.motechproject.tama.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@RooJavaBean
@RooEntity
public class Regimen {
	
    protected Regimen() {
	}

	public Regimen(String name, String displayName) {
		this.name = name;
		this.regimenDisplayName = displayName;
	}

	@NotNull
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<RegimenComposition> compositions = new HashSet<RegimenComposition>();

    @NotNull
    private String regimenDisplayName;

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
}
