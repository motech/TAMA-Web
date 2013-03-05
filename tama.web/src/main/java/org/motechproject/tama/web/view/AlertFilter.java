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

    public static final String STATUS_OPEN = "Open";
    public static final String STATUS_CLOSED = "Closed";
    public static final String STATUS_ALL = "All";

    private String patientId;

    private String alertType;

    private String alertStatus;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    private Date startDate = DateUtil.today().toDate();
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    private Date endDate = DateUtil.today().toDate();

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
        return startDate == null ? null : DateUtil.newDateTime(DateUtil.newDate(startDate), 0, 0, 0);
    }

    public DateTime getEndDateTime() {
        return endDate == null ? null : DateUtil.newDateTime(DateUtil.newDate(endDate), 23, 59, 59);
    }

    public AlertFilter setPatientId(String patientId) {
        this.patientId = StringUtils.isBlank(patientId) ? null : patientId;
        return this;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getAlertType() {
        return alertType;
    }

    public AlertFilter setAlertType(String alertType) {
        this.alertType = alertType;
        return this;
    }

    public PatientAlertType getPatientAlertType() {
        if (StringUtils.isBlank(alertType) || alertType.equals("Any") ) return null;
        for (PatientAlertType each : PatientAlertType.values()) {
            if (each.toString().equals(alertType)) return each;
        }
        return  null;
    }

    public String getAlertStatus() {
        return alertStatus;
    }

    public AlertFilter setAlertStatus(String alertStatus) {
        this.alertStatus = alertStatus;
        return this;
    }

    public List<String> getAllPatientAlertTypes() {
        ArrayList<String> alertTypes = new ArrayList<String>();
        alertTypes.add("Any");
        for(PatientAlertType alertType : Arrays.asList(PatientAlertType.values())){
            alertTypes.add(alertType.toString());
        }
        return alertTypes;
    }

    public List<String> getAllAlertStatuses() {
        ArrayList<String> alertStatuses = new ArrayList<String>();
        alertStatuses.add(STATUS_ALL);
        alertStatuses.add(STATUS_OPEN);
        alertStatuses.add(STATUS_CLOSED);
        return alertStatuses;
    }
}
