package org.motechproject.tama.patient.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.TAMAMessages;
import org.motechproject.tama.common.domain.BaseEntity;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.refdata.domain.IVRLanguage;

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

    private CallPreference callPreference;

    private DayOfWeek dayOfWeeklyCall;

    private TimeOfDay bestCallTime;

    private boolean receiveOTCAdvice = false;

    private boolean activateAppointmentReminders = true;

    private DateTime callPreferenceTransitionDate;

    public CallPreference getCallPreference() {
        return this.callPreference;
    }

    @JsonIgnore
    public String getDisplayCallPreference() {
        return this.callPreference.displayName();
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

    public DayOfWeek getDayOfWeeklyCall() {
        return dayOfWeeklyCall;
    }

    public void setDayOfWeeklyCall(DayOfWeek dayOfWeeklyCall) {
        this.dayOfWeeklyCall = dayOfWeeklyCall;
    }

    public TimeOfDay getBestCallTime() {
        return bestCallTime;
    }

    public void setBestCallTime(TimeOfDay bestCallTime) {
        this.bestCallTime = bestCallTime;
    }


    public boolean hasAgreedToBeCalledAtBestCallTime() {
        return bestCallTime != null && bestCallTime.getHour() != null;
    }

    public DateTime getCallPreferenceTransitionDate() {
        return callPreferenceTransitionDate;
    }

    public void setCallPreferenceTransitionDate(DateTime callPreferenceTransitionDate) {
        this.callPreferenceTransitionDate = callPreferenceTransitionDate;
    }

    public boolean getReceiveOTCAdvice() {
        return receiveOTCAdvice;
    }

    public void setReceiveOTCAdvice(boolean receiveOTCAdvice) {
        this.receiveOTCAdvice = receiveOTCAdvice;
    }

    public boolean getActivateAppointmentReminders() {
        return activateAppointmentReminders;
    }

    public void setActivateAppointmentReminders(boolean activateAppointmentReminders) {
        this.activateAppointmentReminders = activateAppointmentReminders;
    }
}
