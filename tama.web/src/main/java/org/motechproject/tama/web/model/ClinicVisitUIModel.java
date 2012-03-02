package org.motechproject.tama.web.model;

import org.joda.time.DateTime;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.util.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public class ClinicVisitUIModel {

    private ClinicVisit clinicVisit;
    private DateTime visitDate;

    public ClinicVisitUIModel() {
    }

    public ClinicVisitUIModel(ClinicVisit clinicVisit) {
        this.clinicVisit = clinicVisit;
        this.visitDate = clinicVisit.getVisitDate();
    }

    public ClinicVisit getClinicVisit() {
        return clinicVisit;
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public DateTime getVisitDate() {
        return visitDate == null ? DateUtil.now() : visitDate;
    }

    public void setVisitDate(DateTime visitDate) {
        this.visitDate = visitDate;
    }
}
