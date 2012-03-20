package org.motechproject.tama.web;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.domain.DailyPillReminderSummary;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderReportService;
import org.motechproject.tama.outbox.domain.OutboxMessageSummary;
import org.motechproject.tama.outbox.integration.repository.AllOutboxMessageSummaries;
import org.motechproject.tama.outbox.service.OutboxMessageReportService;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.web.model.CallLogSummary;
import org.motechproject.tama.web.service.AllCallLogSummaries;
import org.motechproject.tama.web.viewbuilder.CallLogReportBuilder;
import org.motechproject.tama.web.viewbuilder.DailyPillReminderReportBuilder;
import org.motechproject.tama.web.viewbuilder.OutboxReportBuilder;
import org.motechproject.tama.web.viewbuilder.ReportBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@Controller
public class ReportsController {

    private PatientService patientService;
    private DailyPillReminderReportService dailyPillReminderReportService;
    private OutboxMessageReportService outboxMessageReportService;
    private AllOutboxMessageSummaries allOutboxMessageSummaries;
    private AllCallLogSummaries allCallLogSummaries;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public ReportsController(PatientService patientService,
                             DailyPillReminderReportService dailyPillReminderReportService,
                             OutboxMessageReportService outboxMessageReportService,
                             AllOutboxMessageSummaries allOutboxMessageSummaries,
                             AllCallLogSummaries allCallLogSummaries) {
        this.patientService = patientService;
        this.dailyPillReminderReportService = dailyPillReminderReportService;
        this.outboxMessageReportService = outboxMessageReportService;
        this.allOutboxMessageSummaries = allOutboxMessageSummaries;
        this.allCallLogSummaries = allCallLogSummaries;
    }

    @RequestMapping(value = "/patients/{patientDocId}/reports", method = RequestMethod.GET)
    public ModelAndView index(@PathVariable String patientDocId) {
        return new ModelAndView("reports/index", "report", patientService.getPatientReport(patientDocId));
    }


    @RequestMapping(value = "/patients/{patientDocId}/reports/dailyPillReminderReport.json", method = RequestMethod.GET)
    @ResponseBody
    public String dailyPillReminderReport(@PathVariable String patientDocId,
                                          @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
                                          @RequestParam LocalDate startDate,
                                          @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
                                          @RequestParam LocalDate endDate) throws JSONException {
        return dailyPillReminderReportService.JSONReport(patientDocId, startDate, endDate).toString();
    }

    @RequestMapping(value = "/patients/{patientDocId}/reports/outboxMessageReport.json", method = RequestMethod.GET)
    @ResponseBody
    public String outboxMessageReport(@PathVariable String patientDocId,
                                      @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
                                      @RequestParam LocalDate startDate,
                                      @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
                                      @RequestParam LocalDate endDate) throws JSONException {
        return outboxMessageReportService.JSONReport(patientDocId, startDate, endDate).toString();
    }

    @RequestMapping(value = "/patients/{patientDocId}/reports/dailyPillReminderReport.xls", method = RequestMethod.GET)
    public void buildDailyPillReminderExcelReport(@PathVariable String patientDocId,
                                                  @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
                                                  @RequestParam LocalDate startDate,
                                                  @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
                                                  @RequestParam LocalDate endDate,
                                                  HttpServletResponse response) {
        List<DailyPillReminderSummary> summaryList = dailyPillReminderReportService.create(patientDocId, startDate, endDate);
        DailyPillReminderReportBuilder dailyPillReminderReportBuilder = new DailyPillReminderReportBuilder(summaryList, patientService.getPatientReport(patientDocId), startDate, endDate);
        writeExcelToResponse(response, createExcelReport(dailyPillReminderReportBuilder), "DailyPillReminderReport.xls");
    }

    @RequestMapping(value = "/patients/{patientDocId}/reports/outboxMessageReport.xls", method = RequestMethod.GET)
    public void buildOutboxMessageExcelReport(@PathVariable String patientDocId,
                                              @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
                                              @RequestParam LocalDate startDate,
                                              @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
                                              @RequestParam LocalDate endDate,
                                              HttpServletResponse response) {
        List<OutboxMessageSummary> messageSummaryList = allOutboxMessageSummaries.find(patientDocId, startDate, endDate);
        OutboxReportBuilder outboxReportBuilder = new OutboxReportBuilder(messageSummaryList, patientService.getPatientReport(patientDocId), startDate, endDate);
        writeExcelToResponse(response, createExcelReport(outboxReportBuilder), "OutboxReport.xls");
    }

    @RequestMapping(value = "/reports/callLogReport.xls", method = RequestMethod.GET)
    public void buildCallLogExcelReport(@DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
                                        @RequestParam LocalDate startDate,
                                        @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
                                        @RequestParam LocalDate endDate,
                                        HttpServletResponse response) {

        List<CallLogSummary> callLogSummaryList = allCallLogSummaries.getAllCallLogSummariesBetween(startDate, endDate);
        CallLogReportBuilder callLogReportBuilder = new CallLogReportBuilder(callLogSummaryList);
        writeExcelToResponse(response, createExcelReport(callLogReportBuilder), "AllCallLogsReport.xls");
    }

    private HSSFWorkbook createExcelReport(ReportBuilder reportBuilder) {
        try {
            return reportBuilder.getExcelWorkbook();
        } catch (Exception e) {
            logger.error("Error while generating excel report: " + e.getMessage());
        }
        return null;
    }

    private void writeExcelToResponse(HttpServletResponse response, HSSFWorkbook excelWorkbook, String fileName) {
        try {
            initializeExcelResponse(response, fileName);
            ServletOutputStream outputStream = response.getOutputStream();
            if (null != excelWorkbook)
                excelWorkbook.write(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            logger.error("Error while writing excel report to response: " + e.getMessage());
        }
    }

    private void initializeExcelResponse(HttpServletResponse response, String fileName) {
        response.setHeader("Content-Disposition", "inline; filename=" + fileName);
        response.setContentType("application/vnd.ms-excel");
    }
}
