package org.motechproject.tama.refdata.objectcache;

import org.motechproject.tama.refdata.domain.Drug;
import org.motechproject.tama.refdata.repository.AllDrugs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllDrugsCache extends Cachable<Drug> {

    @Autowired
    public AllDrugsCache(AllDrugs allDrugs) {
        super(allDrugs);
    }

    @Override
    protected String getKey(Drug drug) {
        return drug.getId();
    }

    @Override
    protected int compareTo(Drug t1, Drug t2) {
        return t1.getName().compareTo(t2.getName());
    }
}
