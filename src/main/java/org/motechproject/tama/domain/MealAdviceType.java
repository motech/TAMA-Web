package org.motechproject.tama.domain;

import javax.validation.constraints.NotNull;

import org.ektorp.support.TypeDiscriminator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
@RooEntity
@TypeDiscriminator("doc.documentType == 'MealAdviceType'")
public class MealAdviceType {

    protected MealAdviceType() {
	}

	public MealAdviceType(String type) {
		this.type = type;
	}

	@NotNull
    private String type;
}
