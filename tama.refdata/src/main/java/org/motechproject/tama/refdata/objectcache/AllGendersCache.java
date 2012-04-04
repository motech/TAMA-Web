package org.motechproject.tama.refdata.objectcache;

import org.motechproject.tama.refdata.domain.Gender;
import org.motechproject.tama.refdata.repository.AllGenders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllGendersCache extends Cachable<Gender> {

    @Autowired
    public AllGendersCache(AllGenders allGenders) {
        super(allGenders);
    }

    @Override
    protected String getKey(Gender gender) {
        return gender.getId();
    }

    @Override
    protected int compareTo(Gender t1, Gender t2) {
        return t1.getType().compareTo(t2.getType());
    }
}
