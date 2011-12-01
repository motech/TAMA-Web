package org.motechproject.tamadomain.domain;

import org.motechproject.tamacommon.domain.CouchEntity;

import javax.validation.constraints.NotNull;

public class IVRLanguage extends CouchEntity {

    @NotNull
    private String name;
    private String code;

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

    
    public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public static IVRLanguage newIVRLanguage(String language, String code) {
        IVRLanguage ivrLanguage = new IVRLanguage();
        ivrLanguage.setName(language);
        ivrLanguage.setCode(code);
        return ivrLanguage;
    }

}
