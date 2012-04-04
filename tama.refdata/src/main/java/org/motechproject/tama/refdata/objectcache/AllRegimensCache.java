package org.motechproject.tama.refdata.objectcache;

import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class AllRegimensCache extends Cachable<Regimen>{

    @Autowired
    public AllRegimensCache(AllRegimens allRegimens) {
        super(allRegimens);
    }

    @Override
    protected String getKey(Regimen regimen) {
        return regimen.getId();
    }

    @Override
    public List<Regimen> getAll() {
        List<Regimen> all = super.getAll();
        Collections.sort(all, new Comparator<Regimen>() {
            @Override
            public int compare(Regimen regimen1, Regimen regimen2) {
                return regimen1.getDisplayName().compareTo(regimen2.getDisplayName());
            }
        });
        return all;
    }
}
