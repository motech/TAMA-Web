package org.motechproject.tama.web;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.motechproject.tama.clinicvisits.contract.AppointmentCalenderReport;
import org.motechproject.tama.clinicvisits.service.AppointmentCalenderReportService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.web.model.DateFilter;
import org.motechproject.tama.web.model.PatientIDFilter;
import org.motechproject.tama.web.resportbuilder.AllAppointmentCalendarsBuilder;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AnalysisDataController(CallSummaryController callSummaryController, ReportingProperties reportingProperties, AppointmentCalenderReportService appointmentCalenderReportService) {
        this.callSummaryController = callSummaryController;
        this.reportingProperties = reportingProperties;
        this.appointmentCalenderReportService = appointmentCalenderReportService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String show(Model uiModel) {
        uiModel.addAttribute("patientIdFilter", new PatientIDFilter());
        uiModel.addAttribute("patientDateFilter", new DateFilter());
        uiModel.addAttribute("patientEventDateFilter", new DateFilter());
        uiModel.addAttribute("reports_url", reportingProperties.reportingURL());
        callSummaryController.filterLogs(uiModel);
        return "analysisData/show";
    }

    @RequestMapping(value = "/appointmentCalendarReport.xls", method = RequestMethod.GET)
    public void downloadAppointmentCalenderReport(@RequestParam(value = "patientId", required = true) String patientId, HttpServletResponse response) {
        response.setHeader("Content-Disposition", "inline; filename=AppointmentCalendarReport.xls");
        response.setContentType("application/vnd.ms-excel");
        try {
            AppointmentCalenderReport appointmentCalendarReport = appointmentCalenderReportService.appointmentCalendarReport(patientId);
            AllAppointmentCalendarsBuilder appointmentCalendarBuilder = new AllAppointmentCalendarsBuilder(appointmentCalendarReport.getClinicVisits(), appointmentCalendarReport.getPatientReports());
            writeExcelToResponse(response, appointmentCalendarBuilder);
        } catch (Exception e) {
            logger.error("Error while generating excel report: " + e.getMessage());
        }
    }

    private void writeExcelToResponse(HttpServletResponse response, InMemoryReportBuilder appointmentCalendarBuilder) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        HSSFWorkbook excelWorkbook = appointmentCalendarBuilder.getExcelWorkbook();
        excelWorkbook.write(outputStream);
        outputStream.flush();
    }
}
