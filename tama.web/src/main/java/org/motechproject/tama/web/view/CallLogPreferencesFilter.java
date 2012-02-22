package org.motechproject.tama.web.view;

import org.motechproject.tama.common.TAMAConstants;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

public class CallLogPreferencesFilter {

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    private Date callLogStartDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    private Date callLogEndDate;

    private String pageNumber;

    public Date getCallLogStartDate() {
        return callLogStartDate;
    }

    public void setCallLogStartDate(Date callLogStartDate) {
        this.callLogStartDate = callLogStartDate;
    }

    public Date getCallLogEndDate() {
        return callLogEndDate;
    }

    public void setCallLogEndDate(Date callLogEndDate) {
        this.callLogEndDate = callLogEndDate;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getPageNumber() {
        return pageNumber;
    }
}
