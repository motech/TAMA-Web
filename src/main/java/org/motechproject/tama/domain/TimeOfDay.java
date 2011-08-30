package org.motechproject.tama.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.tama.TAMAConstants.TimeMeridiem;

public class TimeOfDay extends BaseEntity {

    private Integer hour;

    private Integer minute;

    private TimeMeridiem timeMeridiem;

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public TimeMeridiem getTimeMeridiem() {
        return timeMeridiem;
    }

    public void setTimeMeridiem(TimeMeridiem timeMeridiem) {
        this.timeMeridiem = timeMeridiem;
    }

    @JsonIgnore
    public String getTimeOfDayAsString() {
        if (hour == null || minute == null) return null;
        return String.format("%02d:%02d", hour, minute);
    }

    public void setTimeOfDayAsString(String timeOfDayAsString) {
        if (StringUtils.isEmpty(timeOfDayAsString)) return;
        String[] hourMinuteArray = timeOfDayAsString.split(":");
        setHour(Integer.parseInt(hourMinuteArray[0]));
        setMinute(Integer.parseInt(hourMinuteArray[1]));
    }
}
