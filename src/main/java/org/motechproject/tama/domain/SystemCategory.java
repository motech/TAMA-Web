package org.motechproject.tama.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

public class SystemCategory extends BaseEntity {

    private String name;

    private Ailments ailments = new Ailments();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Ailments getAilments() {
        return ailments;
    }

    public void setAilments(Ailments ailments) {
        this.ailments = ailments;
    }

    @JsonIgnore
    public boolean isCategoryEmpty() {
        return ailments.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SystemCategory that = (SystemCategory) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
