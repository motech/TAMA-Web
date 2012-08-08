package org.motechproject.tama.patient.domain;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;

import static java.lang.Integer.parseInt;
import static java.util.Collections.sort;

public class LabResults extends ArrayList<LabResult> {

    public LabResults() {
    }

    public LabResults(Collection<? extends LabResult> c) {
        super(c);
    }

    public int latestCD4Count() {
        sort(this, new LabResult.LabResultComparator());
        for (LabResult result : this) {
            if (result.isCD4()) {
                return parseInt(result.getResult());
            }
        }
        return LabResult.INVALID_CD4_COUNT;
    }

    public int getBaseLineCD4Count() {
        sort(this, new LabResult.LabResultComparator(false));
        return parseInt(get(0).getResult());
    }

    public LocalDate latestLabTestDate() {
        sort(this, new LabResult.LabResultComparator());
        for (LabResult result : this) {
            if (result.isCD4()) {
                return result.getTestDate();
            }
        }
        return null;
    }
}
