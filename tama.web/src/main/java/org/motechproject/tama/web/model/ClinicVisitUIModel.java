package org.motechproject.tama.web.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.TypeOfVisit;
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

    public ClinicVisit getClinicVisit() {
		return clinicVisit;
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
        if (clinicVisit.isUnscheduledWithAppointment()) {
            return TypeOfVisit.Unscheduled.name();
        } else {
            return clinicVisit.getTypeOfVisit();
        }
    }

    public boolean isMissed() {
        return clinicVisit.isMissed();
    }

    public boolean isVisitEditable() {
        return !isMissed();
    }

    public String getTitle() {
        if (clinicVisit.isBaseline())
            return "Activated in TAMA";
        else if (clinicVisit.weekNumber() != null)
            return clinicVisit.weekNumber() + " weeks Follow-up visit";
        else if (clinicVisit.isUnscheduledWithAppointment())
            return "Ad-hoc Visit by appointment";
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
        return !clinicVisit.isBaseline() && !isMissed() && clinicVisit.getConfirmedAppointmentDate() == null && clinicVisit.getVisitDate() == null;
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public LocalDate getAdjustedDueDate() {
        return clinicVisit.getAdjustedDueDate();
    }

    public boolean isConfirmedAppointmentDateEditable() {
        return !clinicVisit.isBaseline() && !isMissed() && clinicVisit.getVisitDate() == null;
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATETIME_FORMAT)
    public DateTime getConfirmedAppointmentDate() {
        return clinicVisit.getConfirmedAppointmentDate();
    }

    public boolean isVisitDateEditable() {
        if (clinicVisit.isBaseline() && clinicVisit.getVisitDate() == null) return false;
        return !isMissed();
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public DateTime getVisitDate() {
        return clinicVisit.getVisitDate();
    }
}
