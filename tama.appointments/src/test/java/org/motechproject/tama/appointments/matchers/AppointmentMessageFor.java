package org.motechproject.tama.appointments.matchers;

import org.apache.commons.collections.CollectionUtils;
import org.mockito.ArgumentMatcher;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.patient.domain.Patient;

import java.util.ArrayList;
import java.util.List;

public class AppointmentMessageFor extends ArgumentMatcher<List<String>> {

    private Patient patient;

    public AppointmentMessageFor(Patient patient) {
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
}
