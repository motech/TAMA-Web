package org.motechproject.tamacallflow.ivr.command;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamadomain.domain.Status;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.tamacallflow.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tamacallflow.service.PatientService;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
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
        when(DateUtil.setTimeZone(any(DateTime.class))).thenReturn(new DateTime(2011, 11, 11, 0, 0, 0));

        final SuspendAdherenceCallsCommand suspendAdherenceCallsCommand = new SuspendAdherenceCallsCommand(allPatients, patientService, contextFactory);
        suspendAdherenceCallsCommand.execute(null);

        Assert.assertEquals(Status.Suspended, patient.getStatus());
        Assert.assertEquals(suspendedDateTime, patient.getLastSuspendedDate());
        verify(patientService).update(patient);
    }
}
