package org.motechproject.tama.web.view;


import org.motechproject.tama.domain.Regimen;
import org.motechproject.tama.repository.Regimens;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RegimensView {

    private final Regimens regimens;

    public RegimensView(Regimens regimens){
        this.regimens = regimens;
    }

    public List<Regimen> getAll() {
        List<Regimen> allRegimens = regimens.getAll();
        Collections.sort(allRegimens, new Comparator<Regimen>() {
            @Override
            public int compare(Regimen regimen, Regimen otherRegimen) {
                return regimen.getRegimenDisplayName().toLowerCase().compareTo(otherRegimen.getRegimenDisplayName().toLowerCase());
            }
        });
        return allRegimens;
    }
}
