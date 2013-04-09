package org.motechproject.tama.web;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.patient.domain.PatientAlert;
import org.motechproject.tama.patient.domain.PatientAlerts;
import org.motechproject.tama.patient.domain.TamaAlertStatus;
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
    private String baseUrlFormat = "%s://%s:%d%s/";

    @Autowired
    public AlertsController(PatientAlertService patientAlertService) {
        this.patientAlertService = patientAlertService;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model uiModel, HttpServletRequest request) {
        AlertFilter allAlertsFilter = new AlertFilter().setAlertStatus(AlertFilter.STATUS_OPEN);
        final String clinicId = loggedInClinic(request);
        uiModel.addAttribute("alerts", getFilteredAlerts(allAlertsFilter, clinicId));
        uiModel.addAttribute("alertFilter", allAlertsFilter);
        uiModel.addAttribute("selectedMenuItem","ALL_ALERTS");
        return "alerts/list";
    }

    @RequestMapping(value = "/list/filter", method = RequestMethod.GET)
    public String list(AlertFilter filter, Model uiModel, HttpServletRequest request) {
        final String clinicId = loggedInClinic(request);
        uiModel.addAttribute("alerts", getFilteredAlerts(filter, clinicId));
        uiModel.addAttribute("alertFilter", filter);
        uiModel.addAttribute("selectedMenuItem","ALL_ALERTS");
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

        initUIModel(uiModel, patientAlert);
        uiModel.addAttribute("referrerUrl", getReferrerUrl(request));
        return "alerts/update" + patientAlert.getAlert().getData().get(PatientAlert.PATIENT_ALERT_TYPE);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String update(Model uiModel, String alertId, String alertStatus, String notes, String doctorsNotes, String type, HttpServletRequest request) {

        Boolean isUpdatedSuccessfully = patientAlertService.updateAlertData(alertId, alertStatus, notes, doctorsNotes, type, loggedInUserId(request));
        uiModel.asMap().clear();

        PatientAlert patientAlert = patientAlertService.readAlert(alertId);
        initUIModel(uiModel, patientAlert);
        uiModel.addAttribute("referrerUrl", getReferrerUrl(request));
        uiModel.addAttribute("alertSaveStatus", isUpdatedSuccessfully.toString());

        return "alerts/update" + patientAlert.getAlert().getData().get(PatientAlert.PATIENT_ALERT_TYPE);
    }

    private void initUIModel(Model uiModel, PatientAlert patientAlert) {
        uiModel.addAttribute("alertInfo", patientAlert);
        uiModel.addAttribute("alertStatuses", Arrays.asList(TamaAlertStatus.values()));
    }

    private PatientAlerts getFilteredAlerts(AlertFilter filter, String clinicId) {
        if (AlertFilter.STATUS_OPEN.equals(filter.getAlertStatus())) {
            return patientAlertService.getUnreadAlertsFor(clinicId, filter.getPatientId(), filter.getPatientAlertType(), filter.getStartDateTime(), filter.getEndDateTime());
        } else if (AlertFilter.STATUS_CLOSED.equals(filter.getAlertStatus())) {
            return patientAlertService.getReadAlertsFor(clinicId, filter.getPatientId(), filter.getPatientAlertType(), filter.getStartDateTime(), filter.getEndDateTime());
        } else {
            return patientAlertService.getAllAlertsFor(clinicId, filter.getPatientId(), filter.getPatientAlertType(), filter.getStartDateTime(), filter.getEndDateTime());
        }
    }

    @RequestMapping(value = "**/closeAlert/{id}", method = RequestMethod.POST)
    public String closeAlert(@PathVariable String id, HttpServletRequest request) {
        updateAlert(id, request, TamaAlertStatus.Closed.name());

        String referrerUrl = getReferrerUrl(request);
        return "redirect:" + referrerUrl;
    }

    @RequestMapping(value = "**/openAlert/{id}", method = RequestMethod.POST)
    public String openAlert(@PathVariable String id, HttpServletRequest request) {
        updateAlert(id, request, TamaAlertStatus.Open.name());

        String referrerUrl = getReferrerUrl(request);
        return "redirect:" + referrerUrl;
    }

    private void updateAlert(String id, HttpServletRequest request, String alert) {
        patientAlertService.updateAlertStatus(id, loggedInUserId(request), alert);
    }

    private String getReferrerUrl(HttpServletRequest request) {
        String referrerUrl = request.getParameter("backPage");
        if (StringUtils.isBlank(referrerUrl))
            referrerUrl = request.getHeader("referer");

        String contextPath = request.getSession().getServletContext().getContextPath();

        String baseUrl = String.format(baseUrlFormat, request.getScheme(), request.getServerName(), request.getServerPort(), contextPath);
        referrerUrl = StringUtils.isBlank(referrerUrl) ? baseUrl : referrerUrl;

        return referrerUrl;
    }
}
