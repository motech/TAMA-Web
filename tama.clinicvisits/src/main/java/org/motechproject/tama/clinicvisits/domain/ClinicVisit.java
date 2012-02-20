package org.motechproject.tama.clinicvisits.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.tama.common.TAMAConstants;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Collections;
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

    private String patientDocId;
    private Visit visit = new Visit();

    public ClinicVisit() {
    }

    public ClinicVisit(String patientDocId, Visit visit) {
        this.patientDocId = patientDocId;
        this.visit = visit;
    }

    public Appointment getAppointment() {
        return visit.appointment();
    }

    public Visit getVisit() {
        return visit;
    }

    public String getId() {
        return visit.id();
    }

    public String getName() {
        return visit.title();
    }

    public void setName(String name) {
        visit.title(name);
    }

    public String getPatientId() {
        return patientDocId;
    }

    public void setPatientId(String patientDocId) {
        this.patientDocId = patientDocId;
    }

    public TypeOfVisit getTypeOfVisit() {
        return TypeOfVisit.valueOf((String) visit.getData().get(TYPE_OF_VISIT));
    }

    public void setTypeOfVisit(TypeOfVisit typeOfVisit) {
        visit.addData(TYPE_OF_VISIT, typeOfVisit.toString());
    }

    public String getTreatmentAdviceId() {
        return (String) visit.getData().get(TREATMENT_ADVICE);
    }

    public void setTreatmentAdviceId(String treatmentAdviceId) {
        visit.addData(TREATMENT_ADVICE, treatmentAdviceId);
    }

    public List<String> getLabResultIds() {
        List<String> labResultIds = (List<String>) visit.getData().get(LAB_RESULTS);
        return labResultIds == null ? Collections.<String>emptyList() :labResultIds;
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
        return visit.visitDate();
    }

    public void setVisitDate(DateTime visitDate) {
        visit.visitDate(visitDate);
    }

    public boolean isMissed() {
        return visit.missed();
    }

    public void setMissed(boolean missed) {
        visit.missed(missed);
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public DateTime getAppointmentDueDate() {
        return visit.appointment().dueDate();
    }

    public void setAppointmentDueDate(DateTime appointmentDueDate) {
        visit.appointment().dueDate(appointmentDueDate);
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public LocalDate getAdjustedDueDate() {
        return (LocalDate) visit.appointment().getData().get(ADJUSTED_DUE_DATE);
    }

    public void setAdjustedDueDate(LocalDate adjustedDueDate) {
        visit.appointment().addData(ADJUSTED_DUE_DATE, adjustedDueDate);
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATETIME_FORMAT)
    public DateTime getConfirmedVisitDate() {
        return visit.appointment().scheduledDate();
    }

    public void setConfirmedVisitDate(DateTime scheduledDate) {
        visit.appointment().scheduledDate(scheduledDate);
    }
}
