package org.motechproject.tama.domain;


import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.util.DateUtil;

public class PatientAlertTest {

    @Test
    public void shouldFormatDateCorrectly() {
        Alert alert = new Alert();
        final DateTime dateTime = DateUtil.newDateTime(DateUtil.newDate(2011, 9, 26), 12, 5, 30);
        alert.setDateTime(dateTime);

        final PatientAlert patientAlert = new PatientAlert();
        patientAlert.setAlert(alert);
        Assert.assertEquals("26/09/2011 12:05 PM", patientAlert.getGeneratedOn());
    }

}
