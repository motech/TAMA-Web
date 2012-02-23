package org.motechproject.tama.clinicvisits.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.Patient;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Collections;
import java.util.List;


public class ClinicVisit implements Comparable<ClinicVisit> {

    public static final String TREATMENT_ADVICE = "TreatmentAdviceId";
    public static final String VITAL_STATISTICS = "VitalStatisticsId";
    public static final String LAB_RESULTS = "LabResultIds";
    public static final String ADJUSTED_DUE_DATE = "AdjustedDueDate";

    private Patient patient;
    private Visit visit = new Visit();

    public ClinicVisit() {
    }

    public ClinicVisit(Patient patient, Visit visit) {
        this.patient = patient;
        this.visit = visit;
    }

    public Visit getVisit() {
        return visit;
    }

    public String getId() {
        return visit.name();
    }

    public void setId(String id) {
        visit.name(id);
    }

    public String getTitle() {
        if (visit.typeOfVisit().isBaselineVisit())
            return "Registered with TAMA";
        else if (visit.weekNumber() != null)
            return weekNumber() + " weeks Follow-up visit";
        else
            return "Unscheduled Visit";
    }

    public Patient getPatient() {
        return patient;
    }

    public String getPatientId() {
        return patient.getId();
    }

    public boolean isBaseline() {
        return visit.typeOfVisit() != null && visit.typeOfVisit().isBaselineVisit();
    }

    public String getTypeOfVisit() {
        return visit.typeOfVisit().toString();
    }

    public String getTreatmentAdviceId() {
        return (String) visit.getData().get(TREATMENT_ADVICE);
    }

    public void setTreatmentAdviceId(String treatmentAdviceId) {
        visit.addData(TREATMENT_ADVICE, treatmentAdviceId);
    }

    public List<String> getLabResultIds() {
        List<String> labResultIds = (List<String>) visit.getData().get(LAB_RESULTS);
        return labResultIds == null ? Collections.<String>emptyList() : labResultIds;
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
        return visit.appointment() == null ? null : visit.appointment().dueDate();
    }

    public void setAppointmentDueDate(DateTime appointmentDueDate) {
        visit.appointment().dueDate(appointmentDueDate);
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public LocalDate getAdjustedDueDate() {
        if (visit.appointment() == null) {
            return null;
        }
        String adjustedDueDate = (String) visit.appointment().getData().get(ADJUSTED_DUE_DATE);
        return adjustedDueDate == null ? null : new LocalDate(adjustedDueDate);
    }

    public void setAdjustedDueDate(LocalDate adjustedDueDate) {
        visit.appointment().addData(ADJUSTED_DUE_DATE, adjustedDueDate);
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATETIME_FORMAT)
    public DateTime getConfirmedVisitDate() {
        return visit.appointment() == null ? null : visit.appointment().firmDate();
    }

    public void setConfirmedVisitDate(DateTime firmDate) {
        visit.appointment().firmDate(firmDate);
    }

    public LocalDate getEffectiveDueDate() {
        LocalDate appointmentDueDate = getAppointmentDueDate() == null ? null : getAppointmentDueDate().toLocalDate();
        return getAdjustedDueDate() == null ? appointmentDueDate : getAdjustedDueDate();
    }

    private Integer weekNumber() {
        return visit.weekNumber();
    }

    @Override
    public int compareTo(ClinicVisit clinicVisit) {
        if (this.visit.appointment() == null || this.visit.appointment().dueDate() == null) return -1;
        if (clinicVisit.visit.appointment() == null || clinicVisit.visit.appointment().dueDate() == null) return 1;
        return this.getEffectiveDueDate().compareTo(clinicVisit.getEffectiveDueDate());
    }
}
