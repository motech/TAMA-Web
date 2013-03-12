package org.motechproject.tama.refdata.seed;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.refdata.domain.Gender;
import org.motechproject.tama.refdata.objectcache.AllGendersCache;
import org.motechproject.tama.refdata.repository.AllGenders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenderSeed {

    @Autowired
    private AllGenders allGenders;

    @Autowired
    private AllGendersCache allGendersCache;

    @Seed(version = "1.0", priority = 0)
    public void load() {
        allGenders.add(Gender.newGender("Male"));
        allGenders.add(Gender.newGender("Female"));
        allGendersCache.refresh();
    }
}