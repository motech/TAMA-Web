package org.motechproject.tama.web.view;

import java.util.Date;

public class CallLogPreferencesFilter {

    Date callLogStartDate;

    Date callLogEndDate;

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

}
