package org.motechproject.tama.web;

import org.motechproject.tama.repository.AllSymptomReportingAlerts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
@RequestMapping("/alerts")
@Controller
public class AlertsController extends BaseController {

    private AllSymptomReportingAlerts allSymptomReportingAlerts;

    @Autowired
    public AlertsController(AllSymptomReportingAlerts allSymptomReportingAlerts) {
        this.allSymptomReportingAlerts = allSymptomReportingAlerts;
    }

    @RequestMapping(value = "/unread", method = RequestMethod.GET)
    public String unread(Model uiModel, HttpServletRequest request) {
        final String clinicId = loggedInClinic(request);
        uiModel.addAttribute("alerts", allSymptomReportingAlerts.getUnreadAlertsForClinic(clinicId));
        return "alerts/unread";
    }

    @RequestMapping(value = "/read", method = RequestMethod.GET)
    public String read(Model uiModel, HttpServletRequest request) {
        final String clinicId = loggedInClinic(request);
        uiModel.addAttribute("alerts", allSymptomReportingAlerts.getReadAlertsForClinic(clinicId));
        return "alerts/read";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
        uiModel.addAttribute("alertInfo", allSymptomReportingAlerts.getSymptomReportingAlert(id));
        return "alerts/show";
    }

}
