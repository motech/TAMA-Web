package org.motechproject.tama.web;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.web.model.PatientReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/patients/{patientId}/reports")
@Controller
public class ReportsController {

    private AllPatients allPatients;
    private PatientService patientService;

    @Autowired
    public ReportsController(AllPatients allPatients, PatientService patientService) {
        this.allPatients = allPatients;
        this.patientService = patientService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index(@PathVariable String patientId) {
        Patient patient = allPatients.get(patientId);
        Regimen regimen = patientService.currentRegimen(patient);
        return new PatientReport(patient, regimen, "reports/index");
    }
}
