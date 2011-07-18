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

    public IVRLanguage(String id) {
        this.setId(id);
    }
    
     public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public static IVRLanguage newIVRLanguage(String language) {
        IVRLanguage ivrLanguage = new IVRLanguage();
        ivrLanguage.setName(language);
        return ivrLanguage;
    }

}
