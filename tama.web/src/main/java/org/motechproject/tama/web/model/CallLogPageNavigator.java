package org.motechproject.tama.web.model;

import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.ivr.domain.CallLogSearch;
import org.motechproject.util.DateUtil;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class CallLogPageNavigator {

    private Integer currentPageNumber;

    private Date callLogStartDate;

    private Date callLogEndDate;

    private String callType;

    private Integer totalNumberOfPages;
    
    private String patientId;

    public CallLogPageNavigator(CallLogSearch callLogSearch, Integer currentPageNumber, Integer totalNumberOfPages, String patientId) {
        this.currentPageNumber = currentPageNumber;
        this.callLogStartDate = callLogSearch.getFromDate().toDate();
        this.callLogEndDate = callLogSearch.getToDate().toDate();
        this.callType = callLogSearch.getCallLogType().name();
        this.totalNumberOfPages = totalNumberOfPages;
        this.patientId = patientId;
    }

    public String getNextPageLink() throws UnsupportedEncodingException {
        Integer nextPageNumber = currentPageNumber + 1;
        if(nextPageNumber > totalNumberOfPages) return null;
        return getFormattedLink(nextPageNumber.toString());
    }
    
    public String getPreviousPageLink() throws UnsupportedEncodingException {
        Integer previousPageNumber = currentPageNumber - 1;
        if(previousPageNumber <= 0) return null;
        return getFormattedLink(previousPageNumber.toString());
    }

    public String getFirstPageLink() throws UnsupportedEncodingException {
        if(currentPageNumber == 1) return null;
        return getFormattedLink("1");
    }

    public String getLastPageLink() throws UnsupportedEncodingException {
        if(currentPageNumber == totalNumberOfPages) return null;
        return getFormattedLink(totalNumberOfPages.toString());
    }

    private String getFormattedLink(String pageNumber) throws UnsupportedEncodingException {
        String startDateString = DateUtil.newDate(callLogStartDate).toString(TAMAConstants.DATE_FORMAT);
        String endDateString = DateUtil.newDate(callLogEndDate).toString(TAMAConstants.DATE_FORMAT);
        String url = "callsummary?callLogStartDate=" + startDateString + "&callLogEndDate=" + endDateString + "&callType=" + callType + "&pageNumber=" + pageNumber;
        if(patientId != null) 
            url = url + "&patientId=" + patientId;
        return url;
    }

    public Integer getCurrentPageNumber() {
        return currentPageNumber;
    }

    public String getCallLogStartDate() {
        return DateUtil.newDate(callLogStartDate).toString(TAMAConstants.DATE_FORMAT);
    }

    public String getCallLogEndDate() {
        return DateUtil.newDate(callLogEndDate).toString(TAMAConstants.DATE_FORMAT);
    }

    public String getCallType() {
        return callType;
    }

    public Integer getTotalNumberOfPages() {
        return totalNumberOfPages;
    }

    public String getPatientId() {
        return patientId;
    }
}