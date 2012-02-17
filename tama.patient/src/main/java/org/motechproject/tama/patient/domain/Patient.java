package org.motechproject.tama.patient.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.TAMAMessages;
import org.motechproject.tama.common.domain.*;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.refdata.domain.Gender;
import org.motechproject.util.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@TypeDiscriminator("doc.documentType == 'Patient'")
public class Patient extends CouchEntity {
    public static final String CLINIC_AND_PATIENT_ID_UNIQUE_CONSTRAINT = "Constraint:Unique:Clinic/PatientId::";
    public static final String PHONE_NUMBER_AND_PASSCODE_UNIQUE_CONSTRAINT = "Constraint:Unique:PhoneNumber/Passcode::";

    protected String patientId;
    @NotNull
    @Pattern(regexp = TAMAConstants.MOBILE_NUMBER_REGEX, message = TAMAMessages.MOBILE_NUMBER_REGEX_MESSAGE)
    protected String mobilePhoneNumber;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    @Past(message = TAMAMessages.DATE_OF_BIRTH_MUST_BE_IN_PAST)
    @NotNull
    protected Date dateOfBirthAsDate;
    @ManyToOne
    private Gender gender;
    @ManyToOne
    private Clinic clinic;

    private String genderId;
    private String clinic_id;

    @Valid
    private PatientPreferences patientPreferences = new PatientPreferences();
    private MedicalHistory medicalHistory;
    private Status status = Status.Inactive;
    private String notes;

    private int travelTimeToClinicInDays;
    private int travelTimeToClinicInHours;
    private int travelTimeToClinicInMinutes;

    private Date registrationDateAsDate;
    private DateTime lastSuspendedDate;
    private DateTime activationDate;
    private DateTime lastDeactivationDate;

    @JsonIgnore
    public boolean allowAdherenceCalls() {
        return status.isActive();
    }

    @JsonIgnore
    public boolean allowOutboxCalls() {
        return status.isActive() || status.isSuspended();
    }

    @JsonIgnore
    public boolean allowIncomingCalls() {
        return status.isActive() || status.isSuspended();
    }

    @JsonIgnore
    public boolean isOnDailyPillReminder() {
        return callPreference().isDaily();
    }

    @JsonIgnore
    public boolean isOnWeeklyPillReminder() {
        return !isOnDailyPillReminder();
    }

    @JsonIgnore
    public boolean hasAgreedToBeCalledAtBestCallTime() {
        return this.patientPreferences.hasAgreedToBeCalledAtBestCallTime();
    }

    @JsonIgnore
    public boolean hasAgreedToReceiveOTCAdvice() {
        return this.patientPreferences.getReceiveOTCAdvice();
    }

    @JsonIgnore
    public CallPreference callPreference() {
        return this.patientPreferences.getCallPreference();
    }

    @JsonIgnore
    public org.motechproject.tama.common.domain.TimeOfDay getBestCallTime() {
        return this.patientPreferences.getBestCallTime();
    }

    @JsonIgnore
    public DayOfWeek getDayOfWeeklyCall() {
        return this.patientPreferences.getDayOfWeeklyCall();
    }

    public String getPatientId() {
        return this.patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
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

    public LocalDate getDateOfBirth() {
        return DateUtil.newDate(dateOfBirthAsDate);
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirthAsDate = toDate(dateOfBirth);
    }

    @JsonIgnore
    public Date getDateOfBirthAsDate() {
        return dateOfBirthAsDate;
    }

    public void setDateOfBirthAsDate(Date dateOfBirth) {
        this.dateOfBirthAsDate = dateOfBirth;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public MedicalHistory getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(MedicalHistory medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public PatientPreferences getPatientPreferences() {
        return patientPreferences;
    }

    public void setPatientPreferences(PatientPreferences patientPreferences) {
        this.patientPreferences = patientPreferences;
    }

    @JsonIgnore
    public Date getRegistrationDateAsDate() {
        return registrationDateAsDate;
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

    public String getClinic_id() {
        return clinic_id;
    }

    public void setClinic_id(String clinic_id) {
        this.clinic_id = clinic_id;
    }

    public List<String> uniqueFields() {
        return Arrays.asList(clinicAndPatientId(), phoneNumberAndPasscode());
    }

    public String clinicAndPatientId() {
        return CLINIC_AND_PATIENT_ID_UNIQUE_CONSTRAINT + this.getClinic_id() + "/" + this.getPatientId();
    }

    public String phoneNumberAndPasscode() {
        return PHONE_NUMBER_AND_PASSCODE_UNIQUE_CONSTRAINT + this.getMobilePhoneNumber() + "/" + this.getPatientPreferences().getPasscode();
    }

    //Sets on patient creation.
    public LocalDate getRegistrationDate() {
        if (registrationDateAsDate == null) {
            this.registrationDateAsDate = toDate(DateUtil.today());
        }
        return DateUtil.newDate(registrationDateAsDate);
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        if (registrationDate != null) {
            this.registrationDateAsDate = toDate(registrationDate);
        }
    }

    public DateTime getActivationDate() {
        return activationDate == null ? null : DateUtil.setTimeZone(activationDate);
    }

    public void setActivationDate(DateTime activationDate) {
        if (this.activationDate == null) {
            this.activationDate = activationDate;
        }
    }

    public DateTime getLastDeactivationDate() {
        return lastDeactivationDate == null ? null : DateUtil.setTimeZone(lastDeactivationDate);
    }

    public void setLastDeactivationDate(DateTime lastDeactivationDate) {
        this.lastDeactivationDate = lastDeactivationDate;
    }

    public DateTime getLastSuspendedDate() {
        return lastSuspendedDate == null ? null : DateUtil.setTimeZone(lastSuspendedDate);
    }

    public void setLastSuspendedDate(DateTime lastSuspendedDate) {
        this.lastSuspendedDate = lastSuspendedDate;
    }

    public Patient deactivate(Status status) {
        this.status = status;
        setLastDeactivationDate(DateUtil.now());
        return this;
    }

    public Patient activate() {
        this.status = Status.Active;
        setActivationDate(DateUtil.now());
        return this;
    }

    @JsonIgnore
    public String getDisplayableSuspendedDateAndTime() {
        DateTimeFormatter timeFormatter = org.joda.time.format.DateTimeFormat.forPattern("HH:mm");
        DateTimeFormatter dateFormatter = org.joda.time.format.DateTimeFormat.forPattern("EEE MMM dd YYYY");
        return getLastSuspendedDate() == null ? null : dateFormatter.print(getLastSuspendedDate()) + ", at " + timeFormatter.print(getLastSuspendedDate().toLocalTime());
    }

    @JsonIgnore
    public int getAge() {
        Period period = new Period(getDateOfBirth(), DateUtil.today(), PeriodType.years());
        return period.getYears();
    }

    @JsonIgnore
    public void suspend() {
        setStatus(Status.Suspended);
        setLastSuspendedDate(DateUtil.now());
    }

    @JsonIgnore
    public boolean canTransitionToWeekly(int minNumberOfDaysOnDailyBeforeTransitioningToWeekly) {
        if (getActivationDate() == null)
            return false;
        boolean moreThan4WeeksAfterActivation = Days.daysBetween(getActivationDate(), DateUtil.now()).getDays() >= minNumberOfDaysOnDailyBeforeTransitioningToWeekly;
        return moreThan4WeeksAfterActivation && patientPreferences.getCallPreference().isDaily();
    }
}