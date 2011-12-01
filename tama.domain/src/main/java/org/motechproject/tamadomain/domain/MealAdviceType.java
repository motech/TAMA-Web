package org.motechproject.tamadomain.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tamacommon.domain.CouchEntity;

import javax.validation.constraints.NotNull;

@TypeDiscriminator("doc.documentType == 'MealAdviceType'")
public class MealAdviceType extends CouchEntity {

    protected MealAdviceType() {
	}

	public MealAdviceType(String type) {
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
