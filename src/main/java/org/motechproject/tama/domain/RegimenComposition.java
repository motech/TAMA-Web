package org.motechproject.tama.domain;

import java.util.HashSet;
import java.util.Set;

import org.motechproject.tama.util.UUIDUtil;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class RegimenComposition extends BaseEntity {
	
	private String regimenCompositionId;

    private Set<String> drugIds = new HashSet<String>();

    public RegimenComposition() {
    	regimenCompositionId = UUIDUtil.newUUID();
	}

	public void addDrug(Drug drug) {
		drugIds.add(drug.getId());
	}
}
