package org.motechproject.tama.domain;

import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
@RooEntity
public class Gender {

	@NotNull
    private String type;

	protected Gender() {
	}

	public Gender(String type) {
		this.type = type;
	}
}
