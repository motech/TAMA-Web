package org.motechproject.tama.web.view;


import org.motechproject.tama.refdata.domain.HIVTestReason;
import org.motechproject.tama.refdata.repository.AllHIVTestReasons;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HIVTestReasonsView {

    private final AllHIVTestReasons HIVTestReasons;

    public HIVTestReasonsView(AllHIVTestReasons HIVTestReasons) {
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
