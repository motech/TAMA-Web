package org.motechproject.tama.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.tama.repository.IVRLanguages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.validation.constraints.NotNull;
import java.util.List;


@Configurable
public class IVRLanguage extends CouchEntity {

    @NotNull
    private String name;

    public IVRLanguage() {
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
}
