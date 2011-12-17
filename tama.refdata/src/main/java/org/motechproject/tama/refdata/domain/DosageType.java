package org.motechproject.tama.refdata.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tamacommon.domain.CouchEntity;

import javax.validation.constraints.NotNull;

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
