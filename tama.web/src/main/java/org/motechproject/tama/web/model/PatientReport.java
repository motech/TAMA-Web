package org.motechproject.tama.web.model;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.refdata.domain.Regimen;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

public class PatientReport extends ModelAndView {

    public static class Summary {

        private Patient patient;
        private Regimen regimen;

        public Summary(Patient patient, Regimen regimen) {
            this.patient = patient;
            this.regimen = regimen;
        }

        public String getPatientId() {
            return patient.getPatientId();
        }

        public String getClinicName() {
            return patient.getClinic().getName();
        }

        public Date getARTStartDate() {
            return patient.getRegistrationDateAsDate();
        }

        public String getRegimenName() {
            return regimen.getDisplayName();
        }
    }

    public PatientReport(Patient patient, Regimen regimen, String viewName) {
        setViewName(viewName);
        addObject("summary", new Summary(patient, regimen));
    }
}
