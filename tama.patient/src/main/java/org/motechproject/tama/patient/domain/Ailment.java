package org.motechproject.tama.patient.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.tama.common.domain.BaseEntity;

public class Ailment extends BaseEntity {

    private AilmentDefinition definition;

    private AilmentState state = AilmentState.NONE;

    public AilmentDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(AilmentDefinition definition) {
        this.definition = definition;
    }

    public AilmentState getState() {
        return state;
    }

    public void setState(AilmentState state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ailment ailment = (Ailment) o;

        return definition == ailment.definition;

    }

    @Override
    public int hashCode() {
        return definition != null ? definition.hashCode() : 0;
    }

    @JsonIgnore
    public boolean everHadTheAilment() {
        return !state.equals(AilmentState.NONE);
    }
}
