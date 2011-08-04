package org.motechproject.tama.domain;

import org.motechproject.tama.util.UUIDUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RegimenComposition extends BaseEntity {

    private String regimenCompositionId;

    private Set<String> derivedCompositionIds = new HashSet<String>();

    private Set<String> drugIds = new HashSet<String>();

    private String displayName;

    public RegimenComposition() {
        regimenCompositionId = UUIDUtil.newUUID();
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

    public String getRegimenCompositionId() {
        return regimenCompositionId;
    }

    public void setRegimenCompositionId(String regimenCompositionId) {
        this.regimenCompositionId = regimenCompositionId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void addDerivedComposition(String... derivedCompositionId) {
        this.derivedCompositionIds.addAll(Arrays.asList(derivedCompositionId));
    }
}
