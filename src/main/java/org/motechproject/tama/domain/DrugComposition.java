package org.motechproject.tama.domain;

import org.motechproject.tama.util.UUIDUtil;

import java.util.HashSet;
import java.util.Set;

public class DrugComposition extends BaseEntity {

    private String drugCompositionId;

    private Set<String> drugIds = new HashSet<String>();

    private String displayName;

    public DrugComposition() {
        drugCompositionId = UUIDUtil.newUUID();
    }

    public void addDrug(Drug drug) {
        drugIds.add(drug.getId());
    }

    public Set<String> getDrugIds() {
        return this.drugIds;
    }

    public void setDrugIds(Set<String> drugIds) {
        this.drugIds = drugIds;
    }

    public String getDrugCompositionId() {
        return drugCompositionId;
    }

    public void setDrugCompositionId(String drugCompositionId) {
        this.drugCompositionId = drugCompositionId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
