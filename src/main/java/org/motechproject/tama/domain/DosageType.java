package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;

import javax.validation.constraints.NotNull;

@RooJavaBean
@RooEntity
@TypeDiscriminator("doc.documentType == 'DosageType'")
public class DosageType extends CouchEntity {
	
    protected DosageType() {
	}

	public DosageType(String type) {
    	this.type = type;
    }

	@NotNull
    private String type;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
