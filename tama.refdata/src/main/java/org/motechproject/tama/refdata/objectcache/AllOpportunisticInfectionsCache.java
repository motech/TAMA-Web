package org.motechproject.tama.refdata.objectcache;

import org.motechproject.tama.refdata.domain.OpportunisticInfection;
import org.motechproject.tama.refdata.repository.AllOpportunisticInfections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllOpportunisticInfectionsCache extends Cachable<OpportunisticInfection> {

    @Autowired
    public AllOpportunisticInfectionsCache(AllOpportunisticInfections allOpportunisticInfections) {
        super(allOpportunisticInfections);
    }

    @Override
    protected String getKey(OpportunisticInfection opportunisticInfection) {
        return opportunisticInfection.getId();
    }

    @Override
    protected int compareTo(OpportunisticInfection t1, OpportunisticInfection t2) {
        if (t1.getName().equals("Other")) return 1;
        if (t2.getName().equals("Other")) return -1;
        return t1.getName().compareTo(t2.getName());
    }
}
