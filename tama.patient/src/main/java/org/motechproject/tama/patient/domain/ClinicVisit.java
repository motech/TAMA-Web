package org.motechproject.tama.patient.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.util.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.List;


@TypeDiscriminator("doc.documentType == 'ClinicVisit'")
public class ClinicVisit extends CouchEntity implements Comparable<ClinicVisit> {
    /*
     * TODO: Verify implication on migration for each change.
     */
    @NotNull
    private String patientId;
    @NotNull
    private String treatmentAdviceId;

    private List<String> labResultIds;

    private String vitalStatisticsId;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    private DateTime visitDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    private DateTime expectedVisitTime;

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

    public DateTime getVisitDate() {
        return visitDate == null ? null : DateUtil.setTimeZone(visitDate);
    }

    public void setVisitDate(DateTime visitDate) {
        this.visitDate = visitDate;
    }

    public void setExpectedVisitTime(DateTime expectedVisitTime) {
        this.expectedVisitTime = expectedVisitTime;
    }

    @Override
    public int compareTo(ClinicVisit clinicVisit) {
        return getVisitDate().compareTo(clinicVisit.getVisitDate());
    }

    public static ClinicVisit createVisitForToday() {
        ClinicVisit clinicVisit = new ClinicVisit();
        clinicVisit.setVisitDate(DateUtil.now());
        return clinicVisit;
    }

    public static ClinicVisit createExpectedVisit(DateTime expectedVisitTime, String patientId) {
        ClinicVisit clinicVisit = new ClinicVisit();
        clinicVisit.setExpectedVisitTime(expectedVisitTime);
        clinicVisit.setPatientId(patientId);
        return clinicVisit;
    }
}
