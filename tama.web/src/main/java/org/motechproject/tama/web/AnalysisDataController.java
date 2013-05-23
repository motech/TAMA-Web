package org.motechproject.tama.web;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.clinicvisits.contract.AppointmentCalenderReport;
import org.motechproject.tama.clinicvisits.domain.ClinicVisitSummary;
import org.motechproject.tama.clinicvisits.service.AppointmentCalenderReportService;
import org.motechproject.tama.clinicvisits.service.ClinicVisitReportService;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.contract.DailyPillReminderReport;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderReportService;
import org.motechproject.tama.facility.service.ClinicService;
import org.motechproject.tama.outbox.contract.OutboxMessageReport;
import org.motechproject.tama.outbox.service.OutboxMessageReportService;
import org.motechproject.tama.patient.domain.PatientAlert;
import org.motechproject.tama.patient.reporting.PatientAlertsReport;
import org.motechproject.tama.patient.service.PatientAlertsReportService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.web.model.*;
import org.motechproject.tama.web.reportbuilder.*;
import org.motechproject.tama.web.reportbuilder.abstractbuilder.InMemoryReportBuilder;
import org.motechproject.tama.web.service.CallLogExcelReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Controller
@RequestMapping("analysisData")
public class AnalysisDataController extends BaseController {

