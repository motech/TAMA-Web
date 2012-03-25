package org.motechproject.tama.ivr.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

public class CallLogSearch {
    private DateTime fromDate;
    private DateTime toDate;
    private CallLog.CallLogType callLogType;
    private String patientId;
    private boolean searchAllClinics;
    private String clinicId;
    private Integer startIndex;
    private Integer limit;

    public CallLogSearch(DateTime fromDate, DateTime toDate, CallLog.CallLogType callLogType, String patientId, boolean searchAllClinics, String clinicId) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.callLogType = callLogType;
        this.patientId = patientId;
        this.searchAllClinics = searchAllClinics;
        this.clinicId = clinicId;
    }

    public void setPaginationParams(Integer startIndex, Integer limit) {
        this.startIndex = startIndex;
        this.limit = limit;
    }

    public boolean isSearchByPatientId(){
        return !StringUtils.isEmpty(patientId);
    }

    public DateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(DateTime fromDate) {
        this.fromDate = fromDate;
    }

    public DateTime getToDate() {
        return toDate;
    }

    public void setToDate(DateTime toDate) {
        this.toDate = toDate;
    }

    public CallLog.CallLogType getCallLogType() {
        return callLogType;
    }

    public void setCallLogType(CallLog.CallLogType callLogType) {
        this.callLogType = callLogType;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public boolean isSearchAllClinics() {
        return searchAllClinics;
    }

    public void setSearchAllClinics(boolean searchAllClinics) {
        this.searchAllClinics = searchAllClinics;
    }

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}

