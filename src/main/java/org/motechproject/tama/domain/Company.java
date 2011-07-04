package org.motechproject.tama.domain;

import javax.validation.constraints.NotNull;

import org.ektorp.support.TypeDiscriminator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
@RooEntity
@TypeDiscriminator("doc.documentType == 'Company'")
public class Company {

	@NotNull
    private String name;
	
	protected Company() {
	}

	public Company(String name) {
		this.name = name;
	}
}
