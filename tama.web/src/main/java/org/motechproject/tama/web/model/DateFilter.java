package org.motechproject.tama.web.model;

import lombok.Data;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import static org.motechproject.util.DateUtil.today;

@Data
public class DateFilter {

    public static final int DAYS_IN_A_YEAR = 365;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public LocalDate startDate;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public LocalDate endDate;

    public DateFilter() {
        startDate = today();
        endDate = today();
    }

    public boolean isMoreThanOneYear() {
        return Days.daysBetween(startDate, endDate).getDays() > DAYS_IN_A_YEAR;
    }

    public DateFilter setDates(LocalDate startDate, LocalDate endDate) {
        setStartDate(startDate);
        setEndDate(endDate);
        return this;
    }
}
