package org.motechproject.tama.clinicvisits.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.appointments.api.contract.VisitResponse;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.util.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Collections;
import java.util.List;


public class ClinicVisit implements Comparable<ClinicVisit> {

    public static final String TREATMENT_ADVICE = "TreatmentAdviceId";
    public static final String VITAL_STATISTICS = "VitalStatisticsId";
    public static final String LAB_RESULTS = "LabResultIds";
    public static final String REPORTED_OPPORTUNISTIC_INFECTIONS = "ReportedOpportunisticInfectionsId";
    public static final String WEEK_NUMBER = "WeekNumber";

    private Patient patient;
    private VisitResponse visit;

    public ClinicVisit() {
    }

    public ClinicVisit(Patient patient, VisitResponse visitResponse) {
        this.patient = patient;
        this.visit = visitResponse;
    }

    public String getId() {
        return visit.getName();
    }

    public String getTitle() {
        if (isBaseline())
            return "Registered with TAMA";
        else if (weekNumber() != null)
            return weekNumber() + " weeks Follow-up visit";
        else
            return "Ad-hoc Visit";
    }

    public Patient getPatient() {
        return patient;
    }

    public String getPatientId() {
        return patient.getId();
    }

    public String getTypeOfVisit() {
        return visit.getTypeOfVisit();
    }

    public boolean isBaseline() {
        return TypeOfVisit.valueOf(getTypeOfVisit()).isBaselineVisit();
    }

    public String getTreatmentAdviceId() {
        return (String) visit.getVisitData().get(TREATMENT_ADVICE);
    }

    public String getReportedOpportunisticInfectionsId() {
        return (String) visit.getVisitData().get(REPORTED_OPPORTUNISTIC_INFECTIONS);
    }

    public List<String> getLabResultIds() {
        List<String> labResultIds = (List<String>) visit.getVisitData().get(LAB_RESULTS);
        return labResultIds == null ? Collections.<String>emptyList() : labResultIds;
    }

    public String getVitalStatisticsId() {
        return (String) visit.getVisitData().get(VITAL_STATISTICS);
    }

    public boolean isMissed() {
        return visit.isMissed();
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public DateTime getVisitDate() {
        return visit.getVisitDate();
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public DateTime getAppointmentDueDate() {
        return visit.getOriginalAppointmentDueDate();
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public LocalDate getAdjustedDueDate() {
        if (visit.getOriginalAppointmentDueDate() == null) return null;
        return visit.getOriginalAppointmentDueDate().equals(visit.getAppointmentDueDate()) ? null :  DateUtil.newDate(visit.getAppointmentDueDate());
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATETIME_FORMAT)
    public DateTime getConfirmedAppointmentDate() {
        return visit.getAppointmentConfirmDate();
    }

    public LocalDate getEffectiveDueDate() {
        return getAdjustedDueDate() == null ? DateUtil.newDate(getAppointmentDueDate()) : getAdjustedDueDate();
    }

    @Override
    public int compareTo(ClinicVisit clinicVisit) {
        if (this.visit.getAppointmentDueDate() == null) return -1;
        if (clinicVisit.visit.getAppointmentDueDate() == null) return 1;
        return this.getEffectiveDueDate().compareTo(clinicVisit.getEffectiveDueDate());
    }

    private Integer weekNumber() {
        return (Integer) visit.getVisitData().get(WEEK_NUMBER);
    }
}
