package org.motechproject.tama.web.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.util.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public class ClinicVisitUIModel {

    private ClinicVisit clinicVisit;
    private DateTime defaultVisitDate;

    public ClinicVisitUIModel() {
    }

    public ClinicVisitUIModel(ClinicVisit clinicVisit) {
        this.clinicVisit = clinicVisit;
        this.defaultVisitDate = DateUtil.now();
    }

    public String getId() {
        return clinicVisit.getId();
    }

    public Patient getPatient() {
        return clinicVisit.getPatient();
    }

    public String getPatientDocId() {
        return clinicVisit.getPatientDocId();
    }

    public String getTypeOfVisit() {
        return clinicVisit.getTypeOfVisit();
    }

    public boolean isVisitEditable() {
        return !clinicVisit.isMissed();
    }

    public String getTitle() {
        if (clinicVisit.isBaseline())
            return "Registered with TAMA";
        else if (clinicVisit.weekNumber() != null)
            return clinicVisit.weekNumber() + " weeks Follow-up visit";
        else
            return "Ad-hoc Visit";
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public DateTime getDefaultVisitDate() {
        return defaultVisitDate;
    }

    public void setDefaultVisitDate(DateTime visitDate) {
        this.defaultVisitDate = visitDate;
    }

    public boolean isAnyVisitDetailsCaptured() {
        return StringUtils.isNotEmpty(clinicVisit.getTreatmentAdviceId()) ||
                CollectionUtils.isNotEmpty(clinicVisit.getLabResultIds()) ||
                StringUtils.isNotEmpty(clinicVisit.getVitalStatisticsId()) ||
                StringUtils.isNotEmpty(clinicVisit.getReportedOpportunisticInfectionsId());
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public DateTime getAppointmentDueDate() {
        return clinicVisit.getAppointmentDueDate();
    }

    public boolean isDueDateEditable() {
        return !clinicVisit.isMissed() && clinicVisit.getConfirmedAppointmentDate() == null && clinicVisit.getVisitDate() == null;
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public LocalDate getAdjustedDueDate() {
        return clinicVisit.getAdjustedDueDate();
    }

    public boolean isConfirmedAppointmentDateEditable() {
        return !clinicVisit.isMissed() && clinicVisit.getVisitDate() == null;
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATETIME_FORMAT)
    public DateTime getConfirmedAppointmentDate() {
        return clinicVisit.getConfirmedAppointmentDate();
    }

    public boolean isVisitDateEditable() {
        return !clinicVisit.isMissed();
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public DateTime getVisitDate() {
        return clinicVisit.getVisitDate();
    }
}
