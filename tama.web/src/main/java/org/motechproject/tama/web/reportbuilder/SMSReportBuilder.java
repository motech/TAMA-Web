package org.motechproject.tama.web.reportbuilder;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.ivr.domain.SMSLog;
import org.motechproject.tama.web.reportbuilder.abstractbuilder.InMemoryReportBuilder;
import org.motechproject.tama.web.reportbuilder.model.ExcelColumn;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.tama.common.TAMAConstants.DATETIME_YYYY_MM_DD_FORMAT;

public class SMSReportBuilder extends InMemoryReportBuilder<SMSLog> {

    private LocalDate startDate;
    private LocalDate endDate;

    public SMSReportBuilder(LocalDate startDate, LocalDate endDate, List<SMSLog> objects) {
        super(objects);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    protected String getWorksheetName() {
        return "SMSReport";
    }

    @Override
    protected String getTitle() {
        return "SMS Report";
    }

    @Override
    protected void initializeColumns() {
        columns = new ArrayList<ExcelColumn>();
        columns.add(new ExcelColumn("Sent Date and Time (yyyy-mm-dd hh:mm:ss)", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Message", Cell.CELL_TYPE_STRING, 25000));
    }

    @Override
    protected List<Object> getRowData(Object object) {
        SMSLog smsLog = (SMSLog) object;
        List<Object> row = new ArrayList<Object>();
        row.add(smsLog.getSentDateTime().toString(DATETIME_YYYY_MM_DD_FORMAT));
        row.add(smsLog.getMaskedMessage());
        return row;
    }

    @Override
    protected void buildSummary(HSSFSheet worksheet) {
        List<HSSFCellStyle> cellStyles = buildCellStylesForSummary(worksheet);
        buildSummaryRow(worksheet, cellStyles, "Report Start Date", startDate.toString(TAMAConstants.DATE_FORMAT));
        buildSummaryRow(worksheet, cellStyles, "Report End Date", endDate.toString(TAMAConstants.DATE_FORMAT));
    }
}
