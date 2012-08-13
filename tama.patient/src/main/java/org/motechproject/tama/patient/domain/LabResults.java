package org.motechproject.tama.patient.domain;

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
        ArrayList<LabResult> results = new ArrayList<LabResult>(this);
        Collections.sort(results, new LabResult.LabResultComparator(false));
        for (LabResult result : results) {
            if (result.isCD4()) {
                return Integer.parseInt(result.getResult());
            }
        }
        return LabResult.INVALID_CD4_COUNT;
    }

    public LocalDate latestLabTestDate() {
        ArrayList<LabResult> results = new ArrayList<LabResult>(this);
        Collections.sort(results, new LabResult.LabResultComparator(false));
        for (LabResult result : results) {
            if (result.isCD4()) {
                return result.getTestDate();
            }
        }
        return null;
    }

    public int baselineCD4Count() {
        ArrayList<LabResult> results = new ArrayList<LabResult>(this);
        Collections.sort(results, new LabResult.LabResultComparator(true));
        for (LabResult result : results) {
            if (result.isCD4()) {
                return Integer.parseInt(result.getResult());
            }
        }
        return LabResult.INVALID_CD4_COUNT;
    }
}
