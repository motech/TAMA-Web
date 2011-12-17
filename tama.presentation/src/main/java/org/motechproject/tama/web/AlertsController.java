package org.motechproject.tama.web;

import org.motechproject.tamacallflow.domain.PatientAlert;
import org.motechproject.tamacallflow.domain.SymptomsAlertStatus;
import org.motechproject.tamacallflow.service.PatientAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@RequestMapping("/alerts")
@Controller
public class AlertsController extends BaseController {

    private PatientAlertService patientAlertService;

    @Autowired
    public AlertsController(PatientAlertService patientAlertService) {
        this.patientAlertService = patientAlertService;
    }

    @RequestMapping(value = "/unread", method = RequestMethod.GET)
    public String unread(Model uiModel, HttpServletRequest request) {
        final String clinicId = loggedInClinic(request);
        uiModel.addAttribute("alerts", patientAlertService.getUnreadAlertsForClinic(clinicId));
        return "alerts/unread";
    }

    @RequestMapping(value = "/read", method = RequestMethod.GET)
    public String read(Model uiModel, HttpServletRequest request) {
        final String clinicId = loggedInClinic(request);
        uiModel.addAttribute("alerts", patientAlertService.getReadAlertsForClinic(clinicId));
        return "alerts/read";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
        PatientAlert patientAlert = patientAlertService.getPatientAlert(id);
        uiModel.addAttribute("alertInfo", patientAlert);
        return "alerts/show" + patientAlert.getAlert().getData().get(PatientAlert.PATIENT_ALERT_TYPE);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
        PatientAlert patientAlert = patientAlertService.getPatientAlert(id);
        initUIModel(id, uiModel, patientAlert);
        return "alerts/update" + patientAlert.getAlert().getData().get(PatientAlert.PATIENT_ALERT_TYPE);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String update(Model uiModel, String alertId, String symptomsAlertStatus, String notes, String doctorsNotes, String type, HttpServletRequest request) {
        try {
            patientAlertService.updateAlert(alertId, symptomsAlertStatus, notes, doctorsNotes, type);
            uiModel.asMap().clear();
        } catch (RuntimeException e) {
            PatientAlert patientAlert = patientAlertService.getPatientAlert(alertId);
            initUIModel(alertId, uiModel, patientAlert);
            return "alerts/update";
        }
        return "redirect:/alerts/" + encodeUrlPathSegment(alertId, request);
    }

    private void initUIModel(String id, Model uiModel, PatientAlert patientAlert) {
        uiModel.addAttribute("alertInfo", patientAlert);
        uiModel.addAttribute("symptomsStatuses", Arrays.asList(SymptomsAlertStatus.values()));
    }
}
