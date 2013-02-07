package org.motechproject.tama.web.model;

import lombok.Data;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.util.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Data
public class FilterWithPatientIDAndDateRange {

    private String patientId;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    private LocalDate startDate;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    private LocalDate endDate;

    public FilterWithPatientIDAndDateRange() {
        startDate = DateUtil.today();
        endDate = startDate;
    }

    public boolean isMoreThanOneMonth() {
        return Days.daysBetween(startDate, endDate).getDays() > 30;
    }
}
