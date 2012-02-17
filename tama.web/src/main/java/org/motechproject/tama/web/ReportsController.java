package org.motechproject.tama.web;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.domain.DailyPillReminderSummary;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderReportService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.web.viewbuilder.DailyPillReminderReportBuilder;
import org.motechproject.tama.web.model.PatientReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RequestMapping("/patients/{patientDocId}/reports")
@Controller
public class ReportsController {

    private AllPatients allPatients;
    private AllTreatmentAdvices allTreatmentAdvices;
    private PatientService patientService;
    private DailyPillReminderReportService dailyPillReminderReportService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ReportsController(AllPatients allPatients,
                             AllTreatmentAdvices allTreatmentAdvices,
                             PatientService patientService,
                             DailyPillReminderReportService dailyPillReminderReportService) {
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.patientService = patientService;
        this.dailyPillReminderReportService = dailyPillReminderReportService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index(@PathVariable String patientDocId) {
        return new ModelAndView("reports/index", "report", generatePatientReportSummary(patientDocId));
    }


    @RequestMapping(value = "dailyPillReminderReport.json", method = RequestMethod.GET)
    @ResponseBody
    public String dailyPillReminderReport(@PathVariable String patientDocId,
                                          @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
                                          @RequestParam LocalDate startDate,
                                          @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
                                          @RequestParam LocalDate endDate) throws JSONException {
        return dailyPillReminderReportService.JSONReport(patientDocId, startDate, endDate).toString();
    }

    @RequestMapping(value = "dailyPillReminderReport.xls", method = RequestMethod.GET)
    public void buildDailyPillReminderExcelReport(@PathVariable String patientDocId,
                                                  @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
                                                  @RequestParam LocalDate startDate,
                                                  @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
                                                  @RequestParam LocalDate endDate,
                                                  HttpServletResponse response) {

        response.setHeader("Content-Disposition", "inline; filename=DailyPillReminderReport.xls");
        response.setContentType("application/vnd.ms-excel");
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            List<DailyPillReminderSummary> summaryList = dailyPillReminderReportService.create(patientDocId, startDate, endDate);

            DailyPillReminderReportBuilder dailyPillReminderReportBuilder = new DailyPillReminderReportBuilder(summaryList, generatePatientReportSummary(patientDocId), startDate, endDate);
            HSSFWorkbook excelWorkbook = dailyPillReminderReportBuilder.getExcelWorkbook();
            excelWorkbook.write(outputStream);
            outputStream.flush();

        } catch (Exception e) {
            logger.error("Error while generating excel report: " + e.getMessage());
        }
    }

    private PatientReport generatePatientReportSummary(String patientDocId) {
        Patient patient = allPatients.get(patientDocId);
        Regimen regimen = patientService.currentRegimen(patient);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.earliestTreatmentAdvice(patientDocId);
        return new PatientReport(patient, regimen, treatmentAdvice);
    }
}
