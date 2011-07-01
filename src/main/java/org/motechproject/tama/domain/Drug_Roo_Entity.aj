    // WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama.domain;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.DocumentNotFoundException;
import org.motechproject.tama.repository.Drugs;
import org.springframework.beans.factory.annotation.Autowired;

privileged aspect Drug_Roo_Entity {
    
	declare parents : Drug extends CouchEntity;


	@Autowired
    transient Drugs Drug.drugs;
    
    public void Drug.persist() {
    	this.drugs.add(this);
    }
    
    public void Drug.remove() {
        this.drugs.remove(this);
    }
    
    public Drug Drug.merge() {
		drugs.update(this);
		return this;
    }
    
    public static long Drug.countDrugs() {
		return drugs().count();
    }

    public static List<Drug> Drug.findAllDrugs() {
		return drugs().getAll();
    }

    public static Drugs Drug.drugs() {
    	return new Drug().drugs;
    }

    public static Drug Drug.findDrug(String id) {
        Drug drug = null;
        try {
            drug = drugs().get(id);
        } catch (DocumentNotFoundException e) {
            //TODO just log that document not found
        }
        return drug;
    }
    
    public static List<Drug> Drug.findDrugEntries(int firstResult, int maxResults) {
        //TODO pagination is not yet handled but this method is being used by controllers
		return drugs().getAll();
    }

    public String Drug.getRegimenCompositionId() {
        return this.regimenCompositionId;
    }

    public void Drug.setRegimenCompositionId(String regimenCompositionId) {
        this.regimenCompositionId = regimenCompositionId;
    }


    @JsonIgnore
	public Drugs Drug.getDrugs() {
		return drugs;
	}

	public void Drug.setDrugs(Drugs drugs) {
		this.drugs = drugs;
	}
}
