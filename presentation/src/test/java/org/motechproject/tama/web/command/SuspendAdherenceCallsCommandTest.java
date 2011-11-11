package org.motechproject.tama.web.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.service.PatientService;

import static junitx.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SuspendAdherenceCallsCommandTest {
    @Mock
    private AllPatients allPatients;
    @Mock
    private PatientService patientService;
    @Mock
    private TAMAIVRContextFactory contextFactory;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldChangeStatusToSuspended(){
        final Patient patient = new Patient();
        final TAMAIVRContextForTest tamaivrContextForTest = new TAMAIVRContextForTest();
        tamaivrContextForTest.patient(patient);
        when(contextFactory.create(Matchers.<KooKooIVRContext>any())).thenReturn(tamaivrContextForTest);
        final SuspendAdherenceCallsCommand suspendAdherenceCallsCommand = new SuspendAdherenceCallsCommand(allPatients, patientService, contextFactory);

        suspendAdherenceCallsCommand.execute(null);

        assertEquals(Patient.Status.Suspended, patient.getStatus());
        verify(patientService).update(patient);
    }
}
