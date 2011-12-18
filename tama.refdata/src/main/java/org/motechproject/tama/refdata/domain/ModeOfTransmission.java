package org.motechproject.tama.refdata.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.common.domain.CouchEntity;

import javax.validation.constraints.NotNull;

@TypeDiscriminator("doc.documentType == 'ModeOfTransmission'")
public class ModeOfTransmission extends CouchEntity {
    @NotNull
    private String type;

    public ModeOfTransmission() {
    }

    public ModeOfTransmission(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static ModeOfTransmission newModeOfTransmission(String type) {
        ModeOfTransmission mode = new ModeOfTransmission();
        mode.setType(type);
        return mode;
    }
}
