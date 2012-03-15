package org.motechproject.tama.patient.domain;


import ch.lambdaj.Lambda;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

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