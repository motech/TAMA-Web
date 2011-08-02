package org.motechproject.tama.ivr.builder;

import org.joda.time.DateTime;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.util.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IVRMessageBuilder {

    IVRMessage ivrMessage;

    @Autowired
    public IVRMessageBuilder(IVRMessage ivrMessage) {
        this.ivrMessage = ivrMessage;
    }

    public List<String> getWavs(DateTime time) {
        DateTime now = DateUtility.getDateTime();
        List<String> wavs = new ArrayList<String>();
        wavs.add(ivrMessage.getWav(String.valueOf(now.getHourOfDay())));
        wavs.add(ivrMessage.getWav(String.valueOf(now.getMinuteOfHour())));
        wavs.add(ivrMessage.getWav(time.getHourOfDay() < 12? IVRMessage.IN_THE_MORNING : IVRMessage.IN_THE_EVENING));
        wavs.add(ivrMessage.getWav(time.getDayOfMonth() == now.getDayOfMonth()? IVRMessage.TODAY : IVRMessage.TOMORROW));
        return wavs;
    }
}
