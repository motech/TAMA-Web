package org.motechproject.tama.domain;

import org.joda.time.DateTime;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.util.DateUtil;

import java.util.HashMap;

public class Alerts {

    public static Alert forSymptomReporting(String externalId, Integer priority, String description, String adviceGiven, HashMap<String, String> data) {
        final Alert symptomsAlert = new Alert(externalId, AlertType.MEDIUM, AlertStatus.NEW, priority, data);
        final DateTime now = DateUtil.now();
        symptomsAlert.setDateTime(now);
        symptomsAlert.setDescription(description);
        symptomsAlert.setName(adviceGiven);
        return symptomsAlert;
    }

    public static Alert forFallingAdherence(String patientId) {
        final Alert alert = new Alert();
        alert.setExternalId(patientId);
        alert.setStatus(AlertStatus.NEW);
        alert.setDateTime(DateUtil.now());
        return alert;
    }
}
