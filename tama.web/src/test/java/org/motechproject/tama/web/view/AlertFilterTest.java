package org.motechproject.tama.web.view;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.motechproject.tama.patient.domain.PatientAlertType;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class AlertFilterTest {
    @Test
    public void shouldReturnAllPatientAlertTypes(){
        List<String> allPatientAlertTypes = new AlertFilter().getAllPatientAlertTypes();
        assertTrue(CollectionUtils.isNotEmpty(allPatientAlertTypes));
        assertEquals("Any", allPatientAlertTypes.get(0));
        assertEquals(PatientAlertType.values().length + 1, allPatientAlertTypes.size());
    }

    @Test
    public void shouldReturnNullIfAlertTypeIsEmpty(){
        AlertFilter alertFilter = new AlertFilter().setAlertType("");
        assertNull(alertFilter.getPatientAlertType());
    }

    @Test
    public void shouldReturnNullIfAlertTypeIsAny(){
        AlertFilter alertFilter = new AlertFilter().setAlertType("Any");
        assertNull(alertFilter.getPatientAlertType());
    }

    @Test
    public void shouldReturnPatientAlertType(){
        AlertFilter alertFilter = new AlertFilter().setAlertType(PatientAlertType.AdherenceInRed.toString());
        assertEquals(PatientAlertType.AdherenceInRed, alertFilter.getPatientAlertType());
    }
}
