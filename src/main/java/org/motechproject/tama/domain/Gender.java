package org.motechproject.tama.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.tama.repository.Genders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.validation.constraints.NotNull;
import java.util.List;

@Configurable
public class Gender extends CouchEntity{

    @Autowired
    transient Genders genders;

	@NotNull
    private String type;

	public Gender() {
	}
    
	public Gender(String type) {
		this.type = type;
	}

    public void persist() {
        this.genders.add(this);
    }

    public void remove() {
        this.genders.remove(this);
    }

    public void flush() {
    }

    public void clear() {
    }

    public Gender merge() {
       this.setRevision(this.genders.get(this.getId()).getRevision());
       this.genders.update(this);
       return this;
    }

    public static final Genders genders() {
        Genders genders = new Gender().genders;
        return genders;
    }

    public static long countGenders() {
        return genders().getAll().size();
    }

    public static List<Gender> findAllGenders() {
       return  genders().getAll();
    }

    public static Gender findGender(String id) {
        if (id == null) return null;
        return genders().get(id);
    }

    public static List<Gender> findGenderEntries(int firstResult, int maxResults) {
         return genders().getAll();
    }

    @JsonIgnore
    public Genders getGenders() {
        return genders;
    }

    public void setGenders(Genders genders) {
        this.genders = genders;
    }
    
     public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
}
