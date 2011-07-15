package org.motechproject.tama.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.tama.repository.Genders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.validation.constraints.NotNull;
import java.util.List;

public class Gender extends CouchEntity{

	@NotNull
    private String type;

	public Gender() {
	}
    
	public Gender(String type) {
		this.type = type;
	}

     public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
}
