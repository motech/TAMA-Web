package org.motechproject.tama.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.TAMAConstants.CallPreference;
import org.motechproject.tama.TAMAMessages;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class PatientPreferences extends BaseEntity {

    @NotNull
    @Pattern(regexp = TAMAConstants.PASSCODE_REGEX, message = TAMAMessages.PASSCODE_REGEX_MESSAGE)
    protected String passcode;

    @ManyToOne
    private IVRLanguage ivrLanguage; // TODO: should probably be removed since Id is present

    private String ivrLanguageId;

    private TAMAConstants.CallPreference callPreference = CallPreference.DailyPillReminder;

    private TAMAConstants.DayOfWeek dayOfWeeklyCall;

    private TimeOfDay bestCallTime;

    public TAMAConstants.CallPreference getCallPreference() {
        return this.callPreference;
    }

    public String getDisplayCallPreference() {
        return this.callPreference == CallPreference.DailyPillReminder? "Daily" : "Weekly";
    }

    public void setCallPreference(CallPreference callPreference) {
        this.callPreference = callPreference;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    @JsonIgnore
    public IVRLanguage getIvrLanguage() {
        return this.ivrLanguage;
    }

    public void setIvrLanguage(IVRLanguage ivrLanguage) {
        this.ivrLanguage = ivrLanguage;
        this.ivrLanguageId = ivrLanguage.getId();
    }

    public String getIvrLanguageId() {
        return ivrLanguageId;
    }

    public void setIvrLanguageId(String ivrLanguageId) {
        this.ivrLanguageId = ivrLanguageId;
    }

    public TAMAConstants.DayOfWeek getDayOfWeeklyCall() {
        return dayOfWeeklyCall;
    }

    public void setDayOfWeeklyCall(TAMAConstants.DayOfWeek dayOfWeeklyCall) {
        this.dayOfWeeklyCall = dayOfWeeklyCall;
    }

    public TimeOfDay getBestCallTime() {
        return bestCallTime;
    }

    public void setBestCallTime(TimeOfDay bestCallTime) {
        this.bestCallTime = bestCallTime;
    }


}