    private CallSummaryController callSummaryController;
    private ReportingProperties reportingProperties;
    private AppointmentCalenderReportService appointmentCalenderReportService;
    private OutboxMessageReportService outboxMessageReportService;
    private DailyPillReminderReportService dailyPillReminderReportService;
    private ClinicVisitReportService clinicVisitReportService;
    private CallLogExcelReportService callLogExcelReportService;
    private ClinicService clinicService;
    private PatientAlertsReportService patientAlertsReportService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AnalysisDataController(CallSummaryController callSummaryController,
                                  ReportingProperties reportingProperties,
                                  AppointmentCalenderReportService appointmentCalenderReportService,
                                  OutboxMessageReportService outboxMessageReportService,
                                  DailyPillReminderReportService dailyPillReminderReportService,
                                  ClinicVisitReportService clinicVisitReportService, CallLogExcelReportService callLogExcelReportService,
                                  ClinicService clinicService, PatientAlertsReportService patientAlertsReportService) {
        this.callSummaryController = callSummaryController;
        this.reportingProperties = reportingProperties;
        this.appointmentCalenderReportService = appointmentCalenderReportService;
        this.outboxMessageReportService = outboxMessageReportService;
        this.dailyPillReminderReportService = dailyPillReminderReportService;
        this.clinicVisitReportService = clinicVisitReportService;
        this.callLogExcelReportService = callLogExcelReportService;
        this.clinicService = clinicService;
        this.patientAlertsReportService = patientAlertsReportService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String show(Model uiModel) {
        uiModel.addAttribute("clinicFilter", new ClinicFilter(clinicService.getAllClinics()));
        uiModel.addAttribute("patientIdFilter", new PatientIDFilter());
        uiModel.addAttribute("patientReportFilter", new ClinicAndDateFilter());
        uiModel.addAttribute("otcSmsFilter", new ClinicAndDateFilter());
        uiModel.addAttribute("clinicianSmsFilter", new ClinicAndDateFilter());
        uiModel.addAttribute("patientEventFilter", new PatientEventFilter());
        uiModel.addAttribute("outboxMessageReportFilter", new FilterWithPatientIDAndDateRange());
        uiModel.addAttribute("messagesReportFilter", new ClinicAndDateFilter());
        uiModel.addAttribute("dosageAdherenceReportFilter", new FilterWithPatientIDAndDateRange());
        uiModel.addAttribute("reports_url", reportingProperties.reportingURL());
        uiModel.addAttribute("patientAlertsReportFilter", new PatientAlertsReportFilter());
        callSummaryController.download(uiModel);
        return "analysisData/show";
    }

    @RequestMapping(value = "/patientEventReport.xls", method = RequestMethod.GET)
    public String downloadPatientEventReport(@RequestParam("clinicId") String clinicId,
                                             @RequestParam("patientId") String patientId,
                                             @RequestParam("startDate") @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT) LocalDate startDate,
                                             @RequestParam("endDate") @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT) LocalDate endDate,
                                             @RequestParam("eventName") String eventName,
                                             Model uiModel) {
        DateFilter filter = new DateFilter().setDates(startDate, endDate);
        if (filter.isMoreThanOneYear()) {
            return error(uiModel, "patientEventReport_warning");
        } else {
            return format("redirect:/tama-reports/patientEvent/report?clinicId=%s&patientId=%s&startDate=%s&endDate=%s&eventName=%s", clinicId, patientId, startDate.toString("dd/MM/yyyy"), endDate.toString("dd/MM/yyyy"), eventName);
        }
    }

    @RequestMapping(value = "/patientRegistrationReport.xls", method = RequestMethod.GET)
    public String downloadPatientRegistrationReport(@RequestParam("clinicId") String clinicId,
                                                    @RequestParam("patientId") String patientId,
                                                    @RequestParam("startDate") @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT) LocalDate startDate,
                                                    @RequestParam("endDate") @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT) LocalDate endDate,
                                                    Model uiModel) {
        DateFilter filter = new DateFilter().setDates(startDate, endDate);
        if (filter.isMoreThanOneYear()) {
            return error(uiModel, "patientRegistrationReport_warning");
        } else {
            return format("redirect:/tama-reports/patient/report?clinicId=%s&patientId=%s&startDate=%s&endDate=%s", clinicId, patientId, startDate.toString("dd/MM/yyyy"), endDate.toString("dd/MM/yyyy"));
        }
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


    @RequestMapping(value = "/patientAlertsReport.xls", method = RequestMethod.GET)
    public void downloadPatientAlertsReport(@RequestParam("clinicId") String clinicId,
                                            @RequestParam("patientId") String patientId,
                                            @RequestParam("startDate") @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT) LocalDate startDate,
                                            @RequestParam("endDate") @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT) LocalDate endDate,
                                            @RequestParam("patientAlertType") String patientAlertType,
                                            @RequestParam("patientAlertStatus") String patientAlertStatus, Model uiModel, HttpServletResponse response) {

        DateTime alertStartDate = DateTime.parse(startDate.toString());
        DateTime alertEndDate = DateTime.parse(endDate.toString());

        PatientAlertsReport patientAlertsReport = patientAlertsReportService.report(patientId, alertStartDate, alertEndDate, patientAlertType, clinicId, patientAlertStatus);
        List<PatientAlert> alerts = new ArrayList<>();

        for (int i = 0; i < patientAlertsReport.getPatientAlerts().size(); i++) {
            alerts.add(patientAlertsReport.getPatientAlerts().get(i));
        }
        AllPatientAlertsReportsBuilder allPatientAlertsReportsBuilder = new AllPatientAlertsReportsBuilder(patientAlertsReport.getPatientReports(), alerts, patientId);

        try {
            writeExcelToResponse(response, allPatientAlertsReportsBuilder, "PatientAlertsReport");
        } catch (Exception e) {
            logger.error("Error while generating excel report: " + e.getMessage());
        }

    }

    @RequestMapping(value = "/outboxMessageReport.xls", method = RequestMethod.GET)
    public String downloadOutboxMessageReport(FilterWithPatientIDAndDateRange filter, Model uiModel, HttpServletResponse response) {
        if (filter.isMoreThanOneYear()) {
            return error(uiModel, "outboxReport_warning");
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
        if (filter.isMoreThanOneYear()) {
            return error(uiModel, "dailyPillReminderReport_warning");
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

    @RequestMapping(value = "/smsReport.xls", method = RequestMethod.GET)
    public String downloadSMSReport(DateFilter filter, @RequestParam("clinicId") String clinicId, @RequestParam("externalId") String externalId, @RequestParam("type") String type, Model model) {
        if (filter.isMoreThanOneYear()) {
            return error(model, type + "Report_warning");
        } else {
            return format(
                    "redirect:/tama-reports/smsLog/report?clinicId=%s&externalId=%s&startDate=%s&endDate=%s&type=%s",
                    clinicId,
                    externalId,
                    filter.getStartDate().toString("dd/MM/yyyy"),
                    filter.getEndDate().toString("dd/MM/yyyy"),
                    type
            );
        }
    }

    @RequestMapping(value = "/messagesReport.xls", method = RequestMethod.GET)
    public String downloadMessagesReport(@RequestParam("clinicId") String clinicId,
                                         @RequestParam("patientId") String patientId,
                                         @RequestParam("startDate") @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT) LocalDate startDate,
                                         @RequestParam("endDate") @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT) LocalDate endDate,
                                         Model uiModel) {
        DateFilter filter = new DateFilter().setDates(startDate, endDate);
        if (filter.isMoreThanOneYear()) {
            return error(uiModel, "messages_warning");
        } else {
            return format("redirect:/tama-reports/messages/report?clinicId=%s&patientId=%s&startDate=%s&endDate=%s", clinicId, patientId, startDate.toString("dd/MM/yyyy"), endDate.toString("dd/MM/yyyy"));
        }
    }

    @RequestMapping(value = "/callLogReport.xls", method = RequestMethod.GET)
    public String downloadCallLogExcelReport(DateFilter filter, Model model, HttpServletResponse response) {
        if (filter.isMoreThanOneYear()) {
            return error(model, "callLogReport_warning");
        }

        HSSFWorkbook callLogReport = callLogExcelReportService.buildReport(filter.startDate, filter.endDate, true);
        try {
            writeExcelToResponse(response, callLogReport, "CallSummaryReport.xls");
        } catch (Exception e) {
            logger.error("Error while generating excel report: " + e.getMessage());
        }

        return null;
    }

    @RequestMapping(value = "/clinicianReport.xls", method = RequestMethod.GET)
    public String downloadClinicianReport(Model uiModel) {
        return "redirect:/tama-reports/clinician/report";
    }

    @RequestMapping(value = "/clinicReport.xls", method = RequestMethod.GET)
    public String downloadClinicReport(Model uiModel) {
        return "redirect:/tama-reports/clinic/report";
    }

    @RequestMapping(value = "/clinicVisitReport.xls", method = RequestMethod.GET)
    public void downloadClinicVisitReport(@RequestParam(value = "clinicVisitPatientId", required = true) String patientId, HttpServletResponse response) {
        List<ClinicVisitSummary> clinicVisitSummaries = clinicVisitReportService.getClinicVisitReport(patientId);
        ClinicVisitReportBuilder clinicVisitReportBuilder = new ClinicVisitReportBuilder(clinicVisitSummaries);
        try {
            writeExcelToResponse(response, clinicVisitReportBuilder, "ClinicVisitReport");
        } catch (Exception e) {
            logger.error("Error while generating excel report: " + e.getMessage());
        }
    }

    private String error(Model uiModel, String warning) {
        String view = show(uiModel);
        uiModel.addAttribute(warning, "There is too much data to load. Please narrow down the date range in your search criteria");
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

    private void writeExcelToResponse(HttpServletResponse response, HSSFWorkbook excelWorkbook, String fileName) {
        try {
            initializeExcelResponse(response, fileName);
            ServletOutputStream outputStream = response.getOutputStream();
            if (null != excelWorkbook) {
                excelWorkbook.write(outputStream);
            }
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
