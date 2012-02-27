package org.motechproject.tama.ivr.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.CallEventCustomData;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class CallLogSearch {
    @Getter private DateTime fromDate;
    @Getter private DateTime toDate;
    @Getter private CallLog.CallLogType callLogType;
    @Getter private boolean searchAllClinics;
    @Getter private String clinicId;
    @Getter @Setter private Integer startIndex;
    @Getter @Setter private Integer limit;

    public CallLogSearch(DateTime fromDate, DateTime toDate, CallLog.CallLogType callLogType, boolean searchAllClinics, String clinicId) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.callLogType = callLogType;
        this.searchAllClinics = searchAllClinics;
        this.clinicId = clinicId;
    }

    public void setPaginationParams(Integer startIndex, Integer limit) {
        this.startIndex = startIndex;
        this.limit = limit;
    }
}

