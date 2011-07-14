package org.motechproject.tama.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.tama.repository.IVRLanguages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.validation.constraints.NotNull;
import java.util.List;


@Configurable
public class IVRLanguage extends CouchEntity {

    @Autowired
    transient IVRLanguages ivrLanguages;

    @NotNull
    private String name;

    protected IVRLanguage() {
    }

    public IVRLanguage(String name) {
        this.name = name;
    }
    
     public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
     public void persist() {
        this.ivrLanguages.add(this);
    }

    public void remove() {
        this.ivrLanguages.remove(this);
    }

    public void flush() {
    }

    public void clear() {
    }

    public IVRLanguage merge() {
       this.setRevision(this.ivrLanguages.get(this.getId()).getRevision());
       this.ivrLanguages.update(this);
       return this;
    }

    public static final IVRLanguages ivrLanguages() {
        IVRLanguages ivrLanguages = new IVRLanguage().ivrLanguages;
        return ivrLanguages;
    }

    public static long countIVRLanguages() {
        return ivrLanguages().getAll().size();
    }

    public static List<IVRLanguage> findAllIVRLanguages() {
       return  ivrLanguages().getAll();
    }

    public static IVRLanguage findIVRLanguage(String id) {
        if (id == null) return null;
        return ivrLanguages().get(id);
    }

    public static List<IVRLanguage> findIVRLanguageEntries(int firstResult, int maxResults) {
         return ivrLanguages().getAll();
    }

    @JsonIgnore
    public IVRLanguages getIVRLanguages() {
        return ivrLanguages;
    }

    public void setIVRLanguages(IVRLanguages ivrLanguages) {
        this.ivrLanguages = ivrLanguages;
    }
}
