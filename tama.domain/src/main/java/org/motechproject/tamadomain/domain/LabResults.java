package org.motechproject.tamadomain.domain;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class LabResults extends ArrayList<LabResult> {

    public LabResults() {
    }

    public LabResults(Collection<? extends LabResult> c) {
        super(c);
    }

    public int latestCD4Count() {
        Collections.sort(this, new LabResult.LabResultComparator());
        for (LabResult result : this) {
            if (result.isCD4()) {
                return Integer.parseInt(result.getResult());
            }
        }
        return LabResult.INVALID_CD4_COUNT;
    }

    public LocalDate latestLabTestDate() {
        Collections.sort(this, new LabResult.LabResultComparator());
        for (LabResult result : this) {
            if (result.isCD4()) {
                return result.getTestDate();
            }
        }
        return null;
    }

}
