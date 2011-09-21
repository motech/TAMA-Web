package org.motechproject.tama.web;

import org.motechproject.tama.repository.AllAlerts;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/alerts")
@Controller
public class AlertsController extends BaseController{
    private AllAlerts allAlerts = new AllAlerts();

    @RequestMapping(method = RequestMethod.GET)
    public String list(Model uiModel, HttpServletRequest request) {
        uiModel.addAttribute("alerts", allAlerts.forClinic(loggedInClinic(request)));
        return "alerts/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
        uiModel.addAttribute("alert", allAlerts.getAlert(id));

        return "alerts/show";
    }

}
