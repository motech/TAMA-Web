package org.motechproject.tama.ivr.call;

import org.motechproject.server.service.ivr.IVRService;
import org.motechproject.tama.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FourDayRecallCall extends IvrCall {
    @Autowired
    public FourDayRecallCall(IVRService callService, AllPatients allPatients) {
        super(allPatients, callService);
    }

    public void execute(String patientId) {
        Map<String, String> params = new HashMap<String, String>() {{
        }};
        makeCall(patientId, params);
    }
}
