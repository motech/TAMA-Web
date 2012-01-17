package org.motechproject.tama.web;

import org.motechproject.tama.patient.domain.PatientAlert;
import org.motechproject.tama.patient.domain.PatientAlerts;
import org.motechproject.tama.patient.domain.SymptomsAlertStatus;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.tama.web.view.AlertFilter;
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

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model uiModel, HttpServletRequest request) {
        AlertFilter unreadAlertsFilter = new AlertFilter().setAlertStatus(AlertFilter.STATUS_UNREAD);
        final String clinicId = loggedInClinic(request);
        uiModel.addAttribute("alerts", getFilteredAlerts(unreadAlertsFilter, clinicId));
        uiModel.addAttribute("alertFilter", unreadAlertsFilter);
        return "alerts/list";
    }

    @RequestMapping(value = "/list/filter", method = RequestMethod.GET)
    public String list(AlertFilter filter, Model uiModel, HttpServletRequest request) {
        final String clinicId = loggedInClinic(request);
        uiModel.addAttribute("alerts", getFilteredAlerts(filter, clinicId));
        uiModel.addAttribute("alertFilter", filter);
        return "alerts/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
        PatientAlert patientAlert = patientAlertService.readAlert(id);
        uiModel.addAttribute("alertInfo", patientAlert);
        return "alerts/show" + patientAlert.getAlert().getData().get(PatientAlert.PATIENT_ALERT_TYPE);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
        PatientAlert patientAlert = patientAlertService.readAlert(id);
        initUIModel(id, uiModel, patientAlert);
        return "alerts/update" + patientAlert.getAlert().getData().get(PatientAlert.PATIENT_ALERT_TYPE);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String update(Model uiModel, String alertId, String symptomsAlertStatus, String notes, String doctorsNotes, String type, HttpServletRequest request) {
        try {
            patientAlertService.updateAlert(alertId, symptomsAlertStatus, notes, doctorsNotes, type);
            uiModel.asMap().clear();
        } catch (RuntimeException e) {
            PatientAlert patientAlert = patientAlertService.readAlert(alertId);
            initUIModel(alertId, uiModel, patientAlert);
            return "alerts/update";
        }
        return "redirect:/alerts/" + encodeUrlPathSegment(alertId, request);
    }

    private void initUIModel(String id, Model uiModel, PatientAlert patientAlert) {
        uiModel.addAttribute("alertInfo", patientAlert);
        uiModel.addAttribute("symptomsStatuses", Arrays.asList(SymptomsAlertStatus.values()));
    }

    private PatientAlerts getFilteredAlerts(AlertFilter filter, String clinicId) {
        if (filter.getAlertStatus().equals(AlertFilter.STATUS_UNREAD))
            return patientAlertService.getUnreadAlertsFor(clinicId, filter.getPatientId(), filter.getPatientAlertType(), filter.getStartDateTime(), filter.getEndDateTime());
        return patientAlertService.getReadAlertsFor(clinicId, filter.getPatientId(), filter.getPatientAlertType(), filter.getStartDateTime(), filter.getEndDateTime());
    }
}
