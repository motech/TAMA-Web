package org.motechproject.tama.clinicvisits.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.tama.common.TAMAConstants;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.List;


public class ClinicVisit {

    public enum TypeOfVisit {
        Baseline,
        Scheduled,
        Unscheduled
    }

    public static final String TREATMENT_ADVICE = "TreatmentAdviceId";
    public static final String VITAL_STATISTICS = "VitalStatisticsId";
    public static final String LAB_RESULTS = "LabResultIds";
    public static final String TYPE_OF_VISIT = "TypeOfVisit";
    public static final String ADJUSTED_DUE_DATE = "AdjustedDueDate";

    private Visit visit = new Visit();

    private Appointment appointment = new Appointment();

    public ClinicVisit() {
    }

    public ClinicVisit(Visit visit, Appointment appointment) {
        this.visit = visit;
        this.appointment = appointment;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public String getId() {
        return visit.getId();
    }

    public String getName() {
        return visit.getTitle();
    }

    public void setName(String name) {
        visit.setTitle(name);
    }

    public String getPatientId() {
        return visit.getExternalId();
    }

    public void setPatientId(String patientId) {
        visit.setExternalId(patientId);
    }

    public TypeOfVisit getTypeOfVisit() {
        return (TypeOfVisit) visit.getData().get(TYPE_OF_VISIT);
    }

    public void setTypeOfVisit(TypeOfVisit typeOfVisit) {
        visit.addData(TYPE_OF_VISIT, typeOfVisit);
    }

    public String getTreatmentAdviceId() {
        return (String) visit.getData().get(TREATMENT_ADVICE);
    }

    public void setTreatmentAdviceId(String treatmentAdviceId) {
        visit.addData(TREATMENT_ADVICE, treatmentAdviceId);
    }

    public List<String> getLabResultIds() {
        return (List<String>) visit.getData().get(LAB_RESULTS);
    }

    public void setLabResultIds(List<String> labResultIds) {
        visit.addData(LAB_RESULTS, labResultIds);
    }

    public String getVitalStatisticsId() {
        return (String) visit.getData().get(VITAL_STATISTICS);
    }

    public void setVitalStatisticsId(String vitalStatisticsId) {
        visit.addData(VITAL_STATISTICS, vitalStatisticsId);
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public DateTime getVisitDate() {
        return visit.getVisitDate();
    }

    public void setVisitDate(DateTime visitDate) {
        visit.setVisitDate(visitDate);
    }

    public String getAppointmentId() {
        return visit.getAppointmentId();
    }

    public void setAppointmentId(String appointmentId) {
        visit.setAppointmentId(appointmentId);
    }

    public boolean isMissed() {
        return visit.isMissed();
    }

    public void setMissed(boolean missed) {
        visit.setMissed(missed);
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public DateTime getAppointmentDueDate() {
        return appointment.getDueDate();
    }

    public void setAppointmentDueDate(DateTime appointmentDueDate) {
        appointment.setDueDate(appointmentDueDate);
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public LocalDate getAdjustedDueDate() {
        return (LocalDate) appointment.getData().get(ADJUSTED_DUE_DATE);
    }

    public void setAdjustedDueDate(LocalDate adjustedDueDate) {
        appointment.addData(ADJUSTED_DUE_DATE, adjustedDueDate);
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATETIME_FORMAT)
    public DateTime getConfirmedVisitDate() {
        return appointment.getScheduledDate();
    }

    public void setConfirmedVisitDate(DateTime scheduledDate) {
        appointment.setScheduledDate(scheduledDate);
    }
}
