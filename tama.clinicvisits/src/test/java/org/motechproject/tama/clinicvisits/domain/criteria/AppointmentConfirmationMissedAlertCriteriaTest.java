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

public class AppointmentConfirmationMissedAlertCriteriaTest {
    @Mock
    private ClinicVisit clinicVisit;
    AppointmentConfirmationMissedAlertCriteria appointmentConfirmationMissedAlertCriteria;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        appointmentConfirmationMissedAlertCriteria = new AppointmentConfirmationMissedAlertCriteria();
    }

    @Test
    public void shouldReturnTrueForAppointmentConfirmationMiss() throws Exception {
        when(clinicVisit.getConfirmedAppointmentDate()).thenReturn(null);
        when(clinicVisit.getEffectiveDueDate()).thenReturn(DateUtil.today());
        assertTrue(appointmentConfirmationMissedAlertCriteria.shouldRaiseAlert(clinicVisit));

    }
    @Test
    public void shouldReturnFalseForAppointmentConfirmationMiss() throws Exception {
        when(clinicVisit.getConfirmedAppointmentDate()).thenReturn(DateUtil.now());
        when(clinicVisit.getEffectiveDueDate()).thenReturn(DateUtil.today());
        assertFalse(appointmentConfirmationMissedAlertCriteria.shouldRaiseAlert(clinicVisit));

    }

    @Test
    public void shouldReturnFalseBeforeDueDate() throws Exception {
        when(clinicVisit.getConfirmedAppointmentDate()).thenReturn(null);
        when(clinicVisit.getEffectiveDueDate()).thenReturn(DateUtil.today().minusDays(1));
        assertFalse(appointmentConfirmationMissedAlertCriteria.shouldRaiseAlert(clinicVisit));

    }
}
