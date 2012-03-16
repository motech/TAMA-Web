package org.motechproject.tamaperformance.datasetup;

import org.joda.time.DateTime;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tamadatasetup.service.TAMADateTimeService;
import org.motechproject.tamafunctionalframework.framework.MyWebClient;
import org.motechproject.tamafunctionalframework.ivr.Caller;
import org.motechproject.tamafunctionalframework.testdata.ivrreponse.IVRResponse;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CallLogSetupService {

    @Autowired
    AllPatients allPatients;

    public void createCallLogs(MyWebClient webClient, TAMADateTimeService tamaDateTimeService, DateTime startDate, int numberOfDays) {
        List<Patient> patients = allPatients.getAll();
        for (int dayNumber = 1; dayNumber <= numberOfDays; dayNumber++) {
            for (Patient patient : patients) {
                patientConfirmsDoseAsTaken(webClient, patient);
            }
            startDate = startDate.plusDays(1);
            tamaDateTimeService.adjustDateTime(startDate);
        }
    }

    private void patientConfirmsDoseAsTaken(MyWebClient webClient, Patient patient) {
        Caller caller = new Caller(unique("sid"), patient.getMobilePhoneNumber(), webClient);
        caller.call();

        IVRResponse ivrResponse = caller.enter(patient.getPatientPreferences().getPasscode());
        ivrResponse.collectDtmf();
        caller.enter("1");
        caller.hangup();
    }

    protected String unique(String name) {
        return name + DateUtil.now().toInstant().getMillis() + Math.random();
    }
}
