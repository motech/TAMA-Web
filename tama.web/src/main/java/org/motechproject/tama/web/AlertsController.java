package org.motechproject.tama.web;

import org.joda.time.DateTime;
import org.motechproject.tama.patient.domain.PatientAlert;
import org.motechproject.tama.patient.domain.PatientAlertType;
import org.motechproject.tama.patient.domain.SymptomsAlertStatus;
import org.motechproject.tama.patient.service.PatientAlertService;
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
        String patientId = getPatientId(request);
        PatientAlertType patientAlertType = getPatientAlertType(request);
        DateTime startDate = parseDateTime(request.getParameter("startDate"));
        DateTime endDate = parseDateTime(request.getParameter("endDate"));
        uiModel.addAttribute("alerts", patientAlertService.getUnreadAlertsFor(clinicId, patientId, patientAlertType, startDate, endDate));
        return "alerts/unread";
    }

    @RequestMapping(value = "/read", method = RequestMethod.GET)
    public String read(Model uiModel, HttpServletRequest request) {
        final String clinicId = loggedInClinic(request);
        String patientId = getPatientId(request);
        PatientAlertType patientAlertType = getPatientAlertType(request);
        DateTime startDate = parseDateTime(request.getParameter("startDate"));
        DateTime endDate = parseDateTime(request.getParameter("endDate"));
        uiModel.addAttribute("alerts", patientAlertService.getReadAlertsFor(clinicId, patientId, patientAlertType, startDate, endDate));
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

    private String getPatientId(HttpServletRequest request){
        String patientId = request.getParameter("patientId");
        return (patientId == null || patientId == "") ? null : patientId;
    }

    private PatientAlertType getPatientAlertType(HttpServletRequest request){
        String patientAlertType = request.getParameter("patientAlertType");
        return (patientAlertType == null || patientAlertType == "") ? null : PatientAlertType.valueOf(patientAlertType);
    }

    private DateTime parseDateTime(String dateTime){
        return (dateTime == null || dateTime == "") ? null : DateTime.parse(dateTime);
    }
}
