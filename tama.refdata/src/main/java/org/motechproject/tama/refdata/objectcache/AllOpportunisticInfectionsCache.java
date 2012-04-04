package org.motechproject.tama.refdata.objectcache;

import org.motechproject.tama.refdata.domain.OpportunisticInfection;
import org.motechproject.tama.refdata.repository.AllOpportunisticInfections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllOpportunisticInfectionsCache extends Cachable<OpportunisticInfection>{

    @Autowired
    public AllOpportunisticInfectionsCache(AllOpportunisticInfections allOpportunisticInfections) {
        super(allOpportunisticInfections);
    }

    @Override
    protected String getKey(OpportunisticInfection opportunisticInfection) {
        return opportunisticInfection.getId();
    }
}
