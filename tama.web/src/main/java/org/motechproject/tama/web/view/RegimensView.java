package org.motechproject.tama.web.view;


import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.objectcache.AllRegimensCache;
import org.motechproject.tama.refdata.repository.AllRegimens;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RegimensView {

    private final AllRegimensCache allRegimens;

    public RegimensView(AllRegimensCache allRegimens) {
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
