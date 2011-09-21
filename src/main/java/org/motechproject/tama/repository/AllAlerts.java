package org.motechproject.tama.repository;

import org.joda.time.DateTime;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class AllAlerts {


    public List<Alert> forClinic(String clinicId) {
        Alert alert1 = new Alert();
        alert1.setId("1234");
        alert1.setAlertType(AlertType.HIGH);
        alert1.setDateTime(new DateTime());
        alert1.setExternalId("123e");
        alert1.setName("Hello");
        alert1.setPriority(1);
        alert1.setStatus(AlertStatus.NEW);
        Alert alert2 = new Alert();
        alert2.setAlertType(AlertType.HIGH);
        alert2.setDateTime(new DateTime());
        alert2.setExternalId("123");
        alert2.setName("Hello");
        alert2.setId("1234");
        alert2.setPriority(1);
        alert2.setStatus(AlertStatus.NEW);
        Alert alert3 = new Alert();
        alert3.setAlertType(AlertType.HIGH);
        alert3.setDateTime(new DateTime());
        alert3.setExternalId("123");
        alert3.setId("123w4");
        alert3.setName("Hello");
        alert3.setPriority(1);
        alert3.setStatus(AlertStatus.NEW);
       return Arrays.asList(alert1, alert2, alert3);
    }

    public Alert getAlert(String alertId) {
        Alert alert3 = new Alert();
        alert3.setAlertType(AlertType.HIGH);
        alert3.setDateTime(new DateTime());
        alert3.setExternalId("123");
        alert3.setName("Hello");
        alert3.setPriority(1);
        alert3.setStatus(AlertStatus.NEW);
        return alert3;
    }
}
