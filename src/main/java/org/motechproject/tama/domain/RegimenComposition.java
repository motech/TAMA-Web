package org.motechproject.tama.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;

import org.ektorp.docref.DocumentReferences;
import org.ektorp.docref.FetchType;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
@RooEntity
public class RegimenComposition {
	
	//TODO this is not needed, get rid of it <katta/shruthi>
	private String regimentCompositionId;

	public RegimenComposition() {
	}

    @DocumentReferences(backReference="regimenCompositionId", fetch = FetchType.EAGER)
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Drug> drugs = new HashSet<Drug>();

	public void addDrug(Drug drug) {
		drugs.add(drug);
	}
}
