package org.motechproject.tamadomain.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.LinkedList;
import java.util.List;

public class Ailments {

    private List<Ailment> ailments = new LinkedList<Ailment>();

    private List<OtherAilment> otherAilments = new LinkedList<OtherAilment>();

    public List<OtherAilment> getOtherAilments() {
        return otherAilments;
    }

    public Ailments setOtherAilments(List<OtherAilment> otherAilments) {
        this.otherAilments = otherAilments;
        return this;
    }

    public List<Ailment> getAilments() {
        return ailments;
    }

    public Ailments setAilments(List<Ailment> ailments) {
        this.ailments = ailments;
        return this;
    }

    @JsonIgnore
    public boolean isNotEmpty(){
        return !ailments.isEmpty();
    }

    @JsonIgnore
    public boolean isEmpty() {
        return ailments.isEmpty();
    }

    @JsonIgnore
    public Ailment getAilment(AilmentDefinition ailmentDefinition) {
        for (Ailment ailment : ailments) {
            if (ailment.getDefinition().equals(ailmentDefinition))
                return ailment;
        }
        return null;
    }
}
