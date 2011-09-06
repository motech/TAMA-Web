package org.motechproject.tama.web.view;


import org.motechproject.tama.domain.Regimen;
import org.motechproject.tama.repository.AllRegimens;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RegimensView {

    private final AllRegimens allRegimens;

    public RegimensView(AllRegimens allRegimens){
        this.allRegimens = allRegimens;
    }

    public List<Regimen> getAll() {
        List<Regimen> allRegimens = this.allRegimens.getAll();
        Collections.sort(allRegimens, new Comparator<Regimen>() {
            @Override
            public int compare(Regimen regimen, Regimen otherRegimen) {
                return regimen.getDisplayName().toLowerCase().compareTo(otherRegimen.getDisplayName().toLowerCase());
            }
        });
        return allRegimens;
    }
}
