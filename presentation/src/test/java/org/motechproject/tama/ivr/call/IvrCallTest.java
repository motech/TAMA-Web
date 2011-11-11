package org.motechproject.tama.ivr.call;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.server.service.ivr.CallRequest;
import org.motechproject.server.service.ivr.IVRService;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.AllPatients;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class IvrCallTest {
    @Mock
    private IVRService ivrService;
    @Mock
    private AllPatients allPatients;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldNotMakeCallWhenPatientIsSuspended() {
        final Patient patient = new Patient();
        patient.setStatus(Patient.Status.Suspended);
        when(allPatients.get("patient_doc_id")).thenReturn(patient);

        new IvrCall(allPatients, ivrService, null).makeCall("patient_doc_id");
        verify(ivrService, never()).initiateCall(Matchers.<CallRequest>any());
    }
}
