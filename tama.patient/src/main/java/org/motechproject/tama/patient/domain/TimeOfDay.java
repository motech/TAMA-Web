package org.motechproject.tama.patient.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.model.Time;
import org.motechproject.tama.common.domain.BaseEntity;

public class TimeOfDay extends BaseEntity {

    private Integer hour;

    private Integer minute;

    private TimeMeridiem timeMeridiem;

    public TimeOfDay() {
    }

    public TimeOfDay(Integer hour, Integer minute, TimeMeridiem timeMeridiem) {
        this.hour = hour;
        this.minute = minute;
        this.timeMeridiem = timeMeridiem;
    }

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

    public Time toTime() {
        Integer hour = getHour();
        if (hour == 12 && timeMeridiem.equals(TimeMeridiem.AM))
            hour = 0;
        else if (hour != 12 && timeMeridiem.equals(TimeMeridiem.PM))
            hour = (hour + 12) % 24;
        return new Time(hour, getMinute());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeOfDay timeOfDay = (TimeOfDay) o;

        if (hour != null ? !hour.equals(timeOfDay.hour) : timeOfDay.hour != null) return false;
        if (minute != null ? !minute.equals(timeOfDay.minute) : timeOfDay.minute != null) return false;
        if (timeMeridiem != timeOfDay.timeMeridiem) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = hour != null ? hour.hashCode() : 0;
        result = 31 * result + (minute != null ? minute.hashCode() : 0);
        result = 31 * result + (timeMeridiem != null ? timeMeridiem.hashCode() : 0);
        return result;
    }
}
