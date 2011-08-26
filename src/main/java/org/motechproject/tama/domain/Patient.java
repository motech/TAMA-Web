package org.motechproject.tama.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.TAMAMessages;
import org.motechproject.util.DateUtil;
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

    private MedicalHistory medicalHistory;
    private PatientPreferences patientPreferences;

    private Status status = Status.Inactive;
    private int travelTimeToClinicInDays;
    private int travelTimeToClinicInHours;
    private int travelTimeToClinicInMinutes;
    private Date registrationDateAsDate;
    private String genderId;
    private String clinic_id;

    @JsonIgnore
    public boolean isActive() {
        return this.status.equals(Status.Active);
    }

    @JsonIgnore
    public boolean isNotActive() {
        return this.status.equals(Status.Inactive);
    }

    public boolean authenticatedWith(String passcode) {
        return this.patientPreferences.getPasscode().equals(passcode);
    }

    @JsonIgnore
    public String getIVRMobilePhoneNumber() {
        return String.format("0%s", mobilePhoneNumber);
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

    public String uniqueId() {
        return this.getClinic_id() + '_' + this.getPatientId();
    }

    @JsonIgnore
    public IVRLanguage getIvrLanguage() {
        return this.patientPreferences.getIvrLanguage();
    }

    @JsonIgnore
    public void setIvrLanguage(IVRLanguage ivrLanguage) {
        this.patientPreferences.setIvrLanguage(ivrLanguage);
    }

    @JsonIgnore
    public String getIvrLanguageId() {
        return this.patientPreferences.getIvrLanguageId();
    }
}
