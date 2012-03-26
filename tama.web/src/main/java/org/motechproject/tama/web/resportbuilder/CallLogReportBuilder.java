package org.motechproject.tama.web.resportbuilder;

import org.apache.poi.ss.usermodel.Cell;
import org.joda.time.LocalDate;
import org.motechproject.tama.web.model.CallFlowDetails;
import org.motechproject.tama.web.model.CallLogSummary;
import org.motechproject.tama.web.resportbuilder.abstractbuilder.BatchReportBuilder;
import org.motechproject.tama.web.resportbuilder.model.ExcelColumn;
import org.motechproject.tama.web.service.AllCallLogSummaries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.motechproject.tama.common.CallTypeConstants.*;

public class CallLogReportBuilder extends BatchReportBuilder {

    private AllCallLogSummaries allCallLogSummaries;
    private LocalDate startDate;
    private LocalDate endDate;

    public CallLogReportBuilder(AllCallLogSummaries allCallLogSummaries, LocalDate startDate, LocalDate endDate) {
        super();
        this.allCallLogSummaries = allCallLogSummaries;
        this.startDate = startDate;
        this.endDate = endDate;
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
        columns.add(new ExcelColumn("TAMA Initiated Call At (yyyy-mm-dd hh:mm:ss)", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Call Started At (yyyy-mm-dd hh:mm:ss)", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Call Ended At (yyyy-mm-dd hh:mm:ss)", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Language", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Flows Accessed", Cell.CELL_TYPE_STRING, 14000));
        columns.add(new ExcelColumn("Menu Accessed (No. of times)", Cell.CELL_TYPE_STRING, 4000));
        columns.add(new ExcelColumn("Menu - Individual Durations (Seconds)", Cell.CELL_TYPE_STRING, 4000));
        columns.add(new ExcelColumn("Menu - Total Duration (Seconds)", Cell.CELL_TYPE_STRING, 4000));
        columns.add(new ExcelColumn("Pill Reminder Accessed (No. of times)", Cell.CELL_TYPE_STRING, 4000));
        columns.add(new ExcelColumn("Pill Reminder - Individual Durations (Seconds)", Cell.CELL_TYPE_STRING, 4000));
        columns.add(new ExcelColumn("Pill Reminder - Total Duration (Seconds)", Cell.CELL_TYPE_STRING, 4000));
        columns.add(new ExcelColumn("Four Day Recall Accessed (No. of times)", Cell.CELL_TYPE_STRING, 4000));
        columns.add(new ExcelColumn("Four Day Recall - Individual Durations (Seconds)", Cell.CELL_TYPE_STRING, 4000));
        columns.add(new ExcelColumn("Four Day Recall - Total Duration (Seconds)", Cell.CELL_TYPE_STRING, 4000));
        columns.add(new ExcelColumn("Health Tips Accessed (No. of times)", Cell.CELL_TYPE_STRING, 4000));
        columns.add(new ExcelColumn("Health Tips - Individual Durations (Seconds)", Cell.CELL_TYPE_STRING, 4000));
        columns.add(new ExcelColumn("Health Tips - Total Duration (Seconds)", Cell.CELL_TYPE_STRING, 4000));
        columns.add(new ExcelColumn("Symptom Reporting Accessed (No. of times)", Cell.CELL_TYPE_STRING, 4000));
        columns.add(new ExcelColumn("Symptom Reporting - Individual Durations (Seconds)", Cell.CELL_TYPE_STRING, 4000));
        columns.add(new ExcelColumn("Symptom Reporting - Total Duration (Seconds)", Cell.CELL_TYPE_STRING, 4000));
        columns.add(new ExcelColumn("Outbox Accessed (No. of times)", Cell.CELL_TYPE_STRING, 4000));
        columns.add(new ExcelColumn("Outbox - Individual Durations (Seconds)", Cell.CELL_TYPE_STRING, 4000));
        columns.add(new ExcelColumn("Outbox - Total Duration (Seconds)", Cell.CELL_TYPE_STRING, 4000));
        columns.add(new ExcelColumn("Age", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Gender", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Distance of Patient from Clinic", Cell.CELL_TYPE_STRING, 8000));
    }

    @Override
    protected List<Object> getRowData(Object object) {
        CallLogSummary callLogSummary = (CallLogSummary) object;
        Map<String,CallFlowDetails> flowDetailsMap = callLogSummary.getFlowDetailsMap();
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

        /* ---Filling Flow Details--- */

        row.add(flowDetailsMap.get(MENU).getNumberOfTimesAccessed());
        row.add(flowDetailsMap.get(MENU).getIndividualAccessDurations());
        row.add(flowDetailsMap.get(MENU).getTotalAccessDuration());
        row.add(flowDetailsMap.get(DAILY_PILL_REMINDER_CALL).getNumberOfTimesAccessed());
        row.add(flowDetailsMap.get(DAILY_PILL_REMINDER_CALL).getIndividualAccessDurations());
        row.add(flowDetailsMap.get(DAILY_PILL_REMINDER_CALL).getTotalAccessDuration());
        row.add(flowDetailsMap.get(FOUR_DAY_RECALL_CALL).getNumberOfTimesAccessed());
        row.add(flowDetailsMap.get(FOUR_DAY_RECALL_CALL).getIndividualAccessDurations());
        row.add(flowDetailsMap.get(FOUR_DAY_RECALL_CALL).getTotalAccessDuration());
        row.add(flowDetailsMap.get(HEALTH_TIPS).getNumberOfTimesAccessed());
        row.add(flowDetailsMap.get(HEALTH_TIPS).getIndividualAccessDurations());
        row.add(flowDetailsMap.get(HEALTH_TIPS).getTotalAccessDuration());
        row.add(flowDetailsMap.get(SYMPTOMS_CALL).getNumberOfTimesAccessed());
        row.add(flowDetailsMap.get(SYMPTOMS_CALL).getIndividualAccessDurations());
        row.add(flowDetailsMap.get(SYMPTOMS_CALL).getTotalAccessDuration());
        row.add(flowDetailsMap.get(OUTBOX_CALL).getNumberOfTimesAccessed());
        row.add(flowDetailsMap.get(OUTBOX_CALL).getIndividualAccessDurations());
        row.add(flowDetailsMap.get(OUTBOX_CALL).getTotalAccessDuration());

        /* ---Filling Flow Details--- */

        row.add(callLogSummary.getAge());
        row.add(callLogSummary.getGender());
        row.add(callLogSummary.getPatientDistanceFromClinic());
        return row;
    }

    @Override
    protected List fetchData(int pageNumber) {
        return allCallLogSummaries.getAllCallLogSummariesBetween(startDate, endDate, pageNumber, pageSize);
    }
}
