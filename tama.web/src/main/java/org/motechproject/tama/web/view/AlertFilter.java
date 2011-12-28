package org.motechproject.tama.web.view;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.PatientAlertType;
import org.motechproject.util.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AlertFilter {

    private String patientId;

    private String alertType;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    private Date endDate;

    public Date getStartDate() {
        return startDate;
    }

    public AlertFilter setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public AlertFilter setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public DateTime getStartDateTime() {
        return startDate == null ? null : DateUtil.newDateTime(startDate);
    }

    public DateTime getEndDateTime() {
        return endDate == null ? null : DateUtil.newDateTime(endDate);
    }

    public AlertFilter setPatientId(String patientId) {
        this.patientId = StringUtils.isBlank(patientId) ? null : patientId;
        return this;
    }

    public String getPatientId() {
        return patientId;
    }

    public AlertFilter setAlertType(String alertType) {
        this.alertType = alertType;
        return this;
    }

    public String getAlertType() {
        return alertType;
    }

    public PatientAlertType getPatientAlertType() {
        return StringUtils.isBlank(alertType) || alertType.equals("Any") ? null : PatientAlertType.valueOf(alertType);
    }

    public List<String> getAllPatientAlertTypes() {
        ArrayList<String> alertTypes = new ArrayList<String>();
        alertTypes.add("Any");
        for(PatientAlertType alertType : Arrays.asList(PatientAlertType.values())){
            alertTypes.add(alertType.toString());
        }
        return alertTypes;
    }
}
