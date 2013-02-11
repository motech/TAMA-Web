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

    public static final int DAYS_IN_A_YEAR = 365;
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

    public boolean isMoreThanOneYear() {
        return Days.daysBetween(startDate, endDate).getDays() > DAYS_IN_A_YEAR;
    }
}
