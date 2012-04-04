package org.motechproject.tama.refdata.objectcache;

import org.motechproject.tama.refdata.domain.OpportunisticInfection;
import org.motechproject.tama.refdata.repository.AllOpportunisticInfections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    @Override
    public List<OpportunisticInfection> getAll() {
        List<OpportunisticInfection> all = super.getAll();
        Collections.sort(all, new Comparator<OpportunisticInfection>() {
            @Override
            public int compare(OpportunisticInfection opportunisticInfection1, OpportunisticInfection opportunisticInfection2) {
                if (opportunisticInfection1.getName().equals("Other")) return 1;
                if (opportunisticInfection2.getName().equals("Other")) return -1;
                return opportunisticInfection1.getName().compareTo(opportunisticInfection2.getName());
            }
        });
        return all;
    }
}
