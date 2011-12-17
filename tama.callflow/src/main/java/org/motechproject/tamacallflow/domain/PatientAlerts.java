package org.motechproject.tamacallflow.domain;


import ch.lambdaj.Lambda;
import org.apache.commons.collections.CollectionUtils;

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
}