package org.motechproject.tama.clinicvisits.domain.criteria;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class VisitMissedAlertCriteriaTest {
    @Mock
    private ClinicVisit clinicVisit;
    @Mock
    private VisitMissedAlertCriteria visitMissedAlertCriteria;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        visitMissedAlertCriteria = new VisitMissedAlertCriteria();
    }

    @Test
    public void shouldReturnTrueWhenVisitMissed() throws Exception {
        when(clinicVisit.getConfirmedAppointmentDate()).thenReturn(DateUtil.newDateTime(DateUtil.today().minusDays(1)));
        when(clinicVisit.getVisitDate()).thenReturn(null);
        assertTrue(visitMissedAlertCriteria.shouldRaiseAlert(clinicVisit));
    } 
    
    @Test
    public void shouldReturnFalseWhenVisited() throws Exception {
        when(clinicVisit.getConfirmedAppointmentDate()).thenReturn(DateUtil.now());
        when(clinicVisit.getVisitDate()).thenReturn(DateUtil.now());
        assertFalse(visitMissedAlertCriteria.shouldRaiseAlert(clinicVisit));
    }
}
