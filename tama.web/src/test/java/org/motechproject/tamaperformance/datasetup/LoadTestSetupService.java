package org.motechproject.tamaperformance.datasetup;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tamadatasetup.service.TAMADateTimeService;
import org.motechproject.tamafunctionalframework.framework.MyWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoadTestSetupService {

    @Autowired
    private ClinicanSetupService clinicanSetupService;
    @Autowired
    private PatientSetupService patientSetupService;
    @Autowired
    private CallLogSetupService callLogSetupService;

    public void createClinicians(int numberOfClinician) {
        clinicanSetupService.createClinicians(numberOfClinician);
    }

    public void createPatients(LocalDate today, int numberOfPatients) {
        patientSetupService.createPatients(today, numberOfPatients);
    }

    public void createCallLogs(TAMADateTimeService tamaDateTimeService, MyWebClient webClient, DateTime startDate, int numberOfDays) {
        callLogSetupService.createCallLogs(webClient, tamaDateTimeService, startDate, numberOfDays);
    }
}
