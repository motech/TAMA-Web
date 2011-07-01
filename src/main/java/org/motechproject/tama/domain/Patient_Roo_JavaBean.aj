// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama.domain;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.tama.domain.Patient.ReminderCall;

privileged aspect Patient_Roo_JavaBean {
    
    public String Patient.getPatientId() {
        return this.patientId;
    }
    
    public void Patient.setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String Patient.getPasscode() {
        return this.passcode;
    }

    public void Patient.setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public ReminderCall Patient.getReminderCall() {
        return this.reminderCall;
    }

    public void Patient.setReminderCall(ReminderCall reminderCall) {
        this.reminderCall = reminderCall;
    }

    public Status Patient.getStatus() {
        return this.status;
    }

    public void Patient.setStatus(Status status) {
        this.status = status;
    }

    public String Patient.getMobilePhoneNumber() {
        return this.mobilePhoneNumber;
    }
    
    public void Patient.setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }
    
    public Date Patient.getDateOfBirth() {
        return this.dateOfBirth;
    }
    
    public void Patient.setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public int Patient.getTravelTimeToClinicInDays() {
        return this.travelTimeToClinicInDays;
    }
    
    public void Patient.setTravelTimeToClinicInDays(int travelTimeToClinicInDays) {
        this.travelTimeToClinicInDays = travelTimeToClinicInDays;
    }
    
    public int Patient.getTravelTimeToClinicInHours() {
        return this.travelTimeToClinicInHours;
    }
    
    public void Patient.setTravelTimeToClinicInHours(int travelTimeToClinicInHours) {
        this.travelTimeToClinicInHours = travelTimeToClinicInHours;
    }
    
    public int Patient.getTravelTimeToClinicInMinutes() {
        return this.travelTimeToClinicInMinutes;
    }
    
    public void Patient.setTravelTimeToClinicInMinutes(int travelTimeToClinicInMinutes) {
        this.travelTimeToClinicInMinutes = travelTimeToClinicInMinutes;
    }

    @JsonIgnore
    public String Patient.getGenderType() {
        if (this.getGender() != null) return this.getGender().getType();
        return null;
    }

    @JsonIgnore
    public Gender Patient.getGender() {
       if (this.gender != null) return this.gender;
       if (this.genderId != null) return Gender.findGender(genderId);
       return null;
    }
    
    public void Patient.setGender(Gender gender) {
        this.gender = gender;
        this.genderId = gender.getId();
    }

    public Date Patient.getRegistrationDate() {
        if (this.registrationDate == null) {
            this.registrationDate = new Date();
        }
        return this.registrationDate;
    }

    public void Patient.setRegistrationDate(Date registrationDate) {
        if (registrationDate != null) {
            this.registrationDate = registrationDate;
        }
    }

    @JsonIgnore
    public IVRLanguage Patient.getIvrLanguage() {
       if (this.ivrLanguage != null) return this.ivrLanguage;
       if (this.ivrLanguageId != null) return IVRLanguage.findIVRLanguage(ivrLanguageId);
       return null;
    }

    public void Patient.setIvrLanguage(IVRLanguage ivrLanguage) {
        this.ivrLanguage = ivrLanguage;
        this.ivrLanguageId = ivrLanguage.getId();
    }

    @JsonIgnore
    public Doctor Patient.getPrincipalDoctor() {
        if (this.principalDoctor != null) return this.principalDoctor;
        if(this.doctorId != null) return Doctor.findDoctor(doctorId);
        return null;
    }
    
    public void Patient.setPrincipalDoctor(Doctor principalDoctor) {
        this.principalDoctor = principalDoctor;
        this.doctorId = principalDoctor.getId();
    }
    
}
