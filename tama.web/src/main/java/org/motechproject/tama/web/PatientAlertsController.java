package org.motechproject.tama.web;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.web.view.AlertFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/patients/{patientDocumentId}/alerts")
@Controller
public class PatientAlertsController extends BaseController {


    private AlertsController alertsController;
    private AllPatients allPatients;

    @Autowired
    public PatientAlertsController(AlertsController alertsController, AllPatients allPatients) {
        this.alertsController = alertsController;
        this.allPatients = allPatients;
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String list(@PathVariable("patientDocumentId") String patientDocumentId, Model uiModel, HttpServletRequest request) {
        Patient patient = allPatients.findByIdAndClinicId(patientDocumentId, loggedInClinic(request));
        AlertFilter filter = initializeFilterForPatient(patient);

        String path = alertsController.list(filter, uiModel, request);
        setPatientInModel(uiModel, patient);
        return "patients/" + path;
    }

    @RequestMapping(value = "list/filter", method = RequestMethod.GET)
    public String list(@PathVariable("patientDocumentId") String patientDocumentId, AlertFilter filter, Model uiModel, HttpServletRequest request) {
        Patient patient = allPatients.findByIdAndClinicId(patientDocumentId, loggedInClinic(request));

        String path = alertsController.list(filter, uiModel, request);
        setPatientInModel(uiModel, patient);
        return "patients/" + path;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("patientDocumentId") String patientDocumentId, @PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
        Patient patient = allPatients.findByIdAndClinicId(patientDocumentId, loggedInClinic(request));

        String path = alertsController.show(id, uiModel, request);
        setPatientInModel(uiModel, patient);
        return "patients/" + path;
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("patientDocumentId") String patientDocumentId, @PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
        Patient patient = allPatients.findByIdAndClinicId(patientDocumentId, loggedInClinic(request));

        String path = alertsController.updateForm(id, uiModel, request);
        setPatientInModel(uiModel, patient);
        return "patients/" + path;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String update(@PathVariable("patientDocumentId") String patientDocumentId, Model uiModel, String alertId, String alertStatus, String notes, String doctorsNotes, String type, HttpServletRequest request) {
        Patient patient = allPatients.findByIdAndClinicId(patientDocumentId, loggedInClinic(request));

        String path = alertsController.update(uiModel, alertId, alertStatus, notes, doctorsNotes, type, request);
        setPatientInModel(uiModel, patient);
        return "patients/" + path;
    }

    @RequestMapping(value = "closeAlert/{id}", method = RequestMethod.POST)
    public String closeAlert(@PathVariable("patientDocumentId") String patientDocumentId, @PathVariable String id, Model uiModel, HttpServletRequest request) {
        Patient patient = allPatients.findByIdAndClinicId(patientDocumentId, loggedInClinic(request));

        String path = alertsController.closeAlert(id, request);
        setPatientInModel(uiModel, patient);
        return path;
    }

    @RequestMapping(value = "openAlert/{id}", method = RequestMethod.POST)
    public String openAlert(@PathVariable("patientDocumentId") String patientDocumentId, @PathVariable String id, Model uiModel, HttpServletRequest request) {
        Patient patient = allPatients.findByIdAndClinicId(patientDocumentId, loggedInClinic(request));

        String path = alertsController.openAlert(id, request);
        setPatientInModel(uiModel, patient);
        return path;
    }

    private AlertFilter initializeFilterForPatient(Patient patient) {
        AlertFilter filter = new AlertFilter();
        filter.setPatientId(patient.getPatientId());
        return filter;
    }

    private void setPatientInModel(Model uiModel, Patient patient) {
        uiModel.addAttribute("patient", patient);
    }
}
