package org.motechproject.tamadomain.domain;

public class OtherAilment extends Ailment {

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public  static OtherAilment newOtherAilment() {
        OtherAilment otherAilment = new OtherAilment();
        otherAilment.setDefinition(AilmentDefinition.others);
        return otherAilment;
    }

}
