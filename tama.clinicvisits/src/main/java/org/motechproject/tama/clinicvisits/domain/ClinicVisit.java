package org.motechproject.tama.clinicvisits.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.util.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@TypeDiscriminator("doc.documentType == 'ClinicVisit'")
public class ClinicVisit extends CouchEntity implements Comparable<ClinicVisit> {

    public enum TypeOfVisit {
        Baseline,
        Scheduled,
        Unscheduled
    }

    /*
     * TODO: Verify implication on migration for each change.
     */
    @NotNull
    private String patientId;
    @NotNull
    private String treatmentAdviceId;

    private List<String> labResultIds = new ArrayList<String>();

    private String vitalStatisticsId;

    private TypeOfVisit typeOfVisit;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    private LocalDate visitDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    private DateTime appointmentDueDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    private LocalDate adjustedDueDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATETIME_FORMAT)
    private DateTime confirmedVisitDate;

    private String name;

    private boolean missed;

    public boolean isMissed() {
        return missed;
    }

    public void setMissed(boolean missed) {
        this.missed = missed;
    }

    public DateTime getConfirmedVisitDate() {
        return confirmedVisitDate == null ? null : DateUtil.setTimeZone(confirmedVisitDate);
    }

    public void setConfirmedVisitDate(DateTime confirmedVisitDate) {
        this.confirmedVisitDate = confirmedVisitDate;
    }

    public LocalDate getAdjustedDueDate() {
        return adjustedDueDate;
    }

    @DateTimeFormat(style="S-", pattern = TAMAConstants.DATE_FORMAT)
    public void setAdjustedDueDate(LocalDate adjustedDueDate) {
        this.adjustedDueDate = adjustedDueDate;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public TypeOfVisit getTypeOfVisit() {
        return typeOfVisit;
    }

    public void setTypeOfVisit(TypeOfVisit typeOfVisit) {
        this.typeOfVisit = typeOfVisit;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getTreatmentAdviceId() {
        return treatmentAdviceId;
    }

    public void setTreatmentAdviceId(String treatmentAdviceId) {
        this.treatmentAdviceId = treatmentAdviceId;
    }

    public List<String> getLabResultIds() {
        return labResultIds;
    }

    public void setLabResultIds(List<String> labResultIds) {
        this.labResultIds = labResultIds;
    }

    public String getVitalStatisticsId() {
        return vitalStatisticsId;
    }

    public void setVitalStatisticsId(String vitalStatisticsId) {
        this.vitalStatisticsId = vitalStatisticsId;
    }

    public LocalDate getVisitDate() {
        return visitDate == null ? null : visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }

    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public DateTime getAppointmentDueDate() {
        return appointmentDueDate == null ? null : DateUtil.setTimeZone(appointmentDueDate);
    }

    public void setAppointmentDueDate(DateTime appointmentDueDate) {
        this.appointmentDueDate = appointmentDueDate;
    }

    @Override
    public int compareTo(ClinicVisit clinicVisit) {
        if (getVisitDate() == null) return 1;
        if (clinicVisit.getVisitDate() == null) return -1;
        return getVisitDate().compareTo(clinicVisit.getVisitDate());
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
