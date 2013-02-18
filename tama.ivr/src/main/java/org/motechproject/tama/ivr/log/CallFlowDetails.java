package org.motechproject.tama.ivr.log;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.sum;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class CallFlowDetails {

    private List<Integer> accessDurations = new ArrayList<>();
    private List<String> responses = new ArrayList<>();

    public int getNumberOfTimesAccessed() {
        return accessDurations.size();
    }

    public int getTotalAccessDuration() {
        return sum(accessDurations).intValue();
    }

    public String getIndividualAccessDurations() {
        return accessDurations.isEmpty() ? "NA" : StringUtils.join(accessDurations, ", ");
    }

    public List<String> getResponses() {
        return responses;
    }

    public void flowAccessed(int flowDuration) {
        accessDurations.add(flowDuration);
    }

    public void respondedWith(List<String> responses) {
        if (isNotEmpty(responses)) {
            this.responses.addAll(responses);
        }
    }

    public List<Integer> getAllIndividualAccessDurations() {
        return accessDurations;
    }
}
