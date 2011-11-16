package org.motechproject.tama.repository;

import ch.lambdaj.function.convert.Converter;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.tama.domain.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.flatten;

@Repository
public class AllAlerts {

    private AlertService alertService;

    private AllPatients allPatients;

    @Autowired
    public AllAlerts(AlertService alertService, AllPatients allPatients) {
        this.alertService = alertService;
        this.allPatients = allPatients;
    }

    public List<Alert> forClinic(String clinicId) {
        return flatten(convert(allPatients.findByClinic(clinicId), new Converter<Patient, List<Alert>>() {
            @Override
            public List<Alert> convert(Patient patient) {
                    return alertService.getBy(patient.getId(), null,null,null,0);
            }
        }));
    }

    public Alert getAlert(String alertId) {
        return null;
    }
}
