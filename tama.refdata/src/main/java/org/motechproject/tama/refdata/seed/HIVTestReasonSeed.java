package org.motechproject.tama.refdata.seed;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.refdata.domain.HIVTestReason;
import org.motechproject.tama.refdata.objectcache.AllHIVTestReasonsCache;
import org.motechproject.tama.refdata.repository.AllHIVTestReasons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HIVTestReasonSeed {

    @Autowired
    private AllHIVTestReasons allHIVTestReasons;

    @Autowired
    private AllHIVTestReasonsCache allHIVTestReasonsCache;

    @Seed(version = "1.0", priority = 0)
    public void load() {
        allHIVTestReasons.add(HIVTestReason.newHIVTestReason("Preemployment"));
        allHIVTestReasons.add(HIVTestReason.newHIVTestReason("Pre-operative"));
        allHIVTestReasons.add(HIVTestReason.newHIVTestReason("General Checkup"));
        allHIVTestReasons.add(HIVTestReason.newHIVTestReason("ANC"));
        allHIVTestReasons.add(HIVTestReason.newHIVTestReason("Pre-marital"));
        allHIVTestReasons.add(HIVTestReason.newHIVTestReason("STDs"));
        allHIVTestReasons.add(HIVTestReason.newHIVTestReason("Symptomatic"));
        allHIVTestReasons.add(HIVTestReason.newHIVTestReason("Blood Donation"));
        allHIVTestReasons.add(HIVTestReason.newHIVTestReason("Spouse Detected"));
        allHIVTestReasonsCache.refresh();
    }
}