package org.motechproject.tama.web.viewbuilder;

import org.apache.poi.ss.usermodel.Cell;
import org.motechproject.tama.web.model.CallLogSummary;

import java.util.ArrayList;
import java.util.List;

public class CallLogReportBuilder extends ReportBuilder<CallLogSummary> {

    public CallLogReportBuilder(List<CallLogSummary> objects) {
        super(objects);
    }

    @Override
    protected String getWorksheetName() {
        return "AllCallLogsReport";
    }

    @Override
    protected String getTitle() {
        return "All Call Logs Report";
    }

    @Override
    protected void initializeColumns() {
        columns = new ArrayList<ExcelColumn>();
        columns.add(new ExcelColumn("Patient ID", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Source Phone Number", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Destination Phone Number", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Clinic Name", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("TAMA Initiated Call At", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Call Started At", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Call Ended At", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Language", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Flows Accessed", Cell.CELL_TYPE_STRING, 14000));
        columns.add(new ExcelColumn("Distance of Patient from Clinic", Cell.CELL_TYPE_STRING, 8000));
    }

    @Override
    protected List<Object> getRowData(Object object) {
        CallLogSummary callLogSummary = (CallLogSummary) object;
        List<Object> row = new ArrayList<Object>();
        row.add(callLogSummary.getPatientId());
        row.add(callLogSummary.getSourcePhoneNumber());
        row.add(callLogSummary.getDestinationPhoneNumber());
        row.add(callLogSummary.getClinicName());
        row.add(callLogSummary.getInitiatedDateTime());
        row.add(callLogSummary.getStartDateTime());
        row.add(callLogSummary.getEndDateTime());
        row.add(callLogSummary.getLanguage());
        row.add(callLogSummary.getFlows());
        row.add(callLogSummary.getPatientDistanceFromClinic());
        return row;
    }
}
