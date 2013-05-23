package org.motechproject.tama.web.model;


import lombok.Data;
import org.joda.time.LocalDate;

import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.PatientAlertType;

import org.motechproject.tama.patient.domain.TamaAlertStatus;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.List;

@Data
public class PatientAlertsReportFilter {

    private DateFilter dateFilter = new DateFilter();

    private String clinicId;

    private String patientAlertType;

    private String patientAlertStatus;

    public List<String> getAllPatientAlertType() {
        List<String> alertType = new ArrayList<>();
        alertType.add("Any");
        for (PatientAlertType patientAlertType : PatientAlertType.values()) {
            alertType.add(patientAlertType.getDisplayName());
        }
        return alertType;
    }

    public List<String> getAllPatientAlertStatus() {

        List<String> alertStatus = new ArrayList<>();
        alertStatus.add("Any");
        alertStatus.add(TamaAlertStatus.Open.toString());
        alertStatus.add(TamaAlertStatus.Closed.toString());
        return alertStatus;
    }


    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public LocalDate getStartDate() {
        return dateFilter.getStartDate();
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public LocalDate getEndDate() {
        return dateFilter.getEndDate();
    }

    public void setStartDate(LocalDate startDate) {
        dateFilter.setStartDate(startDate);
    }

    public void setEndDate(LocalDate endDate) {
        dateFilter.setEndDate(endDate);
    }
}
