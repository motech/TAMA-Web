package org.motechproject.tama.web.model;

import org.joda.time.LocalDate;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.refdata.domain.Regimen;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

public class PatientSummary {

    private Patient patient;
    private TreatmentAdvice earliestTreatmentAdvice, currentTreatmentAdvice;
    private Regimen currentRegimen;
    private ClinicVisits clinicVisits;
    private String warning;

    public PatientSummary(Patient patient, TreatmentAdvice earliestTreatmentAdvice, TreatmentAdvice currentTreatmentAdvice, Regimen currentRegimen, ClinicVisits clinicVisits, String warning) {
        this.patient = patient;
        this.earliestTreatmentAdvice = earliestTreatmentAdvice;
        this.currentTreatmentAdvice = currentTreatmentAdvice;
        this.currentRegimen = currentRegimen;
        this.clinicVisits = clinicVisits;
        this.warning = warning;
    }

    public String getPatientId() {
        return patient.getPatientId();
    }

    public String getMobilePhoneNumber() {
        return patient.getMobilePhoneNumber();
    }

    public String getGender() {
        return patient.getGender().getType();
    }

    public Date getDateOfBirth() {
        return patient.getDateOfBirthAsDate();
    }

    public Date getRegistrationDate() {
        return patient.getRegistrationDateAsDate();
    }

    public Date getArtStartDate() {
        return earliestTreatmentAdvice == null ? null : earliestTreatmentAdvice.getStartDate();
    }

    public Date getCurrentRegimenStartDate() {
        return currentTreatmentAdvice == null ? null : currentTreatmentAdvice.getStartDate();
    }

    public String getCurrentARTRegimen() {
        return currentRegimen == null ? null : currentRegimen.getDisplayName();
    }

    public String getCallPlan() {
        return patient.getPatientPreferences().getDisplayCallPreference();
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public LocalDate getNextAppointmentDueDate() {
        return clinicVisits.nextAppointmentDueDate();
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public LocalDate getNextConfirmedAppointmentDate() {
        return clinicVisits.nextConfirmedAppointmentDate();
    }

    public String getWarning() {
        return warning;
    }

    public String getId(){
        return patient.getId();
    }

    public Status getStatus(){
        return patient.getStatus();
    }
}