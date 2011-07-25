package org.motechproject.tama.domain;

import javax.validation.constraints.NotNull;

public class ModeOfTransmission extends CouchEntity{
	@NotNull
    private String type;

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
