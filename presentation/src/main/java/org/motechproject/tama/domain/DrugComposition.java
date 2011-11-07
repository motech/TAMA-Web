package org.motechproject.tama.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.tama.util.UUIDUtil;

import java.util.HashSet;
import java.util.Set;

public class DrugComposition extends BaseEntity {

    private String id;

    private Set<String> drugIds = new HashSet<String>();

    private Set<Drug> drugs = new HashSet<Drug>();

    private String displayName;

    public DrugComposition() {
        id = UUIDUtil.newUUID();
    }

    public void addDrugId(Drug drug) {
        drugIds.add(drug.getId());
    }

    public Set<String> getDrugIds() {
        return this.drugIds;
    }

    public void setDrugIds(Set<String> drugIds) {
        this.drugIds = drugIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Set<Drug> getDrugs() {
        return drugs;
    }

    public void setDrugs(Set<Drug> drugs) {
        this.drugs = drugs;
    }
}
