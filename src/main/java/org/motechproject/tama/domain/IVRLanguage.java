package org.motechproject.tama.domain;

import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;


@RooJavaBean
@RooEntity
public class IVRLanguage {
    
	@NotNull
    private String name;

	protected IVRLanguage() {
	}

	public IVRLanguage(String name) {
		this.name = name;
	}
}
