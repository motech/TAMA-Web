package org.motechproject.tama.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

privileged aspect IVRLanguage_Roo_Equals {

    public boolean IVRLanguage.equals(Object other) {
        if (other == null) { return false; }
        if (other == this) { return true; }
        if (other.getClass() != getClass()) {
            return false;
        }
        IVRLanguage rhs = (IVRLanguage) other;
        return new EqualsBuilder()
            .append(id, rhs.id)
            .isEquals();
    }

    public int IVRLanguage.hashCode() {
        return new HashCodeBuilder(43, 11)
            .append(id)
            .toHashCode();
    }
}
