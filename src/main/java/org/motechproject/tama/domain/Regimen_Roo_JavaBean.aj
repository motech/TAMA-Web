// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama.domain;

import java.lang.String;
import java.util.Set;
import org.motechproject.tama.domain.RegimenComposition;

privileged aspect Regimen_Roo_JavaBean {
    
    public String Regimen.getName() {
        return this.name;
    }
    
    public void Regimen.setName(String name) {
        this.name = name;
    }
    
    public Set<RegimenComposition> Regimen.getCompositions() {
        return this.compositions;
    }
    
    public void Regimen.setCompositions(Set<RegimenComposition> compositions) {
        this.compositions = compositions;
    }
    
}
