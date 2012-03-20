package org.motechproject.tama.web.viewbuilder;

import org.apache.poi.ss.usermodel.Cell;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.ivr.domain.SMSLog;
import org.motechproject.tama.web.model.CallLogSummary;

import java.util.ArrayList;
import java.util.List;

public class SMSReportBuilder extends ReportBuilder<SMSLog> {

    public SMSReportBuilder(List<SMSLog> objects) {
        super(objects);
    }

    @Override
    protected String getWorksheetName() {
        return "ALLSMSReports";
    }

    @Override
    protected String getTitle() {
        return "All SMS Reports";
    }

    @Override
    protected void initializeColumns() {
        columns = new ArrayList<ExcelColumn>();
        columns.add(new ExcelColumn("Sent Date and Time", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Phone Number Sent To", Cell.CELL_TYPE_STRING, 6000));
        columns.add(new ExcelColumn("Message", Cell.CELL_TYPE_STRING, 25000));
    }

    @Override
    protected List<Object> getRowData(Object object) {
        SMSLog smsLog = (SMSLog) object;
        List<Object> row = new ArrayList<Object>();
        row.add(smsLog.getSentDateTime().toString(TAMAConstants.DATETIME_FORMAT));
        row.add(smsLog.getRecipient());
        row.add(smsLog.getMessage());
        return row;
    }
}
