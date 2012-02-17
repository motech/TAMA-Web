package org.motechproject.tama.web.viewbuilder;

import org.apache.poi.ss.usermodel.Cell;
import org.motechproject.tama.dailypillreminder.domain.DailyPillReminderSummary;

import java.util.ArrayList;
import java.util.List;

public class DailyPillReminderReportBuilder extends ReportBuilder<DailyPillReminderSummary> {

    public DailyPillReminderReportBuilder(List<DailyPillReminderSummary> objects) {
        super(objects);
    }

    @Override
    protected void initializeColumns() {
        columns = new ArrayList<ExcelColumn>();
        columns.add(new ExcelColumn("Date", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Morning Dose Time", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Morning Adherence", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Evening Dose Time", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Evening Adherence", Cell.CELL_TYPE_STRING));
    }

    @Override
    protected List<Object> getRowData(Object object) {
        DailyPillReminderSummary summary = (DailyPillReminderSummary) object;
        List<Object> row = new ArrayList<Object>();
        row.add(summary.getDate());
        row.add(summary.getMorningDoseTime());
        row.add(summary.getMorningDoseStatus());
        row.add(summary.getEveningDoseTime());
        row.add(summary.getEveningDoseStatus());
        return row;
    }

    @Override
    protected String getWorksheetName() {
        return "DailyPillReminderReport";
    }

    @Override
    protected String getTitle() {
        return "Daily Pill Reminder Report";
    }

}
