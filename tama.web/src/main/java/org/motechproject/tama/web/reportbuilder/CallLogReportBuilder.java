package org.motechproject.tama.web.reportbuilder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.log.CallFlowDetails;
import org.motechproject.tama.ivr.repository.AllCallLogs;
import org.motechproject.tama.patient.domain.Patients;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.objectcache.AllIVRLanguagesCache;
import org.motechproject.tama.web.builder.CallLogSummaryBuilder;
import org.motechproject.tama.web.model.CallLogSummary;
import org.motechproject.tama.web.reportbuilder.abstractbuilder.BatchReportBuilder;
import org.motechproject.tama.web.reportbuilder.model.ExcelColumn;
import org.motechproject.util.DateUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.motechproject.tama.common.CallTypeConstants.*;

public class CallLogReportBuilder extends BatchReportBuilder {

    private AllCallLogs allCallLogs;
    private AllPatients allPatients;
    private AllIVRLanguagesCache allIVRLanguages;

    private LocalDate startDate;
    private LocalDate endDate;
    private DateTime startKey;
    private DateTime endKey;
    private String startDocId;

    public CallLogReportBuilder(AllCallLogs allCallLogs, AllPatients allPatients, AllIVRLanguagesCache allIVRLanguages, LocalDate startDate, LocalDate endDate) {
        super();
        this.allCallLogs = allCallLogs;
        this.allPatients = allPatients;
        this.allIVRLanguages = allIVRLanguages;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startKey = DateUtil.newDateTime(startDate);
        this.endKey = DateUtil.newDateTime(endDate, 23, 59, 59);
    }

    @Override
    protected String getWorksheetName() {
        return "CallSummaryReport";
    }

    @Override
    protected String getTitle() {
        return "Call Summary Report";
    }

    @Override
    protected void initializeColumns() {
        columns = new LinkedList<ExcelColumn>();
        columns.add(new ExcelColumn("Patient ID", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Source Phone Number", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Destination Phone Number", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Clinic Name", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("TAMA Initiated Call At (yyyy-mm-dd hh:mm:ss)", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Call Started At (yyyy-mm-dd hh:mm:ss)", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Call Ended At (yyyy-mm-dd hh:mm:ss)", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Language", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Flows Accessed", Cell.CELL_TYPE_STRING, 14000));
        columns.add(new ExcelColumn("Menu Accessed (No. of times)", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Menu - Individual Durations (Seconds)", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Menu - Total Duration (Seconds)", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Pill Reminder Accessed (No. of times)", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Pill Reminder - Individual Durations (Seconds)", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Pill Reminder - Total Duration (Seconds)", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Four Day Recall Accessed (No. of times)", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Four Day Recall - Individual Durations (Seconds)", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Four Day Recall - Total Duration (Seconds)", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Pushed Messages", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Messages Accessed (No. of times)", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Messages - Individual Durations (Seconds)", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Messages - Total Duration (Seconds)", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Message category", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Symptom Reporting Accessed (No. of times)", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Symptom Reporting - Individual Durations (Seconds)", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Symptom Reporting - Total Duration (Seconds)", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Outbox Accessed (No. of times)", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Outbox - Individual Durations (Seconds)", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Outbox - Total Duration (Seconds)", Cell.CELL_TYPE_STRING, 8000));
        columns.add(new ExcelColumn("Age", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Gender", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Distance of Patient from Clinic", Cell.CELL_TYPE_STRING, 8000));
    }

    @Override
    protected List<Object> getRowData(Object object) {
        CallLogSummary callLogSummary = (CallLogSummary) object;
        Map<String, CallFlowDetails> flowDetailsMap = callLogSummary.getFlowDetailsMap();
        List<Object> row = new LinkedList<Object>();
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
        row.add(flowDetailsMap.get(PUSHED_MESSAGES).getResponsesAsString());
        row.add(flowDetailsMap.get(MESSAGES).getNumberOfTimesAccessed());
        row.add(flowDetailsMap.get(MESSAGES).getIndividualAccessDurations());
        row.add(flowDetailsMap.get(MESSAGES).getTotalAccessDuration());

        row.add(callLogSummary.getMessageCategories());

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
    protected List fetchData() {
        List<CallLog> callLogs = allCallLogs.findAllCallLogsForDateRange(startKey, endKey, startDocId == null ? pageSize : pageSize + 1, startDocId);
        removeDocumentMatchingStartKey(callLogs);
        incrementIndexOfBatch(callLogs);
        return buildSummaries(callLogs);
    }

    private void removeDocumentMatchingStartKey(List<CallLog> callLogs) {
        if (startDocId != null) callLogs.remove(0);
    }

    private void incrementIndexOfBatch(List<CallLog> callLogs) {
        if (CollectionUtils.isNotEmpty(callLogs)) {
            final CallLog lastCallLog = callLogs.get(callLogs.size() - 1);
            startKey = lastCallLog.getStartTime();
            startDocId = lastCallLog.getId();
        }
    }

    private List buildSummaries(List<CallLog> callLogs) {
        List<CallLogSummary> callLogSummaries = new LinkedList<CallLogSummary>();
        CallLogSummaryBuilder callSummaryBuilder = new CallLogSummaryBuilder(allPatients,
                new Patients(allPatients.getAll()),
                allIVRLanguages
        );
        for (CallLog callLog : callLogs) {
            callLogSummaries.add(callSummaryBuilder.build(callLog));
        }
        return callLogSummaries;
    }

    @Override
    protected void buildSummary(HSSFSheet worksheet) {
        List<HSSFCellStyle> cellStyles = buildCellStylesForSummary(worksheet);
        buildSummaryRow(worksheet, cellStyles, "Report Start Date", startDate.toString(TAMAConstants.DATE_FORMAT));
        buildSummaryRow(worksheet, cellStyles, "Report End Date", endDate.toString(TAMAConstants.DATE_FORMAT));
    }
}
