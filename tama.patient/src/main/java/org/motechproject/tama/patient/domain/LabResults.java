package org.motechproject.tama.patient.domain;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class LabResults extends ArrayList<LabResult> {

    public LabResults() {
    }

    public LabResults(Collection<? extends LabResult> c) {
        super(c);
    }

    public int latestCountOf(TAMAConstants.LabTestType type) {
        LabResult labResult = getLatestResult(type);
        return labResult == null ? LabResult.INVALID_COUNT : Integer.parseInt(labResult.getResult());
    }

    public LabResult latestResultOf(TAMAConstants.LabTestType type) {
        return getLatestResult(type);
    }

    private LabResult getLatestResult(TAMAConstants.LabTestType type) {
        ArrayList<LabResult> results = new ArrayList<>(this);
        Collections.sort(results, new LabResult.LabResultComparator(false));
        for (LabResult result : results) {
            if (StringUtils.equals(result.getLabTest().getName(), type.getName())) {
                return result;
            }
        }
        return null;
    }


    public LocalDate latestLabTestDateOf(TAMAConstants.LabTestType type) {
        ArrayList<LabResult> results = new ArrayList<>(this);
        Collections.sort(results, new LabResult.LabResultComparator(false));
        for (LabResult result : results) {
            if (StringUtils.equals(result.getLabTest().getName(), type.getName())) {
                return result.getTestDate();
            }
        }
        return null;
    }

    public int baselineCountOf(TAMAConstants.LabTestType type) {
        ArrayList<LabResult> results = new ArrayList<>(this);
        Collections.sort(results, new LabResult.LabResultComparator(true));
        for (LabResult result : results) {
            if (StringUtils.equals(result.getLabTest().getName(), type.getName())) {
                return Integer.parseInt(result.getResult());
            }
        }
        return LabResult.INVALID_COUNT;
    }
}
