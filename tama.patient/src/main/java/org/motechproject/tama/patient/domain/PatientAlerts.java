package org.motechproject.tama.patient.domain;


import ch.lambdaj.Lambda;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.joda.time.DateTime;

import java.util.*;

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

    public PatientAlerts filterByAlertType(PatientAlertType patientAlertType) {
        ArrayList<PatientAlert> filteredAlerts = new ArrayList<PatientAlert>();
        CollectionUtils.select(this, getSelectorForAlertType(patientAlertType), filteredAlerts);
        return new PatientAlerts(filteredAlerts);
    }

    public PatientAlerts filterByClinic(String clinicId) {
        ArrayList<PatientAlert> filteredAlerts = new ArrayList<PatientAlert>();
        CollectionUtils.select(this, getSelectorForClinicId(clinicId), filteredAlerts);
        return new PatientAlerts(filteredAlerts);
    }

    public PatientAlerts sortByAlertStatusAndTimeOfAlert(){

        Comparator<PatientAlert> statusComparator = new Comparator<PatientAlert>() {
            @Override
            public int compare(PatientAlert one, PatientAlert two) {
                String alertStatusOne = one.getAlertStatus();
                String alertStatusTwo = two.getAlertStatus();

                if (alertStatusTwo == null && alertStatusOne == null) {
                    return 0;
                }

                if (alertStatusTwo == null ^ alertStatusOne == null) {
                    return (alertStatusTwo == null) ? -1 : 1;
                }

                return alertStatusTwo.compareTo(alertStatusOne);
            }
        };

        Comparator<PatientAlert> dateComparator = new Comparator<PatientAlert>() {
            @Override
            public int compare(PatientAlert one, PatientAlert two) {
                DateTime dateTimeOne = one.getGeneratedOnAsDateTime();
                DateTime dateTimeTwo = two.getGeneratedOnAsDateTime();

                if (dateTimeTwo == null && dateTimeOne == null) {
                    return 0;
                }

                if (dateTimeTwo == null ^ dateTimeOne == null) {
                    return (dateTimeTwo == null) ? -1 : 1;
                }

                return dateTimeTwo.compareTo(dateTimeOne);
            }
        };

        ComparatorChain comparatorChain = new ComparatorChain();
        comparatorChain.addComparator(statusComparator);
        comparatorChain.addComparator(dateComparator);
        Collections.sort(this, comparatorChain);
        return this;
    }

    private Predicate getSelectorForAlertType(final PatientAlertType patientAlertType) {
        return new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                PatientAlert patientAlert = (PatientAlert) o;
                return patientAlertType == null || (patientAlert.getAlert().getData() != null && patientAlertType.name().equals(patientAlert.getType().name()));
            }
        };
    }

    private Predicate getSelectorForClinicId(final String clinicId) {
        return new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                PatientAlert patientAlert = (PatientAlert) o;
                return patientAlert.getPatient().getClinic_id().equals(clinicId);
            }
        };
    }
}