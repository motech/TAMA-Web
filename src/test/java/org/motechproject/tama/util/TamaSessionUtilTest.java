package org.motechproject.tama.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.service.ivr.IVRSession;

import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TamaSessionUtilTest {

    private IVRSession ivrSession;
    @Mock
    private HttpSession session;

    @Before
    public void setUp() {
        initMocks(this);
        ivrSession = new IVRSession(session);
    }

    @Test
    public void shouldReturnCallType() {
        when(session.getAttribute(TamaSessionUtil.TamaSessionAttribute.SYMPTOMS_REPORTING_PARAM)).thenReturn("true");
        Assert.assertTrue(TamaSessionUtil.isSymptomsReportingCall(ivrSession));
    }
}
