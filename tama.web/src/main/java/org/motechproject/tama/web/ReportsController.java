package org.motechproject.tama.web;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderReportService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.web.model.PatientReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/patients/{patientDocId}/reports")
@Controller
public class ReportsController {

    private AllPatients allPatients;
    private PatientService patientService;
    private DailyPillReminderReportService dailyPillReminderReportService;

    @Autowired
    public ReportsController(AllPatients allPatients,
                             PatientService patientService,
                             DailyPillReminderReportService dailyPillReminderReportService) {
        this.allPatients = allPatients;
        this.patientService = patientService;
        this.dailyPillReminderReportService = dailyPillReminderReportService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index(@PathVariable String patientDocId) {
        Patient patient = allPatients.get(patientDocId);
        Regimen regimen = patientService.currentRegimen(patient);
        return new PatientReport(patient, regimen, "reports/index");
    }

    @RequestMapping(value = "dailyPillReminderReport", method = RequestMethod.GET)
    @ResponseBody
    public String dailyPillReminderReport(@PathVariable String patientDocId,
                                          @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
                                          @RequestParam LocalDate startDate,
                                          @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
                                          @RequestParam LocalDate endDate) throws JSONException {
        return dailyPillReminderReportService.generateJSON(patientDocId, startDate, endDate).toString();
    }
}
