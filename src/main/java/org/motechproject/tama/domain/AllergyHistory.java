package org.motechproject.tama.domain;

import org.motechproject.tama.TAMAConstants;

public class AllergyHistory extends BaseEntity {

    private TAMAConstants.DrugAllergy drugAllergy;
    private boolean specified;
    private String description;

    public TAMAConstants.DrugAllergy getDrugAllergy() {
        return drugAllergy;
    }

    public void setDrugAllergy(TAMAConstants.DrugAllergy drugAllergy) {
        this.drugAllergy = drugAllergy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSpecified() {
        return specified;
    }

    public void setSpecified(boolean specified) {
        this.specified = specified;
    }
}
