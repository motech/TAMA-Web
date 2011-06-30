// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama.domain;

import java.lang.Integer;
import java.lang.Long;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Version;
import org.motechproject.tama.domain.DrugDosage;
import org.springframework.transaction.annotation.Transactional;

privileged aspect DrugDosage_Roo_Entity {
    
    declare @type: DrugDosage: @Entity;
    
    @PersistenceContext
    transient EntityManager DrugDosage.entityManager;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long DrugDosage.id;
    
    @Version
    @Column(name = "version")
    private Integer DrugDosage.version;
    
    public Long DrugDosage.getId() {
        return this.id;
    }
    
    public void DrugDosage.setId(Long id) {
        this.id = id;
    }
    
    public Integer DrugDosage.getVersion() {
        return this.version;
    }
    
    public void DrugDosage.setVersion(Integer version) {
        this.version = version;
    }
    
    @Transactional
    public void DrugDosage.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void DrugDosage.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            DrugDosage attached = DrugDosage.findDrugDosage(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void DrugDosage.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void DrugDosage.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public DrugDosage DrugDosage.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        DrugDosage merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static final EntityManager DrugDosage.entityManager() {
        EntityManager em = new DrugDosage().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long DrugDosage.countDrugDosages() {
        return entityManager().createQuery("SELECT COUNT(o) FROM DrugDosage o", Long.class).getSingleResult();
    }
    
    public static List<DrugDosage> DrugDosage.findAllDrugDosages() {
        return entityManager().createQuery("SELECT o FROM DrugDosage o", DrugDosage.class).getResultList();
    }
    
    public static DrugDosage DrugDosage.findDrugDosage(Long id) {
        if (id == null) return null;
        return entityManager().find(DrugDosage.class, id);
    }
    
    public static List<DrugDosage> DrugDosage.findDrugDosageEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM DrugDosage o", DrugDosage.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
