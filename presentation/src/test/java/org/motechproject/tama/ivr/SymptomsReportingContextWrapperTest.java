package org.motechproject.tama.ivr;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.util.Cookies;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomsReportingContextWrapperTest {
    @Mock
    private KooKooIVRContext kooKooIVRContext;
    @Mock
    private Cookies cookies;

    @Before
    public void setUp() {
        initMocks(this);
        when(kooKooIVRContext.cookies()).thenReturn(cookies);
    }

    @Test
    public void shouldSet1_AsThe_DefaultNumberOfCliniciansCalled() {
        when(cookies.getValue(SymptomsReportingContextWrapper.NUMBER_OF_CLINICIANS_CALLED)).thenReturn("");
        int numberOfCliniciansCalled = new SymptomsReportingContextWrapper(kooKooIVRContext).anotherClinicianCalled();

        assertEquals(1, numberOfCliniciansCalled);
    }

    @Test
    public void shouldIncrementTheNumberOfPatientsCalled() {
        when(cookies.getValue(SymptomsReportingContextWrapper.NUMBER_OF_CLINICIANS_CALLED)).thenReturn("1");
        int numberOfCliniciansCalled = new SymptomsReportingContextWrapper(kooKooIVRContext).anotherClinicianCalled();

        assertEquals(2, numberOfCliniciansCalled);
    }
}
