package org.motechproject.tama.refdata.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.common.domain.CouchEntity;

import javax.validation.constraints.NotNull;

@TypeDiscriminator("doc.documentType == 'HIVTestReason'")
public class HIVTestReason extends CouchEntity {
    @NotNull
    private String name;

    public HIVTestReason() {
    }

    public HIVTestReason(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static HIVTestReason newHIVTestReason(String name) {
        HIVTestReason mode = new HIVTestReason();
        mode.setName(name);
        return mode;
    }
}
