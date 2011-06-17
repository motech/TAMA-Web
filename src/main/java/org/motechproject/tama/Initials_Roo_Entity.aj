// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama;

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
import org.motechproject.tama.Initials;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Initials_Roo_Entity {
    
    declare @type: Initials: @Entity;
    
    @PersistenceContext
    transient EntityManager Initials.entityManager;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long Initials.id;
    
    @Version
    @Column(name = "version")
    private Integer Initials.version;
    
    public Long Initials.getId() {
        return this.id;
    }
    
    public void Initials.setId(Long id) {
        this.id = id;
    }
    
    public Integer Initials.getVersion() {
        return this.version;
    }
    
    public void Initials.setVersion(Integer version) {
        this.version = version;
    }
    
    @Transactional
    public void Initials.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Initials.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Initials attached = Initials.findInitials(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Initials.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Initials.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Initials Initials.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Initials merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static final EntityManager Initials.entityManager() {
        EntityManager em = new Initials().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Initials.countInitialses() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Initials o", Long.class).getSingleResult();
    }
    
    public static List<Initials> Initials.findAllInitialses() {
        return entityManager().createQuery("SELECT o FROM Initials o", Initials.class).getResultList();
    }
    
    public static Initials Initials.findInitials(Long id) {
        if (id == null) return null;
        return entityManager().find(Initials.class, id);
    }
    
    public static List<Initials> Initials.findInitialsEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Initials o", Initials.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
