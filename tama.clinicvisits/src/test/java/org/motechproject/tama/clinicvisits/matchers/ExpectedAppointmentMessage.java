package org.motechproject.tama.clinicvisits.matchers;

import org.apache.commons.collections.CollectionUtils;
import org.mockito.ArgumentMatcher;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.patient.domain.Patient;

import java.util.ArrayList;
import java.util.List;

public class ExpectedAppointmentMessage extends ArgumentMatcher<List<String>> {

    private Patient patient;

    private ExpectedAppointmentMessage(Patient patient) {
        this.patient = patient;
    }

    @Override
    public boolean matches(Object argument) {
        if (argument instanceof List) {
            List<String> audios = (List<String>) argument;

            List<String> expectedWavFiles = new ArrayList<String>();
            expectedWavFiles.add("M07a_01_yourNextClinicVisitDue1");
            expectedWavFiles.addAll(TamaIVRMessage.getAllNumberFileNames("0" + patient.getClinic().getPhone()));
            expectedWavFiles.add("M07a_03_yourNextClinicVisitDue2");

            return CollectionUtils.disjunction(expectedWavFiles, audios).isEmpty();
        }
        return false;
    }

    public static ExpectedAppointmentMessage For(Patient patient){
        return new ExpectedAppointmentMessage(patient);
    }
}
