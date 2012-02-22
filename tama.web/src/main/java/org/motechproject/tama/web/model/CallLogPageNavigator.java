package org.motechproject.tama.web.model;

import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.util.DateUtil;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class CallLogPageNavigator {

    private Integer currentPageNumber;

    private Date callLogStartDate;

    private Date callLogEndDate;

    private Integer totalNumberOfPages;
    
    public CallLogPageNavigator(Integer currentPageNumber, Date callLogStartDate, Date callLogEndDate, Integer totalNumberOfPages) {
        this.currentPageNumber = currentPageNumber;
        this.callLogStartDate = callLogStartDate;
        this.callLogEndDate = callLogEndDate;
        this.totalNumberOfPages = totalNumberOfPages;
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
        return "callsummary?callLogStartDate=" + startDateString + "&callLogEndDate=" + endDateString + "&pageNumber=" + pageNumber;
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

    public Integer getTotalNumberOfPages() {
        return totalNumberOfPages;
    }

}