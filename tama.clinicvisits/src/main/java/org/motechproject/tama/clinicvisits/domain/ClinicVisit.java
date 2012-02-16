package org.motechproject.tama.clinicvisits.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.util.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.List;


public class ClinicVisit extends Visit {

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

    private Appointment appointment;

    public String getName() {
        return getTitle();
    }

    private void setName(String name) {
        setTitle(name);
    }

    public String getPatientId() {
        return getExternalId();
    }

    public void setPatientId(String patientId) {
        setExternalId(patientId);
    }

    public TypeOfVisit getTypeOfVisit() {
        return (TypeOfVisit) getData().get(TYPE_OF_VISIT);
    }

    public void setTypeOfVisit(TypeOfVisit typeOfVisit) {
        addData(TYPE_OF_VISIT, typeOfVisit);
    }

    public String getTreatmentAdviceId() {
        return (String) getData().get(TREATMENT_ADVICE);
    }

    public void setTreatmentAdviceId(String treatmentAdviceId) {
        addData(TREATMENT_ADVICE, treatmentAdviceId);
    }

    public List<String> getLabResultIds() {
        return (List<String>) getData().get(LAB_RESULTS);
    }

    public void setLabResultIds(List<String> labResultIds) {
        addData(LAB_RESULTS, labResultIds);
    }

    public String getVitalStatisticsId() {
        return (String) getData().get(VITAL_STATISTICS);
    }

    public void setVitalStatisticsId(String vitalStatisticsId) {
        addData(VITAL_STATISTICS, vitalStatisticsId);
    }

    @Override
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public DateTime getVisitDate() {
        return super.getVisitDate();
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

    public static ClinicVisit createExpectedVisit(DateTime expectedVisitTime, int weeks, String patientId) {
        ClinicVisit clinicVisit = new ClinicVisit();
        if (weeks == 0) {
            clinicVisit.setName("Registered with TAMA");
            clinicVisit.setTypeOfVisit(TypeOfVisit.Baseline);
        } else {
            clinicVisit.setName(weeks + " weeks Follow-up visit");
            clinicVisit.setTypeOfVisit(TypeOfVisit.Scheduled);
        }
        clinicVisit.setAppointmentDueDate(expectedVisitTime.plusWeeks(weeks));
        clinicVisit.setPatientId(patientId);
        return clinicVisit;
    }
}
