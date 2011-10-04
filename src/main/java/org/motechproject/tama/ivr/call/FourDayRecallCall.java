package org.motechproject.tama.ivr.call;

import org.motechproject.server.service.ivr.IVRService;
import org.motechproject.tama.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class FourDayRecallCall extends IvrCall {
    @Autowired
    public FourDayRecallCall(IVRService callService, AllPatients allPatients) {
        super(allPatients, callService);
    }

    public void execute(String patientDocId) {
        makeCall(patientDocId, new HashMap<String, String>());
    }
}