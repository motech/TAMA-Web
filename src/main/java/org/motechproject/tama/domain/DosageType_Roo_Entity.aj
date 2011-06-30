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
import org.motechproject.tama.domain.DosageType;
import org.springframework.transaction.annotation.Transactional;

privileged aspect DosageType_Roo_Entity {
    
    declare @type: DosageType: @Entity;
    
    @PersistenceContext
    transient EntityManager DosageType.entityManager;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long DosageType.id;
    
    @Version
    @Column(name = "version")
    private Integer DosageType.version;
    
    public Long DosageType.getId() {
        return this.id;
    }
    
    public void DosageType.setId(Long id) {
        this.id = id;
    }
    
    public Integer DosageType.getVersion() {
        return this.version;
    }
    
    public void DosageType.setVersion(Integer version) {
        this.version = version;
    }
    
    @Transactional
    public void DosageType.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void DosageType.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            DosageType attached = DosageType.findDosageType(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void DosageType.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void DosageType.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public DosageType DosageType.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        DosageType merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static final EntityManager DosageType.entityManager() {
        EntityManager em = new DosageType().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long DosageType.countDosageTypes() {
        return entityManager().createQuery("SELECT COUNT(o) FROM DosageType o", Long.class).getSingleResult();
    }
    
    public static List<DosageType> DosageType.findAllDosageTypes() {
        return entityManager().createQuery("SELECT o FROM DosageType o", DosageType.class).getResultList();
    }
    
    public static DosageType DosageType.findDosageType(Long id) {
        if (id == null) return null;
        return entityManager().find(DosageType.class, id);
    }
    
    public static List<DosageType> DosageType.findDosageTypeEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM DosageType o", DosageType.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
