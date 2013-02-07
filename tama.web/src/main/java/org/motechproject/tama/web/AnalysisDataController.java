package org.motechproject.tama.web;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.motechproject.tama.clinicvisits.contract.AppointmentCalenderReport;
import org.motechproject.tama.clinicvisits.service.AppointmentCalenderReportService;
import org.motechproject.tama.dailypillreminder.contract.DailyPillReminderReport;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderReportService;
import org.motechproject.tama.outbox.contract.OutboxMessageReport;
import org.motechproject.tama.outbox.service.OutboxMessageReportService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.web.model.FilterWithPatientIDAndDateRange;
import org.motechproject.tama.web.model.DateFilter;
import org.motechproject.tama.web.model.PatientIDFilter;
import org.motechproject.tama.web.resportbuilder.AllAppointmentCalendarsBuilder;
import org.motechproject.tama.web.resportbuilder.AllDailyPillReminderReportsBuilder;
import org.motechproject.tama.web.resportbuilder.AllOutboxReportsBuilder;
import org.motechproject.tama.web.resportbuilder.abstractbuilder.InMemoryReportBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("analysisData")
public class AnalysisDataController extends BaseController {

    private CallSummaryController callSummaryController;
    private ReportingProperties reportingProperties;
    private AppointmentCalenderReportService appointmentCalenderReportService;
    private OutboxMessageReportService outboxMessageReportService;
    private DailyPillReminderReportService dailyPillReminderReportService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AnalysisDataController(CallSummaryController callSummaryController, ReportingProperties reportingProperties, AppointmentCalenderReportService appointmentCalenderReportService, OutboxMessageReportService outboxMessageReportService, DailyPillReminderReportService dailyPillReminderReportService) {
        this.callSummaryController = callSummaryController;
        this.reportingProperties = reportingProperties;
        this.appointmentCalenderReportService = appointmentCalenderReportService;
        this.outboxMessageReportService = outboxMessageReportService;
        this.dailyPillReminderReportService = dailyPillReminderReportService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String show(Model uiModel) {
        uiModel.addAttribute("patientIdFilter", new PatientIDFilter());
        uiModel.addAttribute("patientDateFilter", new DateFilter());
        uiModel.addAttribute("patientEventDateFilter", new DateFilter());
        uiModel.addAttribute("outboxMessageReportFilter", new FilterWithPatientIDAndDateRange());
        uiModel.addAttribute("dosageAdherenceReportFilter", new FilterWithPatientIDAndDateRange());
        uiModel.addAttribute("reports_url", reportingProperties.reportingURL());
        callSummaryController.filterLogs(uiModel);
        return "analysisData/show";
    }

    @RequestMapping(value = "/appointmentCalendarReport.xls", method = RequestMethod.GET)
    public void downloadAppointmentCalenderReport(@RequestParam(value = "patientId", required = true) String patientId, HttpServletResponse response) {
        AppointmentCalenderReport appointmentCalendarReport = appointmentCalenderReportService.appointmentCalendarReport(patientId);
        AllAppointmentCalendarsBuilder appointmentCalendarBuilder = new AllAppointmentCalendarsBuilder(appointmentCalendarReport.getClinicVisits(), appointmentCalendarReport.getPatientReports());
        try {
            writeExcelToResponse(response, appointmentCalendarBuilder, "AppointmentCalendarReport");
        } catch (Exception e) {
            logger.error("Error while generating excel report: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/outboxMessageReport.xls", method = RequestMethod.GET)
    public String downloadOutboxMessageReport(FilterWithPatientIDAndDateRange filter, Model uiModel, HttpServletResponse response) {
        if (filter.isMoreThanOneMonth()) {
            return error(uiModel);
        }
        OutboxMessageReport outboxMessageReport = outboxMessageReportService.reports(filter.getPatientId(), filter.getStartDate(), filter.getEndDate());
        AllOutboxReportsBuilder allOutboxReportsBuilder = new AllOutboxReportsBuilder(outboxMessageReport.getOutboxMessageSummaries(), outboxMessageReport.getPatientReports());
        try {
            writeExcelToResponse(response, allOutboxReportsBuilder, "OutboxSummaryReport");
        } catch (Exception e) {
            logger.error("Error while generating excel report: " + e.getMessage());
        }
        return null;
    }

    @RequestMapping(value = "/dailyPillReminderReport.xls", method = RequestMethod.GET)
    public String downloadDailyPillReminderReport(FilterWithPatientIDAndDateRange filter, Model uiModel, HttpServletResponse response) {
        if (filter.isMoreThanOneMonth()) {
            return error(uiModel);
        }
        DailyPillReminderReport dailyPillReminderReport = dailyPillReminderReportService.reports(filter.getPatientId(), filter.getStartDate(), filter.getEndDate());
        AllDailyPillReminderReportsBuilder allDailyPillReminderReportsBuilder = new AllDailyPillReminderReportsBuilder(dailyPillReminderReport.getDailyPillReminderSummaries(), dailyPillReminderReport.getPatientReports());
        try {
            writeExcelToResponse(response, allDailyPillReminderReportsBuilder, "DailyPillReminderReport");
        } catch (Exception e) {
            logger.error("Error while generating excel report: " + e.getMessage());
        }
        return null;
    }

    private String error(Model uiModel) {
        String view = show(uiModel);
        uiModel.addAttribute("warning", "There is too much data. Please narrow your search");
        return view;
    }

    private void writeExcelToResponse(HttpServletResponse response, InMemoryReportBuilder appointmentCalendarBuilder, String appointmentCalendarReport) throws IOException {
        response.setHeader("Content-Disposition", "inline; filename=" + appointmentCalendarReport + ".xls");
        response.setContentType("application/vnd.ms-excel");

        ServletOutputStream outputStream = response.getOutputStream();
        HSSFWorkbook excelWorkbook = appointmentCalendarBuilder.getExcelWorkbook();
        excelWorkbook.write(outputStream);
        outputStream.flush();
    }
}
