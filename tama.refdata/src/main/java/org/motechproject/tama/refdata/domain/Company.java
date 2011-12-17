package org.motechproject.tama.refdata.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tamacommon.domain.CouchEntity;

import javax.validation.constraints.NotNull;

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
