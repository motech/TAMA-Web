// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama.domain;

import org.motechproject.tama.repository.TreatmentAdvices;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import javax.persistence.EntityManager;

privileged aspect TreatmentAdvice_Roo_Entity {
    
    declare parents : TreatmentAdvice extends CouchEntity;
    
    @Autowired
    transient TreatmentAdvices TreatmentAdvice.treatmentAdvices;

    public void TreatmentAdvice.persist() {
        this.treatmentAdvices.add(this);
    }
    
    public void TreatmentAdvice.remove() {
    }
    
    public void TreatmentAdvice.flush() {
    }
    
    public void TreatmentAdvice.clear() {
    }
    
    public TreatmentAdvice TreatmentAdvice.merge() {
		return null;
    }
    
    public static final EntityManager TreatmentAdvice.entityManager() {
		return null;
    }
    
    public static long TreatmentAdvice.countTreatmentAdvices() {
		return 0;
    }
    
    public static List<TreatmentAdvice> TreatmentAdvice.findAllTreatmentAdvices() {
		return null;
    }
    
    public static TreatmentAdvice TreatmentAdvice.findTreatmentAdvice(Long id) {
		return null;
    }
    
    public static List<TreatmentAdvice> TreatmentAdvice.findTreatmentAdviceEntries(int firstResult, int maxResults) {
		return null;
    }
}
