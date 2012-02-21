package org.motechproject.tama.web.model;

import org.motechproject.tama.common.TAMAConstants;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
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
    
    public String getGoToPageLink() throws UnsupportedEncodingException {
        return getFormattedLink("");
    }

    private String getFormattedLink(String pageNumber) throws UnsupportedEncodingException {
        String startDateString = new SimpleDateFormat(TAMAConstants.DATE_FORMAT).format(callLogStartDate);
        String endDateString = new SimpleDateFormat(TAMAConstants.DATE_FORMAT).format(callLogEndDate);
        return "callsummary?callLogStartDate=" + startDateString + "&callLogEndDate=" + endDateString + "&pageNumber=" + pageNumber;
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

    public Integer getCurrentPageNumber() {
        return currentPageNumber;
    }

    public void setCurrentPageNumber(Integer currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

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

    public Integer getTotalNumberOfPages() {
        return totalNumberOfPages;
    }

    public void setTotalNumberOfPages(Integer totalNumberOfPages) {
        this.totalNumberOfPages = totalNumberOfPages;
    }
}