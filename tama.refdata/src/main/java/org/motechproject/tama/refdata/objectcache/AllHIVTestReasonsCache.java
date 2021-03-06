package org.motechproject.tama.refdata.objectcache;

import org.motechproject.tama.refdata.domain.HIVTestReason;
import org.motechproject.tama.refdata.repository.AllHIVTestReasons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllHIVTestReasonsCache extends Cachable<HIVTestReason>{

    @Autowired
    public AllHIVTestReasonsCache(AllHIVTestReasons allHIVTestReasons) {
        super(allHIVTestReasons);
    }

    @Override
    protected String getKey(HIVTestReason hivTestReason) {
        return hivTestReason.getId();
    }

    @Override
    protected int compareTo(HIVTestReason t1, HIVTestReason t2) {
        return t1.getName().compareTo(t2.getName());
    }
}
