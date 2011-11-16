package org.motechproject.tama.ivr.context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.util.Cookies;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomsReportingContextTest {
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
        when(cookies.getValue(SymptomsReportingContext.NUMBER_OF_CLINICIANS_CALLED)).thenReturn("");
        SymptomsReportingContext symptomsReportingContext = new SymptomsReportingContext(kooKooIVRContext);

        assertEquals(0, symptomsReportingContext.numberOfCliniciansCalled());
        assertEquals(1, symptomsReportingContext.anotherClinicianCalled());
    }

    @Test
    public void shouldIncrementTheNumberOfPatientsCalled() {
        when(cookies.getValue(SymptomsReportingContext.NUMBER_OF_CLINICIANS_CALLED)).thenReturn("1");
        SymptomsReportingContext symptomsReportingContext = new SymptomsReportingContext(kooKooIVRContext);

        assertEquals(1, symptomsReportingContext.numberOfCliniciansCalled());
        assertEquals(2, symptomsReportingContext.anotherClinicianCalled());
    }
}
