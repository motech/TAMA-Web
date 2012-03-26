package org.motechproject.tama.web.resportbuilder;

import org.apache.poi.ss.usermodel.Cell;
import org.motechproject.tama.ivr.domain.SMSLog;
import org.motechproject.tama.web.resportbuilder.abstractbuilder.InMemoryReportBuilder;
import org.motechproject.tama.web.resportbuilder.model.ExcelColumn;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.tama.common.TAMAConstants.DATETIME_YYYY_MM_DD_FORMAT;

public class SMSReportBuilder extends InMemoryReportBuilder<SMSLog> {

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
        columns.add(new ExcelColumn("Sent Date and Time (yyyy-mm-dd hh:mm:ss)", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Phone Number Sent To", Cell.CELL_TYPE_STRING, 6000));
        columns.add(new ExcelColumn("Message", Cell.CELL_TYPE_STRING, 25000));
    }

    @Override
    protected List<Object> getRowData(Object object) {
        SMSLog smsLog = (SMSLog) object;
        List<Object> row = new ArrayList<Object>();
        row.add(smsLog.getSentDateTime().toString(DATETIME_YYYY_MM_DD_FORMAT));
        row.add(smsLog.getRecipient());
        row.add(smsLog.getMessage());
        return row;
    }
}
