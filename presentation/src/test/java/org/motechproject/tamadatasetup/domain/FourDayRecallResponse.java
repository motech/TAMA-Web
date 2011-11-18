package org.motechproject.tamadatasetup.domain;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.util.DateUtil;

public class FourDayRecallResponse {
    private FourDayRecallCall[] fourDayRecallCalls;

    public FourDayRecallResponse(int[] weeklyResponses) {
        fourDayRecallCalls = new FourDayRecallCall[weeklyResponses.length];
        DateTime now = DateUtil.now();
        for (int i = 0; i < weeklyResponses.length; i++) {
            fourDayRecallCalls[i] = new FourDayRecallCall(weeklyResponses[i], now.plusWeeks(i + 1));
        }
    }

    public static FourDayRecallResponse parse(String configuredAdherenceResponse) {
        String[] adherenceResponseArray = StringUtils.split(configuredAdherenceResponse, ",");
        int[] adherenceResponses = new int[adherenceResponseArray.length];
        for (int i = 0; i < adherenceResponseArray.length; i++) {
            adherenceResponses[i] = Integer.parseInt(adherenceResponseArray[i]);
        }
        return new FourDayRecallResponse(adherenceResponses);
    }

    public FourDayRecallCall[] calls() {
        return fourDayRecallCalls;
    }
}
