package org.motechproject.tamadatasetup.domain;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;

public class FourDayRecallPatientSchedule {
    private int[] responses;
    private int index;
    private FourDayRecallSetupConfiguration configuration;

    public FourDayRecallPatientSchedule(FourDayRecallSetupConfiguration configuration) {
        this.configuration = configuration;
        responses = parse(configuration.adherenceResponse());
    }

    public int numberOfWeeks() {
        return responses.length;
    }

    private int[] parse(String configuredAdherenceResponse) {
        String[] adherenceResponseArray = StringUtils.split(configuredAdherenceResponse, ",");
        int[] adherenceResponses = new int[adherenceResponseArray.length];
        for (int i = 0; i < adherenceResponseArray.length; i++) {
            adherenceResponses[i] = Integer.parseInt(adherenceResponseArray[i]);
        }
        return adherenceResponses;
    }

    public FourDayRecallPatientEvents events() {
        LocalDate callDate = configuration.treatmentAdviceGivenDate().plusWeeks(index + 1 + configuration.startFromWeeksAfterTreatmentAdvice());
        DateTime callTime = DateUtil.newDateTime(callDate, configuration.bestCallTime());
        boolean runFallingTrendJob = index + configuration.startFromWeeksAfterTreatmentAdvice() > 0;
        FourDayRecallPatientEvents fourDayRecallPatientEvents = new FourDayRecallPatientEvents(responses[index], callTime, runFallingTrendJob);
        index++;
        return fourDayRecallPatientEvents;
    }
}
