package org.motechproject.tama.ivr.log;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.sum;

public class CallFlowDetails {

    private List<Integer> accessDurations = new ArrayList<Integer>();

    public int getNumberOfTimesAccessed() {
        return accessDurations.size();
    }

    public int getTotalAccessDuration() {
        return sum(accessDurations).intValue();
    }

    public String getIndividualAccessDurations() {
        return accessDurations.isEmpty() ? "NA" : StringUtils.join(accessDurations, ", ");
    }

    public void flowAccessed(int flowDuration) {
        accessDurations.add(flowDuration);
    }
}
