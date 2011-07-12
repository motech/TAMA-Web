package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;

import javax.validation.constraints.NotNull;

@RooJavaBean
@RooEntity
@TypeDiscriminator("doc.documentType == 'Company'")
public class Company extends CouchEntity {

	@NotNull
    private String name;
	
	protected Company() {
	}

	public Company(String name) {
		this.name = name;
	}

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
