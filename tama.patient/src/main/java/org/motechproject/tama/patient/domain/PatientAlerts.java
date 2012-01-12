package org.motechproject.tama.patient.domain;


import ch.lambdaj.Lambda;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static java.util.Collections.reverseOrder;

public class PatientAlerts extends ArrayList<PatientAlert> {

    public PatientAlerts() {
    }

    public PatientAlerts(Collection<? extends PatientAlert> patientAlerts) {
        super(patientAlerts);
    }

    public PatientAlert lastSymptomReportedAlert() {
        List<PatientAlert> filterCriteria = filter(Lambda.<PatientAlert>having(on(PatientAlert.class).isSymptomReportingAlert()), this);
        List<Object> objectList = sort(filterCriteria, on(PatientAlert.class).getAlert().getDateTime(), reverseOrder());
        return CollectionUtils.isEmpty(objectList) ? null : (PatientAlert) objectList.get(0);
    }

    public PatientAlerts filterByAlertTypeAndDateRange(PatientAlertType patientAlertType, DateTime startDate, DateTime endDate) {
        Predicate selectorForAlertTypeAndDateRange = getSelectorForAlertTypeAndDateRange(patientAlertType, startDate, endDate);
        ArrayList<PatientAlert> filteredAlerts = new ArrayList<PatientAlert>();
        CollectionUtils.select(this, selectorForAlertTypeAndDateRange, filteredAlerts);
        return new PatientAlerts(filteredAlerts);
    }

    public PatientAlerts filterByAlertTypeAndDateRangeIfPresent(PatientAlertType patientAlertType, DateTime startDate, DateTime endDate) {
        Predicate selectorForAlertTypeAndDateRangeIfPresent = getSelectorForAlertTypeAndDateRangeIfPresent(patientAlertType, startDate, endDate);
        ArrayList<PatientAlert> filteredAlerts = new ArrayList<PatientAlert>();
        CollectionUtils.select(this, selectorForAlertTypeAndDateRangeIfPresent, filteredAlerts);
        return new PatientAlerts(filteredAlerts);
    }

    private Predicate getSelectorForAlertTypeAndDateRange(final PatientAlertType patientAlertType, final DateTime startDate, final DateTime endDate) {
        return new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                PatientAlert patientAlert = (PatientAlert) o;
                DateTime alertTime = patientAlert.getAlert().getDateTime();
                boolean isOfRequiredAlertType = patientAlert.getAlert().getData() != null && patientAlertType.name().equals(patientAlert.getAlert().getData().get(PatientAlert.PATIENT_ALERT_TYPE));
                return isOfRequiredAlertType && alertTime.isAfter(startDate) && alertTime.isBefore(endDate);
            }
        };
    }

    private Predicate getSelectorForAlertTypeAndDateRangeIfPresent(final PatientAlertType patientAlertType, final DateTime startDate, final DateTime endDate) {
        return new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                PatientAlert patientAlert = (PatientAlert) o;
                DateTime alertTime = patientAlert.getAlert().getDateTime();
                boolean isOfRequiredAlertType = patientAlertType == null || (patientAlert.getAlert().getData() != null && patientAlertType.name().equals(patientAlert.getAlert().getData().get(PatientAlert.PATIENT_ALERT_TYPE)));
                boolean isAfterStartDate = startDate == null || alertTime.isAfter(startDate);
                boolean isBeforeEndDate = endDate == null || !alertTime.toLocalDate().isAfter(endDate.toLocalDate());
                return isOfRequiredAlertType && isAfterStartDate && isBeforeEndDate;
            }
        };
    }
}