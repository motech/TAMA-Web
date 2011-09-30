package org.motechproject.tama.web;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.flatten;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectUnique;
import static ch.lambdaj.Lambda.sort;
import static java.util.Collections.reverseOrder;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.PatientAlert;
import org.motechproject.tama.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
@RequestMapping("/alerts")
@Controller
public class AlertsController extends BaseController {

    private AlertService alertService;

    @Autowired
    public AlertsController(AlertService alertService, AllPatients allPatients) {
        this.alertService = alertService;
        this.allPatients = allPatients;
    }

    private AllPatients allPatients;

    @RequestMapping(value = "/unread", method = RequestMethod.GET)
    public String unread(Model uiModel, HttpServletRequest request) {
        final String clinicId = loggedInClinic(request);
        final List<PatientAlert> alerts = getAlerts(clinicId, AlertStatus.NEW);
        uiModel.addAttribute("alerts", alerts);
        return "alerts/unread";
    }

    @RequestMapping(value = "/read", method = RequestMethod.GET)
    public String read(Model uiModel, HttpServletRequest request) {
        final String clinicId = loggedInClinic(request);
        final List<PatientAlert> alerts = getAlerts(clinicId, AlertStatus.READ);
        uiModel.addAttribute("alerts", alerts);
        return "alerts/read";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
        final Alert alert = selectUnique(alertService.getBy(null, null, null, null, 100),
                having(on(Alert.class).getId(),
                        equalTo(id)));

        alertService.changeStatus(alert.getId(), AlertStatus.READ);
        uiModel.addAttribute("alertInfo", PatientAlert.newPatientAlert(alert, allPatients.get(alert.getExternalId())));

        return "alerts/show";
    }


    private List<PatientAlert> getAlerts(String clinicId, final AlertStatus alertStatus) {
        final Converter<Patient, List<PatientAlert>> patientListConverter = new Converter<Patient, List<PatientAlert>>() {
            @Override
            public List<PatientAlert> convert(final Patient patient) {
                final Converter<Alert, PatientAlert> alertPatientAlertConverter = new Converter<Alert, PatientAlert>() {
                    @Override
                    public PatientAlert convert(Alert alert) {
                        return PatientAlert.newPatientAlert(alert, patient);
                    }
                };
                final List<PatientAlert> patientAlerts = Lambda.convert(alertService.getBy(patient.getId(), null, alertStatus, null, 100), alertPatientAlertConverter);
                return patientAlerts;
            }
        };
        return sort(flatten(convert(allPatients.findByClinic(clinicId), patientListConverter)), on(PatientAlert.class).getAlert().getDateTime(), reverseOrder());
    }

}
