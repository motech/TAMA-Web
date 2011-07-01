package org.motechproject.tama.domain;

import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
@RooEntity
public class DosageType {
	
    protected DosageType() {
	}

	public DosageType(String type) {
    	this.type = type;
    }

	@NotNull
    private String type;
}
