// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama;

import java.lang.Object;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

privileged aspect Doctor_Roo_Equals {
    
    public boolean Doctor.equals(Object other) {
        if (other == null) { return false; }
        if (other == this) { return true; }
        if (other.getClass() != getClass()) {
            return false;
        }
        Doctor rhs = (Doctor) other;
        return new EqualsBuilder()
            .append(id, rhs.id)
            .isEquals();
    }
    
    public int Doctor.hashCode() {
        return new HashCodeBuilder(43, 11)
            .append(id)
            .toHashCode();
    }
    
}
