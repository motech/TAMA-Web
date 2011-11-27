package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.Status;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.service.PatientService;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junitx.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
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
        mockStatic(DateUtil.class);
    }

    @Test
    public void shouldChangeStatusToSuspended(){
        final Patient patient = new Patient();
        final TAMAIVRContextForTest tamaivrContextForTest = new TAMAIVRContextForTest();
        tamaivrContextForTest.patient(patient);

        when(contextFactory.create(Matchers.<KooKooIVRContext>any())).thenReturn(tamaivrContextForTest);
        DateTime suspendedDateTime = new DateTime(2011, 11, 11, 0, 0, 0);
        when(DateUtil.now()).thenReturn(suspendedDateTime);

        final SuspendAdherenceCallsCommand suspendAdherenceCallsCommand = new SuspendAdherenceCallsCommand(allPatients, patientService, contextFactory);
        suspendAdherenceCallsCommand.execute(null);

        assertEquals(Status.Suspended, patient.getStatus());
        assertEquals(suspendedDateTime, patient.getLastSuspendedDate());
        verify(patientService).update(patient);
    }
}
