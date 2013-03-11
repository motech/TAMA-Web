package org.motechproject.tama.web.model;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientEventLog;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.refdata.domain.Regimen;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class PatientSummary {

    public static final String NOT_APPLICABLE = "NA";
    private Patient patient;
    private TreatmentAdvice earliestTreatmentAdvice, currentTreatmentAdvice;
    private Regimen currentRegimen;
    private ClinicVisits clinicVisits;
    private List<String> warning;
    private List<PatientEventLog> statusChangeHistory;
    private Double runningAdherencePercentage;

    public PatientSummary(Patient patient, TreatmentAdvice earliestTreatmentAdvice, TreatmentAdvice currentTreatmentAdvice,
                          Regimen currentRegimen, ClinicVisits clinicVisits, List<PatientEventLog> statusChangeHistory,
                          Double runningAdherencePercentage, List<String> warning) {
        this.patient = patient;
        this.earliestTreatmentAdvice = earliestTreatmentAdvice;
        this.currentTreatmentAdvice = currentTreatmentAdvice;
        this.currentRegimen = currentRegimen;
        this.clinicVisits = clinicVisits;
        this.runningAdherencePercentage = runningAdherencePercentage;
        this.setStatusChangeHistory(statusChangeHistory);
        this.warning = warning;
    }

    private PatientSummary setStatusChangeHistory(List<PatientEventLog> statusChangeHistory) {
        this.statusChangeHistory = statusChangeHistory;
        Collections.sort(this.statusChangeHistory, new Comparator<PatientEventLog>() {
            @Override
            public int compare(PatientEventLog one, PatientEventLog two) {
                return two.getDate().compareTo(one.getDate());
            }
        });
        return this;
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
        return getDateOfBirthAsDate();
    }

    public Date getDateOfBirthAsDate() {
        return patient.getDateOfBirthAsDate();
    }

    public Date getRegistrationDate() {
        return getRegistrationDateAsDate();
    }

    public Date getRegistrationDateAsDate() {
        return patient.getRegistrationDateAsDate();
    }

    public Date getArtStartDate() {
        return earliestTreatmentAdvice == null ? null : earliestTreatmentAdvice.getStartDate();
    }

    public Date getCurrentRegimenStartDate() {
        return currentTreatmentAdvice == null ? null : currentTreatmentAdvice.getStartDate();
    }

    public String getPillTimes() {
        return currentTreatmentAdvice == null ? "" : StringUtils.join(currentTreatmentAdvice.distinctDrugTimes(), ", ");
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

    public List<String> getWarning() {
        return warning;
    }

    public String getId() {
        return patient.getId();
    }

    public Status getStatus() {
        return patient.getStatus();
    }

    public List<PatientEventLog> getStatusChangeHistory() {
        return statusChangeHistory;
    }

    public String getRunningAdherencePercentage() {
        return String.format("%2.0f%%", runningAdherencePercentage);
    }

    public String getBestCallTime() {
        TimeOfDay bestCallTime = patient.getBestCallTime();
        return (null == bestCallTime || patient.isOnDailyPillReminder()) ? NOT_APPLICABLE : bestCallTime.toString();
    }

    public String getDayOfWeeklyCall() {
        DayOfWeek dayOfWeeklyCall = patient.getDayOfWeeklyCall();
        return dayOfWeeklyCall == null ? NOT_APPLICABLE : patient.getDayOfWeeklyCall().name();
    }
}