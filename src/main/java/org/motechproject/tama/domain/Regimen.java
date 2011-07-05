package org.motechproject.tama.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;

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
}
