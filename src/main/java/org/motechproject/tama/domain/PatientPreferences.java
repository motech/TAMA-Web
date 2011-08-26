package org.motechproject.tama.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.TAMAConstants.ReminderCall;
import org.motechproject.tama.TAMAMessages;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class PatientPreferences extends BaseEntity {

    @NotNull
    @Pattern(regexp = TAMAConstants.PASSCODE_REGEX, message = TAMAMessages.PASSCODE_REGEX_MESSAGE)
    protected String passcode;

    @ManyToOne
    private IVRLanguage ivrLanguage;

    private String ivrLanguageId;

    private ReminderCall reminderCall = ReminderCall.Daily;

    public ReminderCall getReminderCall() {
        return this.reminderCall;
    }

    public void setReminderCall(ReminderCall reminderCall) {
        this.reminderCall = reminderCall;
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
}
