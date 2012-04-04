package org.motechproject.tama.refdata.objectcache;

import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllRegimensCache extends Cachable<Regimen> {

    @Autowired
    public AllRegimensCache(AllRegimens allRegimens) {
        super(allRegimens);
    }

    @Override
    protected String getKey(Regimen regimen) {
        return regimen.getId();
    }

    @Override
    protected int compareTo(Regimen t1, Regimen t2) {
        return t1.getDisplayName().compareTo(t2.getDisplayName());
    }
}
