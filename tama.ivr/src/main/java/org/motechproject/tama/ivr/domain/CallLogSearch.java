package org.motechproject.tama.ivr.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

public class CallLogSearch {
    @Getter private DateTime fromDate;
    @Getter private DateTime toDate;
    @Getter private CallLog.CallLogType callLogType;
    @Getter private String patientId;
    @Getter private boolean searchAllClinics;
    @Getter private String clinicId;
    @Getter @Setter private Integer startIndex;
    @Getter @Setter private Integer limit;

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
}

