package org.motechproject.tama.ivr.builder;

import org.joda.time.DateTime;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.util.DateUtil;
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
        List<String> wavs = new ArrayList<String>();
        wavs.add(ivrMessage.getWav(String.valueOf(time.getHourOfDay() % 12)));
        wavs.add(ivrMessage.getWav(String.valueOf(time.getMinuteOfHour())));
        wavs.add(ivrMessage.getWav(time.getHourOfDay() < 12 ? IVRMessage.IN_THE_MORNING : IVRMessage.IN_THE_EVENING));
        wavs.add(ivrMessage.getWav(time.toLocalDate().equals(DateUtil.today()) ? IVRMessage.TODAY : IVRMessage.TOMORROW));
        return wavs;
    }
}
