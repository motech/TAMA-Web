package org.motechproject.tama.web.view;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.motechproject.tama.patient.domain.PatientAlertType;
import org.motechproject.util.DateUtil;

import java.util.List;

import static junit.framework.Assert.*;

public class AlertFilterTest {
    @Test
    public void shouldReturnAllPatientAlertTypes() {
        List<String> allPatientAlertTypes = new AlertFilter().getAllPatientAlertTypes();
        assertTrue(CollectionUtils.isNotEmpty(allPatientAlertTypes));
        assertEquals("Any", allPatientAlertTypes.get(0));
        assertEquals(PatientAlertType.values().length + 1, allPatientAlertTypes.size());
    }

    @Test
    public void shouldReturnAllAlertStatuses() {
        List<String> allAlertStatuses = new AlertFilter().getAllAlertStatuses();
        assertTrue(CollectionUtils.isNotEmpty(allAlertStatuses));
        assertEquals(AlertFilter.STATUS_UNREAD, allAlertStatuses.get(0));
        assertEquals(AlertFilter.STATUS_READ, allAlertStatuses.get(1));
    }

    @Test
    public void shouldReturnNullIfAlertTypeIsEmpty() {
        AlertFilter alertFilter = new AlertFilter().setAlertType("");
        assertNull(alertFilter.getPatientAlertType());
    }

    @Test
    public void shouldReturnNullIfAlertTypeIsAny() {
        AlertFilter alertFilter = new AlertFilter().setAlertType("Any");
        assertNull(alertFilter.getPatientAlertType());
    }

    @Test
    public void shouldSetStartDateToSOD_ToSearchAllAlerts_RaisedAfter_TheStartDateTime() {
        AlertFilter alertFilter = new AlertFilter().setEndDate(DateUtil.now().toDate());
        assertEquals(DateUtil.newDateTime(DateUtil.today(), 0, 0, 0), alertFilter.getStartDateTime());
    }

    @Test
    public void shouldSetEndDateToEOD_ToSearchAllAlerts_RaisedUntil_TheEndDateTime() {
        AlertFilter alertFilter = new AlertFilter().setEndDate(DateUtil.now().toDate());
        assertEquals(DateUtil.newDateTime(DateUtil.today(), 23, 59, 59), alertFilter.getEndDateTime());
    }

    @Test
    public void shouldReturnPatientAlertType() {
        AlertFilter alertFilter = new AlertFilter().setAlertType(PatientAlertType.AdherenceInRed.toString());
        assertEquals(PatientAlertType.AdherenceInRed, alertFilter.getPatientAlertType());
    }
}
