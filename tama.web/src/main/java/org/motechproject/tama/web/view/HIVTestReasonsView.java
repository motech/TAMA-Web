package org.motechproject.tama.web.view;


import org.motechproject.tama.refdata.domain.HIVTestReason;
import org.motechproject.tama.refdata.objectcache.AllHIVTestReasonsCache;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HIVTestReasonsView {

    private final AllHIVTestReasonsCache HIVTestReasons;

    public HIVTestReasonsView(AllHIVTestReasonsCache HIVTestReasons) {
        this.HIVTestReasons = HIVTestReasons;
    }

    public List<HIVTestReason> getAll() {
        List<HIVTestReason> allTestReasons = HIVTestReasons.getAll();
        Collections.sort(allTestReasons, new Comparator<HIVTestReason>() {
            @Override
            public int compare(HIVTestReason hivTestReason, HIVTestReason otherHIVTestReason) {
                return hivTestReason.getName().toLowerCase().compareTo(otherHIVTestReason.getName().toLowerCase());
            }
        });
        return allTestReasons;
    }
}
