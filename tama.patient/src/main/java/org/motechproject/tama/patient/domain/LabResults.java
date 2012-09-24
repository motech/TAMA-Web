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
        LabResult labResult = latestCD4Result();
        return labResult == null ? LabResult.INVALID_CD4_COUNT : Integer.parseInt(labResult.getResult());
    }

    public LabResult latestCD4Result() {
        ArrayList<LabResult> results = new ArrayList<LabResult>(this);
        Collections.sort(results, new LabResult.LabResultComparator(false));
        for (LabResult result : results) {
            if (result.isCD4()) {
                return result;
            }
        }
        return null;
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
