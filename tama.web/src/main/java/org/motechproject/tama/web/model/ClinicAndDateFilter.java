package org.motechproject.tama.web.model;

import lombok.Data;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Data
public class ClinicAndDateFilter {

    private DateFilter dateFilter = new DateFilter();
    private String clinicId;

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
