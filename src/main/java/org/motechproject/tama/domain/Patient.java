package org.motechproject.tama.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.TAMAMessages;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.util.Date;

@TypeDiscriminator("doc.documentType == 'Patient'")
public class Patient extends CouchEntity {
    @NotNull
    protected String patientId;
    @NotNull
    @Pattern(regexp = TAMAConstants.PASSCODE_REGEX, message = TAMAMessages.PASSCODE_REGEX_MESSAGE)
    protected String passcode;
    @NotNull
    @Pattern(regexp = TAMAConstants.MOBILE_NUMBER_REGEX, message = TAMAMessages.MOBILE_NUMBER_REGEX_MESSAGE)
    protected String mobilePhoneNumber;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = "dd/MM/yyyy")
    @Past(message = TAMAMessages.DATE_OF_BIRTH_MUST_BE_IN_PAST)
    @NotNull
    protected Date dateOfBirth;
    @ManyToOne
    private Gender gender;
    @ManyToOne
    private Clinic clinic;
    @ManyToOne
    private IVRLanguage ivrLanguage;

    private ReminderCall reminderCall = ReminderCall.Daily;
    private Status status = Status.Inactive;
    private int travelTimeToClinicInDays;
    private int travelTimeToClinicInHours;
    private int travelTimeToClinicInMinutes;
    private Date registrationDate;
    private String genderId;
    private String ivrLanguageId;
    private String clinic_id;

    @JsonIgnore
    public boolean isActive() {
        return this.status.equals(Status.Active);
    }

    @JsonIgnore
    public boolean isNotActive() {
        return this.status.equals(Status.Inactive);
    }

    public boolean hasPasscode(String passcode) {
        return this.passcode.equals(passcode);
    }

    @JsonIgnore
    public String getIVRMobilePhoneNumber() {
        return String.format("0%s", mobilePhoneNumber);
    }

    public enum ReminderCall {
        Daily, Weekly
    }

    public enum Status {
        Inactive, Active
    }

    public Patient activate() {
        this.status = Status.Active;
        return this;
    }

    public String getPatientId() {
        return this.patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPasscode() {
        return this.passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public ReminderCall getReminderCall() {
        return this.reminderCall;
    }

    public void setReminderCall(ReminderCall reminderCall) {
        this.reminderCall = reminderCall;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMobilePhoneNumber() {
        return this.mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getTravelTimeToClinicInDays() {
        return this.travelTimeToClinicInDays;
    }

    public void setTravelTimeToClinicInDays(int travelTimeToClinicInDays) {
        this.travelTimeToClinicInDays = travelTimeToClinicInDays;
    }

    public int getTravelTimeToClinicInHours() {
        return this.travelTimeToClinicInHours;
    }

    public void setTravelTimeToClinicInHours(int travelTimeToClinicInHours) {
        this.travelTimeToClinicInHours = travelTimeToClinicInHours;
    }

    public int getTravelTimeToClinicInMinutes() {
        return this.travelTimeToClinicInMinutes;
    }

    public void setTravelTimeToClinicInMinutes(int travelTimeToClinicInMinutes) {
        this.travelTimeToClinicInMinutes = travelTimeToClinicInMinutes;
    }

    public Date getRegistrationDate() {
        if (this.registrationDate == null) {
            this.registrationDate = new Date();
        }
        return this.registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        if (registrationDate != null) {
            this.registrationDate = registrationDate;
        }
    }

    @JsonIgnore
    public String getGenderType() {
        if (this.getGender() != null) return this.getGender().getType();
        return null;
    }

    @JsonIgnore
    public Gender getGender() {
        return this.gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
        this.genderId = gender.getId();
    }

    @JsonIgnore
    public IVRLanguage getIvrLanguage() {
        return this.ivrLanguage;
    }

    public void setIvrLanguage(IVRLanguage ivrLanguage) {
        this.ivrLanguage = ivrLanguage;
        this.ivrLanguageId = ivrLanguage.getId();
    }

    @JsonIgnore
    public Clinic getClinic() {
        return this.clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
        this.clinic_id = clinic.getId();
    }

    public String getGenderId() {
        return genderId;
    }

    public void setGenderId(String genderId) {
        this.genderId = genderId;
    }

    public String getIvrLanguageId() {
        return ivrLanguageId;
    }

    public void setIvrLanguageId(String ivrLanguageId) {
        this.ivrLanguageId = ivrLanguageId;
    }

    public String getClinic_id() {
        return clinic_id;
    }

    public void setClinic_id(String clinic_id) {
        this.clinic_id = clinic_id;
    }


}
