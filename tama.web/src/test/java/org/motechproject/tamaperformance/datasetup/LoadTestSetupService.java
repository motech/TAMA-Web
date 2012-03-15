package org.motechproject.tamaperformance.datasetup;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoadTestSetupService {

    @Autowired
    private ClinicanSetupService clinicanSetupService;
    @Autowired
    private PatientSetupService patientSetupService;

    public void createClinicians(int numberOfClinician) {
        clinicanSetupService.createClinicians(numberOfClinician);
    }

    public void createPatients(LocalDate today, int numberOfPatients) {
        patientSetupService.createPatients(today, numberOfPatients);
    }
}
